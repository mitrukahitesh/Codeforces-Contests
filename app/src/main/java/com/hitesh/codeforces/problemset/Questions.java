package com.hitesh.codeforces.problemset;

import java.util.List;

public class Questions implements Comparable {
    private String name;
    private String id;
    private Integer solvedBy;
    private List<String> tags;

    public Questions(String name, String id, Integer solvedBy, List<String> tags) {
        this.name = name;
        this.id = id;
        this.solvedBy = solvedBy;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Integer getSolvedBy() {
        return solvedBy;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Object o) {
        Questions q = (Questions)o;
        return q.solvedBy - this.solvedBy;
    }
}
