package com.example.taskmanagement.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy").withZone(ZoneId.systemDefault());

    public static String format(Instant instant) {
        return FORMATTER.format(instant);
    }
}
