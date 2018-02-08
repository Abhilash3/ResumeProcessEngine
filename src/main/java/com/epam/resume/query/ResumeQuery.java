package com.epam.resume.query;

import java.util.List;
import java.util.Objects;

public class ResumeQuery {

    private final List<String> skills;

    private final int experience;

    public ResumeQuery(List<String> skills, int experience) {
        this.skills = skills;
        this.experience = experience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeQuery that = (ResumeQuery) o;
        return experience == that.experience &&
                Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills, experience);
    }

    public List<String> skills() {
        return skills;
    }

    public int experience() {
        return experience;
    }
}
