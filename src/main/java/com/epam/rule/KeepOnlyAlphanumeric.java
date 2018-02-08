package com.epam.rule;

public class KeepOnlyAlphanumeric implements Rule {
    @Override
    public String apply(String s) {
        return s.replaceAll("[^a-zA-Z0-9 ]", " ");
    }
}
