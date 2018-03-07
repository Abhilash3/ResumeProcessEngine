package com.epam.resume.loader.parsing.rule;

import com.epam.common.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
class KeepOnlyAlphanumeric implements IRule {
    @Override
    public String apply(String s) {
        // TODO: need to be able to search for .net skill
        return s.replaceAll(Constants.Patterns.ALL_EXCEPT_ALPHANUMERIC, Constants.SPACE);
    }
}
