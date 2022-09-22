package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import myannotation.Json1MParser;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import play.libs.Files;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.AliyunUploadController;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static utils.AliyunUploadController.IMG_URL_PREFIX;

/**
 * 获取七牛token
 */
public class UploaderManager extends BaseController {

    Logger.ALogger logger = Logger.of(UploaderManager.class);
    @Inject
    AliyunUploadController uploadController;

    public static final int THUMB_WIDTH = 400;
    public static final int THUMB_HEIGHT = 400;
    private static final int AVATAR_WEIGHT = 80;
    private static final int AVATAR_HEIGHT = 80;
    private static final double THUMB_QUALITY = 0.8;
    private static final String BASE_IMG_PREFIX = "data:image/jpeg;base64,";
    private static final String RENAME_FILE_PREFIX = "upload_";

    /**
     * @api {POST} /v1/cp/upload/ 上传
     * @apiName upload
     * @apiGroup System
     * @apiSuccess (Success 200){int}code 200
     * @apiSuccess (Success 200){string} url 保存的地址
     * @apiSuccess (Error 40003) {int}code 40003 上传失败
     */
    public CompletionStage<Result> upload(Http.Request request) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> uploadFile = body.getFile("file");
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (null == uploadFile) return okCustomJson(CODE500, "上传文件失败，请重试");
                String fileName = uploadFile.getFilename();
                Files.TemporaryFile file = uploadFile.getRef();
                String today = dateUtils.getToday();
                String targetFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
                String key = "gy/" + today + "/" + targetFileName;
                String destPath = "/tmp/upload/" + targetFileName;
                file.copyTo(Paths.get(destPath), true);
                File destFile = new File(destPath);
                return uploadController.uploadToOss(destFile, key);
            } catch (Exception e) {
                return okCustomJson(40003, "reason:" + e.getMessage());
            }
        });
    }

    public CompletionStage<Result> upload2(Http.Request request) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> uploadFile = body.getFile("file");
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (null == uploadFile) return okCustomJson(CODE500, "上传文件失败，请重试");
                String fileName = uploadFile.getFilename();
                Files.TemporaryFile file = uploadFile.getRef();
                String today = dateUtils.getToday();
                String targetFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
                String key = "gy/" + today + "/" + targetFileName;
                String destPath = "/tmp/upload/" + targetFileName;
                file.copyTo(Paths.get(destPath), true);
                File destFile = new File(destPath);
                return uploadController.uploadToOss2(destFile, fileName);
            } catch (Exception e) {
                return okCustomJson(40003, "reason:" + e.getMessage());
            }
        });

    }

    /**
     * @api {POST} /v1/cp/upload_resize/ 03上传并生成缩略图
     * @apiName uploadAndResize
     * @apiGroup System
     * @apiSuccess (Success 200){int}code 200
     * @apiSuccess (Success 200){string} big_url 原图保存的地址
     * @apiSuccess (Success 200){string} thumbnail_url 缩略图保存的地址
     * @apiSuccess (Error 40003) {int}code 40003 上传失败
     */
    public CompletionStage<Result> uploadAndResize(Http.Request request) {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> uploadFile = body.getFile("file");
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (null == uploadFile) return okCustomJson(CODE500, "上传文件失败，请重试");
                String fileName = uploadFile.getFilename();
                Files.TemporaryFile file = uploadFile.getRef();
                String today = dateUtils.getToday();
                String targetFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
                String key = "gy/" + today + "/" + targetFileName;
                String destPath = "/tmp/upload/" + targetFileName;
                file.copyTo(Paths.get(destPath), true);
                File destFile = new File(destPath);

                String thumbKey = UUID.randomUUID() + "_thumb." + FilenameUtils.getExtension(fileName);
                String thumbFilePath = "/tmp/upload/" + thumbKey;
                File thumbFile = new File(thumbFilePath);
                Thumbnails.of(destFile)
                        .size(THUMB_WIDTH, THUMB_HEIGHT)
                        .keepAspectRatio(false)
                        .outputQuality(THUMB_QUALITY)
                        .toFile(thumbFile);

                ObjectNode node = Json.newObject();
                node.put("code", 200);
                node.put("big_url", IMG_URL_PREFIX + key);
                node.put("thumbnail_url", IMG_URL_PREFIX + thumbKey);

                uploadController.uploadToOss(destFile, key);
                uploadController.uploadToOss(thumbFile, thumbKey);
                return ok(node);
            } catch (Exception e) {
                logger.error("uploadToOss:" + e.getMessage());
                return okCustomJson(40003, "reason:" + e.getMessage());
            }
        });
    }



    /**
     * @api {POST}  /v1/cp/upload_base64/  04上传base64
     * @apiName uploadBase64
     * @apiGroup System
     * @apiSuccess (Success 200){int}code 200
     * @apiSuccess (Success 200){string} imgUrl 上传返回的地址
     * @apiSuccess (Error 40003) {int}code 40003 上传失败
     */
    @BodyParser.Of(Json1MParser.class)
    public CompletionStage<Result> uploadBase64(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            String bytes = requestNode.findPath("data").asText();
            int index = bytes.indexOf(BASE_IMG_PREFIX);
            if (index >= 0) {
                String sub = bytes.substring(BASE_IMG_PREFIX.length());
                byte[] decoderBytes = Base64.getDecoder().decode(sub);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decoderBytes);
                String key = "poster_" + System.currentTimeMillis() + ".jpeg";
                String imgUrl = uploadController.uploadToOss(byteArrayInputStream, key);
                ObjectNode node = Json.newObject();
                node.put("code", 200);
                node.put("imgUrl", imgUrl);
                return ok(node);
            }
            return okCustomJson(CODE500, "上传发生异常，请稍后再试");
        });
    }

}
