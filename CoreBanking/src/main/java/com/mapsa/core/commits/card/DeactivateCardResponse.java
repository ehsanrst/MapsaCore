package com.mapsa.core.commits.card;

import java.io.Serializable;

public class DeactivateCardResponse extends CardCommitResponse implements Serializable {
    private boolean done;

    public DeactivateCardResponse(DeactivateCardCommit deactivateCardCommit, boolean done) {
        this.done = done;
        super.setCommitId(deactivateCardCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }

}
