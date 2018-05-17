package com.epam;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.grouping.vo.Grouping;
import com.epam.resume.vo.FileProperties;
import com.epam.resume.vo.Resume;

import java.util.Arrays;

public class TestingMother {

    private TestingMother() {
        throw Utils.runtimeException(Constants.Message.NOT_INITIALIZABLE);
    }

    public static Grouping grouping(String... words) {
        return new Grouping(Arrays.asList(words));
    }

    public static FileProperties properties(String name, String extension, String path, long lastModified) {
        return new FileProperties(name, extension, path, lastModified);
    }

    public static Resume resume(String id, String email, String name, String path, int exp, String words) {
        return new Resume(id, email, properties(name, Constants.FileTypes.PDF, path, 0),
                Utils.currentYear() - exp, Utils.wordFrequency(words), Constants.BLANK);
    }
}
