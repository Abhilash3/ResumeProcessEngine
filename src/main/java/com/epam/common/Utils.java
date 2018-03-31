package com.epam.common;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static long currentTimeInMillis() {
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
}
