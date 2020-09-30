
package com.hitesh.codeforces.user;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchedUser {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("result")
    @Expose
    private List<UserDetail> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<UserDetail> getUserDetail() {
        return result;
    }

    public void setUserDetail(List<UserDetail> result) {
        this.result = result;
    }

}
