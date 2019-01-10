package com.mapsa.core.commits.status;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class UserCommitStatus extends CommitStatus implements Serializable {
    @Id
    private String commitId;
    private String commitStatus;

    public UserCommitStatus() {
    }

    public UserCommitStatus(String commitId, String commitStatus) {
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
