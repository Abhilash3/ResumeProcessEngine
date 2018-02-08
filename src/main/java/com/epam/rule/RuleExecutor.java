package com.epam.rule;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class RuleExecutor {

    private Set<Rule> rules;

    public RuleExecutor() {
        rules = new LinkedHashSet<>();
    }

    public RuleExecutor addRule(Rule rule) {
        rules.add(rule);
        return this;
    }

    public String applyRules(String content) {
        return rules.stream().reduce(content, (s, rule) -> rule.apply(s), (s1, s2) -> s1 + s2);
    }
}
