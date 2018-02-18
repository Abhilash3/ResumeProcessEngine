package com.epam.parsing;

import com.epam.parsing.rule.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class RuleExecutor {

    @Autowired
    private List<IRule> rules;

    String applyRules(String content) {
        return rules.stream().reduce(content, (s, rule) -> rule.apply(s), (s1, s2) -> s1 + s2);
    }
}
