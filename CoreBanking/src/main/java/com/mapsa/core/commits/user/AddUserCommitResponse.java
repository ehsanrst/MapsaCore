package com.mapsa.core.commits.user;

import java.io.Serializable;

public class AddUserCommitResponse extends UserCommitResponse implements Serializable {

    private String userId;

    public AddUserCommitResponse(AddUserCommit addUserCommit, String userId) {
        this.userId = userId;
        super.setCommitId(addUserCommit.getCUID());
    }

    public String getUserId() {
        return userId;
    }

}
