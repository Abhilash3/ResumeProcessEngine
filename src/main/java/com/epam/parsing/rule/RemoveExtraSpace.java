package com.epam.parsing.rule;

import com.epam.common.Constants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class RemoveExtraSpace implements IRule {
    @Override
    public String apply(String s) {
        return s.replaceAll(Constants.Patterns.WHITESPACES, Constants.SPACE);
    }
}
