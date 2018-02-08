package com.epam.rule;

public class RemoveExtraSpace implements Rule {
    @Override
    public String apply(String s) {
        return s.replaceAll("[\\s]+", " ");
    }
}
