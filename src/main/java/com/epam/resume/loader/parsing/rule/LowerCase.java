package com.epam.resume.loader.parsing.rule;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
class LowerCase implements IRule {
    @Override
    public String apply(String s) {
        return s.toLowerCase();
    }
}
