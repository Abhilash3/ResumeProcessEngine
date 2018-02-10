package com.epam.rule.component;

import com.epam.common.Constants;
import com.epam.rule.Rule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class KeepOnlyAlphanumeric implements Rule {

    private static final String AllExceptAlphanumeric = "[^a-zA-Z0-9 ]";

    @Override
    public String apply(String s) {
        return s.replaceAll(AllExceptAlphanumeric, Constants.SPACE);
    }
}
