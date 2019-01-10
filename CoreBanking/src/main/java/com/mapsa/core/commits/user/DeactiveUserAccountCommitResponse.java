package com.mapsa.core.commits.user;

import java.io.Serializable;

public class DeactiveUserAccountCommitResponse extends UserCommitResponse implements Serializable {

    private boolean done;

    public DeactiveUserAccountCommitResponse(DeactivateUserAccountCommit deactivateUserAccountCommit ,boolean done) {

        this.done = done;
        super.setCommitId(deactivateUserAccountCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}
