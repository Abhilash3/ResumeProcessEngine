package com.epam.resume.loader.parsing.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Order(3)
class LowerCase implements Function<String, String> {
    @Override
    public String apply(String s) {
        return s.toLowerCase();
    }
}
