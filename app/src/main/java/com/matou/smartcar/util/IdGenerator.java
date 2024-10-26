package com.matou.smartcar.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ranfeng
 */
public class IdGenerator {
    private static long currentId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE) + 1;

    public static synchronized long getNextId() {
        // 获取下一个ID并递增
        return currentId++;
    }
}