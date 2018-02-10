package com.epam.rule.component;

import com.epam.common.Constants;
import com.epam.rule.Rule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class RemoveExtraSpace implements Rule {

    private static final String Whitespaces = "[\\s]+";

    @Override
    public String apply(String s) {
        return s.replaceAll(Whitespaces, Constants.SPACE);
    }
}
