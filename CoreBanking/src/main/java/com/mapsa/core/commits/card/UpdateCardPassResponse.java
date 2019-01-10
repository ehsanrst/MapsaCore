package com.mapsa.core.commits.card;

import java.io.Serializable;

public class UpdateCardPassResponse extends CardCommitResponse implements Serializable {
    private boolean done;

    public UpdateCardPassResponse(UpdateCardPassCommit updateCardPassCommit, boolean done) {
        this.done = done;
        super.setCommitId(updateCardPassCommit.getCUID());
    }

    public boolean isDone() {
        return done;
    }

}
