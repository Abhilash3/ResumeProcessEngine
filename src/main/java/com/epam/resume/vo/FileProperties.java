package com.epam.resume.vo;

import java.util.Objects;

import static com.epam.common.Constants.PERIOD;

public class FileProperties {

    private final String fileName;
    private final String extension;
    private final String filePath;
    private final long lastModified;

    public FileProperties(String fileName, String extension, String filePath, long lastModified) {
        this.fileName = fileName;
        this.extension = extension;
        this.filePath = filePath;
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "FileProperties{fileName='" + fileName +
                "', extension='" + extension +
                "', filePath='" + filePath +
                "', lastModified=" + lastModified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileProperties that = (FileProperties) o;
        return lastModified == that.lastModified &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(extension, that.extension) &&
                Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, extension, filePath, lastModified);
    }

    public String extension() {
        return extension;
    }

    public String fileName() {
        return fileName;
    }

    String filePath() {
        return filePath;
    }

    long lastModified() {
        return lastModified;
    }

    String fullName() {
        return fileName + PERIOD + extension;
    }
}
