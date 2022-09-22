package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * Created by win7 on 2016/6/11.
 */
public class ValidationUtil {
    /**
     * 是否是有效的手机号
     * 由于手机号码规则经常变动，为了方便起见，只判断是11位，并且以1开头，暂不考虑其他规则
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (null == phoneNumber || phoneNumber.length() != 11) return false;
        String regExp = "^[1][0-9]{10}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNumber);
        return m.find();
    }

    /**
     * 是否是有效的验证码，目前校验6位
     *
     * @param vcode
     * @return
     */
    public static boolean isVcodeCorrect(String vcode) {
        if (null == vcode || vcode.length() != 6) return false;
        return true;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断字符串是否为空。
     *
     * @param value
     * @return false, 当该字符串不为null且长度>0时;其他都为true
     */
    public static boolean isEmpty(String value) {
        if (null != value) {
            value = value.trim();
            if (value.length() > 0 && !value.equalsIgnoreCase("null"))
                return false;
        }
        return true;
    }

    /**
     * 判断是否是有效的密码
     *
     * @param password
     * @return
     */
    public static boolean isValidPassword(String password) {
        if (null == password) return false;
        String reg = "^[0-9a-zA-Z]{6,20}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 校验是否是有效的日期格式,yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static boolean isValidDate(String date) {
        if (isEmpty(date)) return false;
        String reg = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(date);
        return m.matches();
    }

    public static boolean isValidIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }
            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
