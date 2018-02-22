package com.epam.resume.request;

import java.util.Objects;

public class UpdateRequest {

    private final String fileName;
    private final int graduation;
    private final String email;

    public UpdateRequest(String fileName, int graduation, String email) {
        this.fileName = fileName;
        this.graduation = graduation;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateRequest that = (UpdateRequest) o;
        return graduation == that.graduation &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, graduation, email);
    }

    @Override
    public String toString() {
        return "UpdateRequest{fileName='" + fileName +
                "', graduation=" + graduation +
                ", email='" + email + "'}";
    }

    public String fileName() {
        return fileName;
    }

    public int graduation() {
        return graduation;
    }

    public String email() {
        return email;
    }
}
