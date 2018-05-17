package com.epam.resume.loader.parsing;

import com.epam.resume.loader.parsing.rule.KeepOnlyAlphanumeric;
import com.epam.resume.loader.parsing.rule.LowerCase;
import com.epam.resume.loader.parsing.rule.TrimWhiteSpaces;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Rules.class, KeepOnlyAlphanumeric.class, TrimWhiteSpaces.class, LowerCase.class})
public class RulesTest {

    @Autowired
    private Rules rule;

    @Test
    public void lowercase() {
        executeTest("Test String", "test string");
        executeTest("Long Test String like this ONE", "long test string like this one");
    }

    @Test
    public void removeExtraChars() {
        executeTest("ab cd ef +-_=!@#$%^&*(){}[].?></, 127834", "ab cd ef + @# 127834");
        executeTest("a()b +-_=!@$#%^&*.?></, 12[]78{}34", "a b + @ # 12 78 34");
    }

    @Test
    public void trimExtraSpaces() {
        executeTest("string with    extra space and \n next line char", "string with extra space and next line char");
        executeTest("string with \n multiple \n\n next line chars", "string with multiple next line chars");
    }

    private void executeTest(String input, String output) {
        assertEquals(output, rule.applyRules(input));
    }
}