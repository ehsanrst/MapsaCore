package com.mapsa.core.commits.account;

import java.io.Serializable;

public class CreateAccountCommitResponse extends AccountCommitResponse implements Serializable {
    private String accountId;

    public CreateAccountCommitResponse(CreateAccountCommit createAccountCommit, String accountId) {
        this.accountId = accountId;
        super.setCommitId(createAccountCommit.getCUID());
    }

    public String getAccountId() {
        return this.accountId;
    }

}
