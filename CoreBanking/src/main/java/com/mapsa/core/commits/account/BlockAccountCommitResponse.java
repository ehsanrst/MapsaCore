package com.mapsa.core.commits.account;

import java.io.Serializable;

public class BlockAccountCommitResponse extends AccountCommitResponse implements Serializable {
    private boolean done;

    public BlockAccountCommitResponse(BlockAccountCommit blockAccountCommit, boolean done) {
        this.done = done;
        super.setCommitId(blockAccountCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }
}

