package com.epam.resume.loader.parsing.rule;

import com.epam.common.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Order(2)
class RemoveExtraSpace implements Function<String, String> {
    @Override
    public String apply(String s) {
        return s.replaceAll(Constants.Patterns.WHITESPACES, Constants.SPACE);
    }
}
