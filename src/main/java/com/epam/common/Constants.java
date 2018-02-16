package com.epam.common;

public class Constants {

    public static final String PERIOD = ".";
    public static final String SPACE = " ";
    public static final String SORT = "sort";
    public static final String SKILLS = "skills";
    public static final String RELEVANCE = "relevance";
    public static final String EXPERIENCE = "experience";

    public static class FileTypes {
        public static final String PDF = "pdf";
        public static final String DOC = "doc";
    }

    public static class Patterns {
        public static final String EMAIL = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
        public static final String ALL_EXCEPT_ALPHANUMERIC = "[^a-zA-Z0-9 ]";
        public static final String WHITESPACES = "[\\s]+";
        public static final String YEARS = "\\b\\d{4}\\b";
    }

    public static class Resume {
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String FILE_NAME = "fileName";
        public static final String FILE_PATH = "filePath";
        public static final String EXTENSION = "extension";
        public static final String LAST_MODIFIED = "lastModified";
        public static final String GRADUATION = "graduation";
        public static final String WORDS = "words";

        public static final String COLLECTION = "resume";
    }
}
