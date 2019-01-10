package com.mapsa.core.commits.status;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class AccountCommitStatus extends CommitStatus implements Serializable {
    @Id
    private String commitId;
    private String commitStatus;

    public AccountCommitStatus() {
    }

    public AccountCommitStatus(String commitId, String commitStatus) {
        this.commitId = commitId;
        this.commitStatus = commitStatus;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getStatus() {
        return commitStatus;
    }
}
