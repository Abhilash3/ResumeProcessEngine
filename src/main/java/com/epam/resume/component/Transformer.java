package com.epam.resume.component;

import com.epam.common.Constants;
import com.epam.file.FileTypes;
import com.epam.resume.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
class Transformer {

    @Autowired
    private RuleExecutor executor;

    private Map<String, Long> wordFrequency(String string) {
        return Arrays.stream(string.split(Constants.SPACE)).collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }

    private String email(String string) {
        String email = "";
        Matcher match = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(string);
        if (match.find()) {
            email = match.group();
        }
        return email;
    }

    private int graduationYear(String string) {
        Matcher matcher = Pattern.compile("19[89][0-9]|20[0-1][0-9]").matcher(string);
        Set<Integer> numbers = new HashSet<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }
        List<Integer> years = new ArrayList<>(numbers);
        years.sort(Comparator.comparingInt(a -> a));

        int graduationYear = 2000;
        outer:
        for (int i = 0; i < years.size() - 1; i++) {
            for (int j = i + 1; j < years.size(); j++) {
                if (years.get(j) - years.get(i) > 6) {
                    break;
                } else if (years.get(j) - years.get(i) >= 4) {
                    graduationYear = years.get(j);
                    break outer;
                }
            }
        }
        return graduationYear;
    }

    Resume createResume(File file) throws IOException {
        String fileFullName = file.getName();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        String extension = fileFullName.substring(fileFullName.lastIndexOf('.') + 1);
        String fileContent = FileTypes.parse(extension).parse(file);
        String filePath = file.getAbsolutePath();

        Map<String, Long> frequency = wordFrequency(executor.applyRules(fileContent));

        return new Resume(email(fileContent), fileName, extension, filePath, graduationYear(fileContent), frequency);
    }
}
