package com.epam.resume_gateway.dao;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Resume {

    @Id
    private final String id;
    private final String content;

    public Resume(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(id, resume.id) &&
                Objects.equals(content, resume.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    public String id() {
        return id;
    }

    public String content() {
        return content;
    }
}
