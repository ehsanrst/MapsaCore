package com.mapsa.core.commits.status;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class CardCommitStatus extends CommitStatus implements Serializable {
    @Id
    private String commitId;
    private String commitStatus;

    public CardCommitStatus(String commitId, String commitStatus) {
        this.commitId = commitId;
        this.commitStatus = commitStatus;
    }

    public CardCommitStatus() {
    }

    public String getCommitId() {
        return commitId;
    }

    public String getCommitStatus() {
        return commitStatus;
    }
}
