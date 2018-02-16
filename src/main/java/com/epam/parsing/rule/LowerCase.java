package com.epam.parsing.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class LowerCase implements Rule {
    @Override
    public String apply(String s) {
        return s.toLowerCase();
    }
}
