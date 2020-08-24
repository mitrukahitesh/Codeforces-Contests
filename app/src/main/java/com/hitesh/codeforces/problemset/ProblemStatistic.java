
package com.hitesh.codeforces.problemset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProblemStatistic {

    @SerializedName("contestId")
    @Expose
    private Integer contestId;
    @SerializedName("index")
    @Expose
    private String index;
    @SerializedName("solvedCount")
    @Expose
    private Integer solvedCount;

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Integer getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(Integer solvedCount) {
        this.solvedCount = solvedCount;
    }

}
