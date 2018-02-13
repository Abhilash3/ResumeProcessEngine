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

    private static final String NO_EMAIL = "No Email";

    @Autowired
    private RuleExecutor executor;

    private Map<String, Long> wordFrequency(String string) {
        return Arrays.stream(string.split(Constants.SPACE)).collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }

    private String emailId(String string) {
        String email = NO_EMAIL;
        Matcher match = Pattern.compile(Constants.Patterns.EMAIL).matcher(string);

        if (match.find()) {
            email = match.group();
        }
        return email;
    }

    private int graduationYear(String string) {
        Matcher matcher = Pattern.compile(Constants.Patterns.YEARS).matcher(string);
        Set<Integer> numbers = new HashSet<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = numbers.stream()
                .filter(a -> a <= currentYear && a >= currentYear - 30)
                .collect(Collectors.toList());

        years.sort(Comparator.comparingInt(a -> a));

        int graduationYear = 0;

        if (years.size() == 1) {
            graduationYear = years.get(0);
        } else if (years.size() == 2) {
            graduationYear = years.get(1);
        } else {
            outer:
            for (int i = 0; i < years.size() - 1; i++) {
                for (int j = i + 1; j < years.size(); j++) {
                    if (years.get(j) - years.get(i) > 7) {
                        break;
                    } else if (years.get(j) - years.get(i) >= 4) {
                        graduationYear = years.get(j);
                        break outer;
                    }
                }
            }
        }
        return graduationYear;
    }

    Resume resumeFrom(File file) throws IOException {
        String fileFullName = file.getName();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.')).trim();
        String extension = fileFullName.substring(fileFullName.lastIndexOf('.') + 1);
        String fileContent = FileTypes.parse(extension).parse(file);
        String filePath = file.getAbsolutePath();

        Map<String, Long> frequency = wordFrequency(executor.applyRules(fileContent));

        return new Resume(emailId(fileContent), fileName, extension, filePath, graduationYear(fileContent), frequency);
    }
}
