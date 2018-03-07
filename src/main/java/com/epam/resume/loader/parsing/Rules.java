package com.epam.resume.loader.parsing;

import com.epam.resume.loader.parsing.rule.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Rules {

    @Autowired
    private List<IRule> rules;

    public String applyRules(String content) {
        return rules.stream().reduce(content, (s, rule) -> rule.apply(s), (s1, s2) -> s1 + s2);
    }
}
