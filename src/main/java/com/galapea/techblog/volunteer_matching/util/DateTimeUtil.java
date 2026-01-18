package com.galapea.techblog.volunteer_matching.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter ISO_DATE_TIME_WITH_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    /**
     * Converts a string with UTC offset (e.g. "2023-12-15T10:45:00.032Z") to LocalDateTime.
     * The offset is ignored and the instant is converted to the local time.
     */
    public static LocalDateTime parseToLocalDateTime(String dateTimeStr) {
        OffsetDateTime odt = OffsetDateTime.parse(dateTimeStr, ISO_DATE_TIME_WITH_OFFSET);
        return odt.toLocalDateTime();
    }

    public static String formatToZoneDateTimeString(LocalDateTime localDateTime) {
        java.time.ZonedDateTime zdt = localDateTime.atZone(ZoneId.of("UTC"));
        return zdt.format(ISO_DATE_TIME_WITH_OFFSET);
    }

    public static String formatDaysAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long days = duration.toDays();

        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "1 day ago";
        } else {
            return days + " days ago";
        }
    }
}
