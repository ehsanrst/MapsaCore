package com.mapsa.core.commits.account;

import java.io.Serializable;

public class DepositCommitResponse extends AccountCommitResponse implements Serializable {

    private Boolean done;
    private DepositCommit depositCommit;

    public DepositCommitResponse(DepositCommit depositCommit, Boolean done) {
        this.done = done;
        super.setCommitId(depositCommit.getCUID());
    }

    public Boolean getDone() {
        return done;
    }

}

