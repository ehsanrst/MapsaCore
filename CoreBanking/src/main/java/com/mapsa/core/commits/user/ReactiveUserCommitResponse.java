package com.mapsa.core.commits.user;

import java.io.Serializable;

public class ReactiveUserCommitResponse extends UserCommitResponse implements Serializable {
    private boolean done;

    public ReactiveUserCommitResponse(ReactivateUserCommit commit ,boolean done) {
        this.done = done;
        super.setCommitId(commit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}

