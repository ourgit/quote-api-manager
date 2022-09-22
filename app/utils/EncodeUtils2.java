package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加解密辅助工具
 * Created by win7 on 2016/6/9.
 */
@Singleton
public class EncodeUtils2 {

    private static final String md5salt = "xkxOIUDxclkjk96690";
    public static final String API_SALT = "https://g.starnew.cn/cent/";
    public static String key = "kG02XDFSLlksdjXc"; // 128 bit key

    /**
     * 计算md5，带盐值
     *
     * @param value
     * @return
     */
    public static String getMd5WithSalt(String value) {
        return getMd5(md5salt + value);
    }

    public static String getMd5WithSaltForApi(String value) {
        return getMd5(API_SALT + value);
    }

    /**
     * 计算md5
     *
     * @param value
     * @return
     */
    public static String getMd5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }


    public static String encrypt(String value) {
        if (ValidationUtil.isEmpty(value)) return "";
        return doEncrypt(value);
    }

    public static String decrypt(String encrypted) {
        if (ValidationUtil.isEmpty(encrypted)) return "";
        return doDecrypt(encrypted);
    }


    private static byte[] KEY = new byte[]{'s', '8', 'T', 'u', 'D', 'D', 'z', 's', 'T', 'k', 'f', 'k', 'd', 'a', '1', 'm'};
    private static final String ALGO = "AES";
    private static  String doEncrypt(String data) {
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encVal = c.doFinal(data.getBytes("UTF-8"));
            byte[] encrypted = Base64.getEncoder().encode(encVal);
            return new String(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String doDecrypt(String encrypted) {
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decoded = Base64.getDecoder().decode(encrypted.replace("\r\n", ""));
            byte[] decValue = c.doFinal(decoded);
            return new String(decValue,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static Key getKey() {
        SecretKeySpec key = new SecretKeySpec(KEY, ALGO);
        return key;
    }

}
