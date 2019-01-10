package com.mapsa.core.commits.user;

import java.io.Serializable;

public class AddUserAccountCommitResponse extends UserCommitResponse implements Serializable {

    private boolean done;
    public AddUserAccountCommitResponse(AddUserAccountCommit addUserAccountCommit,boolean done) {
        this.done = done;
        super.setCommitId(addUserAccountCommit.getCUID());
    }
    public boolean isDone() {
        return done;
    }
}
