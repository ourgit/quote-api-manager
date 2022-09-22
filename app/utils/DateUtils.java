package utils;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by win7 on 2016/6/9.
 */
@Singleton
public class DateUtils {

    public static final long ARTICLE_END_TIME = 4102243200l;

    /**
     * 获取当前时间，以秒为单位
     *
     * @return
     */
    public long getCurrentTimeBySecond() {
        return System.currentTimeMillis() / 1000;
    }

    int pageSize = 0;

    /**
     * 秒数转化为HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatSecondsToHMS(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数格式化为年月日,如20160503
     *
     * @param timeStamp
     * @return
     */
    public String formatToYMD(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为YYYY-MM-DD HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatToYMDHMSBySecond(long timeStamp) {
        if (timeStamp < 1) return "";
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为YYYY-MM-DD HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatToYMBySecond(long timeStamp) {
        if (timeStamp < 1) return "";
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("YYYY-MM");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为YYYY-MM-DD HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatToDayBySecond(long timeStamp) {
        if (timeStamp < 1) return "";
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("d");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为MM-DD HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatToMDHMSBySecond(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为MM-DD
     *
     * @param timeStamp
     * @return
     */
    public String formatToMDBySecond(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("MM月dd号");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 秒数转化为YYYY-MM-DD
     *
     * @param timeStamp
     * @return
     */
    public String formatToDashYMDBySecond(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * 获取昨夜零点的秒数
     *
     * @return
     */
    public long getTodayMinTimestamp() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayMin = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 0, 0, 0);
        Timestamp timestamp = Timestamp.valueOf(todayMin);
        return timestamp.getTime() / 1000;
    }


    /**
     * 获取今夜零点的秒数
     *
     * @return
     */
    public long getTodayMidnightTimestamp() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime todayMidNight = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 59, 59);
        Timestamp timestamp = Timestamp.valueOf(todayMidNight);
        return timestamp.getTime()/1000;
    }

    /**
     * 获取今夜零点的秒数
     *
     * @return
     */
    public long get3DaysAgoTimestamp() {
        return getCurrentTimeBySecond() - 3 * 24 * 3600;
    }

    /**
     * 获取多少天前的秒数
     *
     * @return
     */
    public long getDaysAgoTimestamp(int days) {
        return getCurrentTimeBySecond() - days * 24 * 3600;
    }

    /**
     * 输出距离今天天数的某一天时间，格式以2016-03-20输出
     *
     * @param farFrom
     * @return
     */
    public String getFormatdayFarfromToday(int farFrom) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(farFrom);
        String formatString = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return formatString;
    }

    /**
     * 获取当前时间的秒数，取整到分钟
     *
     * @return
     */
    public long getCurrentTimeTrunkMinutes() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now.truncatedTo(ChronoUnit.MINUTES));
        long currentTimeWithoutMinutes = timestamp.getTime() / 1000;
        return currentTimeWithoutMinutes;
    }

    /**
     * 获取当前时间的秒数，取整到小时
     *
     * @return
     */
    public long getCurrentTimeTrunkHours() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now.truncatedTo(ChronoUnit.HOURS));
        long currentTimeWithoutHours = timestamp.getTime() / 1000;
        return currentTimeWithoutHours;
    }

    /**
     * 转换时间
     *
     * @param date
     * @return
     */
    @NotNull
    public String formateStringToDateYMD(String date) {
        String[] split = date.split("-");
        LocalDate thatDate = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        return thatDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public long convertMonthToMinUnixStamp(String date) {
        String[] split = date.split("-");
        LocalDateTime thatDate = LocalDateTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 1, 0, 0, 0);
        Timestamp timestamp = Timestamp.valueOf(thatDate);
        return timestamp.getTime() / 1000;
    }

    public long convertMonthToMaxUnixStamp(String date) {
        String[] split = date.split("-");
        LocalDateTime thatDate = LocalDateTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 1, 0, 0, 0);
        Timestamp timestamp = Timestamp.valueOf(thatDate.plusMonths(1));
        return timestamp.getTime() / 1000;
    }

    /**
     * 日期转为unix stamp
     *
     * @param date
     * @return
     */
    public long convertStringToUnixStamp(String date) {
        String[] split = date.split("-");
        LocalDateTime time = LocalDateTime.of(Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                0, 0, 0);
        Timestamp timestamp = Timestamp.valueOf(time);
        return timestamp.getTime() / 1000;
    }

    public long convertYMDHMS2unixstamp(String date) {
        if (ValidationUtil.isEmpty(date)) return 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date result = df.parse(date);
            return result.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String convertToHHmmssSSS(long createTime) {
        DateFormat formatter = new SimpleDateFormat("HHmmssSSS");
        String dateFormatted = formatter.format(createTime);
        return dateFormatted;
    }

    public String convertToDateWithMills(long createTime) {
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        String dateFormatted = formatter.format(createTime);
        return dateFormatted;
    }

    /**
     * 获取昨日时间戳最小值与最大值
     *
     * @return
     */
    public long[] getYesterDayMinAndMaxStamp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime yesterdayMin = LocalDateTime.of(yesterday.getYear(), yesterday.getMonth(), yesterday.getDayOfMonth(), 0, 0, 0);
        LocalDateTime yesterdayMax = LocalDateTime.of(yesterday.getYear(), yesterday.getMonth(), yesterday.getDayOfMonth(), 23, 59, 59);
        long minStamp = Timestamp.valueOf(yesterdayMin).getTime() / 1000;
        long maxStamp = Timestamp.valueOf(yesterdayMax).getTime() / 1000;
        return new long[]{minStamp, maxStamp};
    }

    /**
     * 秒数转化为YYYY-MM-DD HH:mm:ss
     *
     * @param timeStamp
     * @return
     */
    public String formatToYMDDashBySecond(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public String getNowYMDHMS() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public String formatSecondsToHM(long timeStamp) {
        Date date = new Date(timeStamp * 1000);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public String getCurrentMonth() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("YYYYMM");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }
    public String getMonth(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("YYYYMM");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }
    public String getCurrentMonthDash() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("YYYY-MM");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public String getToday() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }
}
