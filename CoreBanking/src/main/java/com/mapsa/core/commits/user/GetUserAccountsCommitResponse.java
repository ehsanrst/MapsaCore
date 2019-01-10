package com.mapsa.core.commits.user;

import com.mapsa.core.account.AccountStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetUserAccountsCommitResponse extends UserCommitResponse implements Serializable {

    private List<AccountStatus> accounts;

    public GetUserAccountsCommitResponse(GetUserAccountsCommit getUserAccountsCommit, ArrayList<AccountStatus> accounts) {
        this.accounts = accounts;
        super.setCommitId(getUserAccountsCommit.getCUID());
    }

    public List<AccountStatus> getAccounts() {
        return accounts;
    }

}
