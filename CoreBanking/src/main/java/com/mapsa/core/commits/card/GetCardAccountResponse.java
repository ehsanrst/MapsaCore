package com.mapsa.core.commits.card;

import java.io.Serializable;

public class GetCardAccountResponse extends CardCommitResponse implements Serializable {
    private String accountId;

    public GetCardAccountResponse(GetCardAccountCommit getCardAccountCommit, String accountId) {
        this.accountId = accountId;
        super.setCommitId(getCardAccountCommit.getCUID());
    }

    public String getAccountId() {
        return accountId;
    }

}
