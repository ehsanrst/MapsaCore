package com.mapsa.core.commits.user;

import com.mapsa.core.commits.account.DeactivateAccountCommit;

import java.io.Serializable;

public class DeactiveUserCommitResponse extends UserCommitResponse implements Serializable {

    private boolean done;

    public DeactiveUserCommitResponse(DeactivateUserCommit deactivateUserCommit, boolean done) {
        this.done = done;
        super.setCommitId(deactivateUserCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}
