
package com.hitesh.codeforces.contest;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contests {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

}
