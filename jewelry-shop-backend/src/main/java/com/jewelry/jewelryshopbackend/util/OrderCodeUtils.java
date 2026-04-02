package com.jewelry.jewelryshopbackend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderCodeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderCodeUtils() {
    }

    public static String generate() {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "ODR-" + LocalDateTime.now().format(FORMATTER) + "-" + suffix;
    }
}
