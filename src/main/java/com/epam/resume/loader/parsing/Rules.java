package com.epam.resume.loader.parsing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class Rules {

    private final Function<String, String> ops;

    @Autowired
    public Rules(List<Function<String, String>> rules) {
        this.ops = rules.stream().reduce(Function.identity(), Function::andThen);
    }

    public String applyRules(String content) {
        return ops.apply(content);
    }
}
