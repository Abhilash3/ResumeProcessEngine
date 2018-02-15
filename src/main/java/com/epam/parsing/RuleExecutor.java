package com.epam.parsing;

import com.epam.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class RuleExecutor {

    @Autowired
    private List<Rule> rules;

    String applyRules(String content) {
        return rules.stream().reduce(content, (s, rule) -> rule.apply(s), (s1, s2) -> s1 + s2);
    }
}
