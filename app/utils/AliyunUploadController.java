package utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 文件上传
 */
public class AliyunUploadController extends BaseController {
    Logger.ALogger logger = Logger.of(AliyunUploadController.class);
    private static final String END_POINT = "https://oss-cn-hangzhou.aliyuncs.com";
    private static final String END_POINT2 = "https://oss-accelerate.aliyuncs.com";
    private static String bucketName = "q-files";
    public static String IMG_URL_PREFIX = "https://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/";

    public CompletionStage<Result> upload(Http.Request request) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> uploadFile = body.getFile("file");
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (null == uploadFile) return okCustomJson(CODE500, "上传文件失败，请重试");
                String fileName = uploadFile.getFilename();
                long fileSize = uploadFile.getFileSize();
                if (fileSize > 6 * 1024 * 1024) return okCustomJson(CODE40005, "系统限制上传文件的大小最多6M");
                Files.TemporaryFile file = uploadFile.getRef();
                String today = dateUtils.getToday();
                String targetFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
                String key = "gy/" + today + "/" + targetFileName;
                String destPath = "/tmp/upload/" + targetFileName;
                file.copyTo(Paths.get(destPath), true);
                File destFile = new File(destPath);
                return uploadToOss(destFile, key);
            } catch (Exception e) {
                return okCustomJson(40003, "reason:" + e.getMessage());
            }
        });

    }

    public Result uploadToOss(File file, String key) {
        OSS client = new OSSClientBuilder().build(END_POINT, businessUtils.getAlinYunAccessId(), businessUtils.getAliYunSecretKey());
        try {
            client.putObject(new PutObjectRequest(bucketName, key, file));
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.put("url", IMG_URL_PREFIX + key);
            return ok(node);
        } catch (OSSException oe) {
            logger.error("uploadToOss:" + oe.getMessage());
            return okCustomJson(CODE500, oe.getMessage());
        } finally {
            client.shutdown();
        }
    }

    public Result uploadToOss2(File file, String key) {
        OSS client = new OSSClientBuilder().build(END_POINT, businessUtils.getAlinYunAccessId(), businessUtils.getAliYunSecretKey());
        try {
            client.putObject(new PutObjectRequest(bucketName, key, file));
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.put("url", IMG_URL_PREFIX + key);
            return ok(node);
        } catch (OSSException oe) {
            logger.error("uploadToOss:" + oe.getMessage());
            return okCustomJson(CODE500, oe.getMessage());
        } finally {
            client.shutdown();
        }
    }


    public String uploadToOss(ByteArrayInputStream buffer, String key) {
        OSS client = new OSSClientBuilder().build(END_POINT2, businessUtils.getAlinYunAccessId(), businessUtils.getAliYunSecretKey());
        try {
            String keyPath = "static/" + key;
            client.putObject(new PutObjectRequest(bucketName, keyPath, buffer));
            client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            String url = IMG_URL_PREFIX + keyPath;
            return url;
        } catch (OSSException oe) {
            logger.error("uploadToOss:" + oe.getMessage());
        } finally {
            client.shutdown();
        }
        return "";
    }
}
