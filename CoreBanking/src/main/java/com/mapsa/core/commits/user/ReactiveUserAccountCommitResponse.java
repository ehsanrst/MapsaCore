package com.mapsa.core.commits.user;


import java.io.Serializable;

public class ReactiveUserAccountCommitResponse extends UserCommitResponse implements Serializable {
    private boolean done;

    public ReactiveUserAccountCommitResponse(ReactivateUserAccountCommit reactivateUserAccountCommit,boolean done) {
        this.done = done;
        super.setCommitId(reactivateUserAccountCommit.getCUID());
    }

}
