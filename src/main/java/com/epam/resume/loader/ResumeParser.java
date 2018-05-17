package com.epam.resume.loader;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.file.FileTypes;
import com.epam.resume.loader.parsing.Rules;
import com.epam.resume.vo.FileProperties;
import com.epam.resume.vo.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
class ResumeParser {

    private static final String NO_EMAIL = "Email not found";

    @Value("${application.resume.location}")
    private String basePath;

    @Autowired
    private Rules rules;

    private Map<String, Long> wordFrequency(String string) {
        return Utils.wordFrequency(rules.applyRules(string));
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
        int currentYear = Utils.currentYear();
        List<Integer> years = numbers.stream()
                .filter(a -> a <= currentYear && a >= currentYear - 30)
                .collect(Collectors.toList());

        if (years.isEmpty()) {
            return 0;
        }

        if (years.size() == 1) {
            return years.get(0);
        }

        years.sort(Comparator.comparingInt(a -> a));

        int start = 0;
        if (years.get(1) - years.get(0) >= 12) {
            start = 1;
        }
        int possibleGraduationYear = years.get(start);
        for (int i = 1; i < 3 && i < years.size() - start; i++) {
            int yearDiff = years.get(i + start) - possibleGraduationYear;
            if (yearDiff >= 4 && yearDiff <= 7) {
                possibleGraduationYear = years.get(i + start);
                break;
            }
        }
        return possibleGraduationYear;
    }

    Resume resumeFrom(File file) throws IOException {
        String fileFullName = file.getName();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.')).trim();
        String extension = fileFullName.substring(fileFullName.lastIndexOf('.') + 1);
        String fileContent = FileTypes.parser(extension).parse(file);
        String filePath = file.getAbsolutePath().replaceAll("[\\\\]", "/").split(basePath)[1];
        long lastModified = file.lastModified();

        Map<String, Long> frequency = wordFrequency(fileContent);
        String email = emailId(fileContent);
        int graduationYear = graduationYear(fileContent);

        String id = NO_EMAIL.equals(email) ? filePath : email;
        FileProperties properties = new FileProperties(fileName, extension, filePath, lastModified);
        return new Resume(id, email, properties, graduationYear, frequency, Constants.BLANK);
    }
}
