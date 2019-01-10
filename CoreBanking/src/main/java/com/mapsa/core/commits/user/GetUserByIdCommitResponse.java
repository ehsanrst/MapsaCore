package com.mapsa.core.commits.user;

import com.mapsa.core.user.User;

import java.io.Serializable;

public class GetUserByIdCommitResponse extends UserCommitResponse implements Serializable {
    public GetUserByIdCommitResponse(GetUserByIdCommit getUserByIdCommit,User user) {
        this.user = user;
        super.setCommitId(getUserByIdCommit.getCUID());
    }

    private User user;

    public User getUser() {
        return user;
    }


}
