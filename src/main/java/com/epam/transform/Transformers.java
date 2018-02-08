package com.epam.transform;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Transformers {

    private Transformers() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Long> wordFrequency(String s) {
        return Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }
}
