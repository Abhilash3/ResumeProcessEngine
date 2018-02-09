package com.epam.resume.component;

import com.epam.resume.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
class Transformer {

    @Autowired
    private RuleExecutor executor;

    private Map<String, Long> wordFrequency(String s) {
        return Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }

    Resume createResume(File file) throws IOException {
        String fileName = file.getName();
        String id = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);/*
        String fileContent = FileTypes.parse(extension).parse(file);

        Matcher matcher = Pattern.compile("19[89][0-9]|20[0-1][0-9]").matcher(fileContent);
        Set<Integer> numbers = new HashSet<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }
        int[] years = new int[numbers.size()];
        int index = 0;
        for(Integer year : numbers) {
            years[index++] = year;
        }

        Arrays.sort(years);

        int graduationYear = 2000;
        outer:
        for (int i = 0; i < years.length - 1; i++) {
            for (int j = i + 1; j < years.length; j++) {
                if (years[j] - years[i] > 6) {
                    break;
                } else if (years[j] - years[i] >= 4) {
                    graduationYear = years[j];
                    break outer;
                }
            }
        }

        Map<String, Long> map = wordFrequency(executor.applyRules(fileContent));*/

        return new Resume(id, extension, file.getAbsolutePath(), 1990 + (int) (Math.random() * 20), Collections.emptyList());
    }
}
