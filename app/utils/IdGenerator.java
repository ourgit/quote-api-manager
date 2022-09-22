package utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2017/3/21.
 */
public class IdGenerator {
    /**
     * 实现不重复的时间
     */
    public static final char[] ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static long getId() {
        AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());
        return lastTime.incrementAndGet();
    }
    public static String createUUid() {
//        AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());
//        return lastTime.incrementAndGet();
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,ALPHABET,32);
    }
}
