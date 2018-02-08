package com.epam.rule;

public class LowerCase implements Rule {
    @Override
    public String apply(String s) {
        return s.toLowerCase();
    }
}
