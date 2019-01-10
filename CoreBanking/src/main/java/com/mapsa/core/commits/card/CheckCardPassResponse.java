package com.mapsa.core.commits.card;

import java.io.Serializable;

public class CheckCardPassResponse extends CardCommitResponse implements Serializable {
    private boolean passIsCorrect;

    public CheckCardPassResponse(CheckCardPassCommit checkCardPassCommit, boolean passIsCorrect) {
        this.passIsCorrect = passIsCorrect;
        super.setCommitId(checkCardPassCommit.getCUID());

    }

    public boolean isPassIsCorrect() {
        return passIsCorrect;
    }

}
