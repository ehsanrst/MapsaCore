package com.mapsa.core.commits.card;

import java.io.Serializable;

public class ReactivateCardResponse extends CardCommitResponse implements Serializable {
    private boolean done;

    public ReactivateCardResponse(ReactivateCardCommit reactivateCardCommit, boolean done) {
        this.done = done;
        super.setCommitId(reactivateCardCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }

}
