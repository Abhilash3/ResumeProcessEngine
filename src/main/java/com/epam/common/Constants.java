package com.epam.common;

public class Constants {

    public static final String ID = "id";
    public static final String PERIOD = ".";
    public static final String SPACE = " ";
    public static final String BLANK = "";
    public static final String EXPERIENCE = "experience";
    public static final String RELEVANCE = "relevance";
    public static final String PAGE = "page";
    public static final String SORT = "sort";
    public static final String KEYWORDS = "keywords";
    public static final String FIELD = "field";
    public static final String CONTENT = "content";

    private Constants() {
        throw exception();
    }

    private static RuntimeException exception() {
        return Utils.runtimeException(Message.NOT_INITIALIZABLE);
    }

    public static class Message {
        public static final String NOT_INITIALIZABLE = "Not initializable";

        private Message() {
            throw exception();
        }
    }

    public static class FileTypes {
        public static final String PDF = "pdf";
        public static final String DOC = "doc";

        private FileTypes() {
            throw exception();
        }
    }

    public static class Patterns {
        public static final String EMAIL = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
        public static final String ALL_EXCEPT_ALPHANUMERIC = "[^a-zA-Z0-9+@# ]";
        public static final String WHITESPACES = "[\\s]+";
        public static final String YEARS = "\\b\\d{4}\\b";

        private Patterns() {
            throw exception();
        }
    }

    public static class Grouping {
        public static final String KEYWORDS = "keywords";
        public static final String COLLECTION = "groupings";

        private Grouping() {
            throw exception();
        }
    }

    public static class Resume {
        public static final String ID = "_id";
        public static final String EMAIL = "email";
        public static final String PROPERTIES = "properties";
        public static final String GRADUATION = "graduation";
        public static final String WORDS = "words";
        public static final String NOTES = "notes";
        public static final String COLLECTION = "resumes";

        private Resume() {
            throw exception();
        }
    }

    public static class FileProperties {
        public static final String FILE_NAME = "fileName";
        public static final String FILE_PATH = "filePath";
        public static final String EXTENSION = "extension";
        public static final String LAST_MODIFIED = "lastModified";

        private FileProperties() {
            throw exception();
        }
    }
}
