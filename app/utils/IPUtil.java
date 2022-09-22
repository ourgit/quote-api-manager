package utils;

import play.Environment;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

/**
 * IP归属地辅助类
 */
@Singleton
public class IPUtil {
    @Inject
    private Environment play;

    private static boolean dbloaded = false;

    Logger.ALogger logger = Logger.of(IPUtil.class);

    public String getCityByIp(String ip) {
        try {
            if (ValidationUtil.isEmpty(ip)) return "未知ip";
            loadIpDb();
            String[] result = IP.find(ip);//返回字符串数组 中国 福建  福州
            StringBuilder sb = new StringBuilder();
            if (null != result) {
                for (int i = 0; i < result.length; i++) {
                    sb.append(result[i]);
                }
            }
            return sb.toString().trim();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
    }


    public String getProvinceByIp(String ip) {
        try {
            if (ValidationUtil.isEmpty(ip)) return "未知ip";
            loadIpDb();
            String[] result = IP.find(ip);//返回字符串数组 中国 福建  福州
            if (null != result && result.length > 0) {
                return result[1];
            }
            return "";
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
    }


    private void loadIpDb() {
        if (!dbloaded) {
            IP.enableFileWatch = true; // 默认值为：false，如果为true将会检查ip库文件的变化自动reload数据
            String ipFile = play.rootPath().getAbsolutePath() + File.separator + "conf" + File.separator + "ipdb.dat";
            logger.info("load ipFile:" + ipFile);
            IP.load(ipFile);
            dbloaded = true;
        }
    }


}
