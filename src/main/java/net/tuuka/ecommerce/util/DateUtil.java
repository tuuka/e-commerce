package net.tuuka.ecommerce.util;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

/*  Converters to serialize/deserialize ZonedDateTime into/from Jackson Json
    without JavaTimeModule but with field annotation :
     @JsonSerialize(converter = DateUtil.ZonedDateTimeToStringConverter.class)
     @JsonDeserialize(converter = DateUtil.StringToZonedDateTimeConverter.class)*/

    public static class ZonedDateTimeToStringConverter extends StdConverter<ZonedDateTime, String> {

        @Override
        public String convert(ZonedDateTime value) {
            return value.format(DATE_FORMATTER);
        }
    }

    public static class StringToZonedDateTimeConverter extends StdConverter<String, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(String value) {
            return ZonedDateTime.parse(value, DATE_FORMATTER);
        }
    }

}
