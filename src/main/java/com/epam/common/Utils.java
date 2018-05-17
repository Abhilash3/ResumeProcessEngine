package com.epam.common;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    private Utils() {
        throw runtimeException(Constants.Message.NOT_INITIALIZABLE);
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static long currentMillis() {
        return now().toInstant().toEpochMilli();
    }

    public static int currentYear() {
        return now().getYear();
    }

    public static <E, F> List<F> collect(Iterator<E> iterator, Function<E, F> function) {
        Iterable<E> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(function).collect(Collectors.toList());
    }

    public static Map<String, Long> wordFrequency(String string) {
        return Arrays.stream(string.split(Constants.SPACE)).filter(a -> a.length() > 0)
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }

    public static RuntimeException runtimeException(String msg) {
        return new RuntimeException(msg);
    }
}
