package dang.kp.manager.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateTimeUtils {
    public static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    public static final SimpleDateFormat SDF_FULL_SHOW = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat SDF_SIMPLE = new SimpleDateFormat("yyyyMMdd");
    // 补位字符
    public static final SimpleDateFormat SDF_YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_YMD_HMS = new SimpleDateFormat("yyyyMMddHHmmss");
    // 1天的秒数 1000 * 3600 * 24
    private static final Long DAY_IN_MILLIS = 86400000L;

    public static String getDay() {
        return SDF_YYYYMMDD.format(new Date());
    }

    public static String getHMSDay() {
        return SDF_YMD_HMS.format(new Date());
    }

    public static String getDayFull(Long time) {
        return SDF_FULL.format(new Date(time));
    }

    /**
     * 生成主键
     *
     * @return
     */
    public static Long getLastPositiveDate(Integer overDueDays) {
        return getSomeDay(overDueDays).getTime();
    }

    public static String getLastSomeDay(Integer days) {
        return SDF_YYYYMMDD.format(getSomeDay(0 - days));
    }

    public static Date getSomeDay(Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static String getSomeYears(Integer years) {
        return SDF_FULL_SHOW.format(nextSomeYears(years));
    }
    public static Date nextSomeYears(Integer years) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    public static String getPastDateTime(Integer days) {
        return SDF_SIMPLE.format(new Date(System.currentTimeMillis() - days * DAY_IN_MILLIS)) + "000000";
    }

    /**
     * 功能描述:
     *
     * @param: 获取当前系统时间 yyyy-MM-dd HH:mm:ss
     * @return:
     * @auther: youqing
     * @date: 2018/5/26 9:59
     */
    public static String now() {
        String date = SDF_FULL_SHOW.format(System.currentTimeMillis());
        return date;
    }
    /**
     * 功能描述:
     *
     * @param: 获取当前系统时间 yyyy-MM-dd HH:mm:ss
     * @return:
     * @auther: youqing
     * @date: 2018/5/26 9:59
     */
    public static String nowSimple() {
        String date = SDF_YYYYMMDD.format(System.currentTimeMillis());
        return date;
    }
    public static int nowWeekDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Long timeCost(String timeStart) {

        try {
            Long start = SDF_FULL_SHOW.parse(timeStart).getTime();
            return (System.currentTimeMillis() - start) / 1000;
        } catch (ParseException e) {
            log.error("ParseException {}", e);
            return 0L;
        }
    }
}
