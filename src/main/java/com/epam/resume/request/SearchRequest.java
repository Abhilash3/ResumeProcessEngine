package com.epam.resume.request;

import java.util.List;
import java.util.Objects;

public class SearchRequest {

    private final List<String> skills;
    private final int experience;
    private final String sort;

    public SearchRequest(List<String> skills, int experience, String sort) {
        this.skills = skills;
        this.experience = experience;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRequest that = (SearchRequest) o;
        return experience == that.experience &&
                Objects.equals(skills, that.skills) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills, experience, sort);
    }

    @Override
    public String toString() {
        return "SearchRequest{skills=" + skills +
                ", experience=" + experience +
                ", sort='" + sort + "'}";
    }

    public String sort() {
        return sort;
    }

    public List<String> skills() {
        return skills;
    }

    public int experience() {
        return experience;
    }
}
