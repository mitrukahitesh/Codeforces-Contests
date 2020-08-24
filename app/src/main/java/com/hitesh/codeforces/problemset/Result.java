
package com.hitesh.codeforces.problemset;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("problems")
    @Expose
    private List<Problem> problems = null;
    @SerializedName("problemStatistics")
    @Expose
    private List<ProblemStatistic> problemStatistics = null;

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public List<ProblemStatistic> getProblemStatistics() {
        return problemStatistics;
    }

    public void setProblemStatistics(List<ProblemStatistic> problemStatistics) {
        this.problemStatistics = problemStatistics;
    }

}
