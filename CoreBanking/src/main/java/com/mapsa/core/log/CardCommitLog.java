package com.mapsa.core.log;

import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.commits.card.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class CardCommitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long logId;
    private String commitId;
    @Lob
    @Column (length = 10000)
    private CardCommit commit;
    private String time;
    private String previousCommitId;

    public CardCommitLog() {
    }

    public CardCommitLog(CardCommit commit, String previousCommitId) {
        this.commit = commit;
        this.commitId = commit.getCUID();
        this.previousCommitId = previousCommitId;
        this.setTime();
        this.previousCommitId = previousCommitId;
    }

    public CardCommit getCommit() {
        return this.commit;
    }

    public String getCommitType() {
        if (commit instanceof AddCardCommit) {
            return "AddCard";
        } else if (commit instanceof CheckCardPassCommit) {
            return "CheckCardPass";
        } else if (commit instanceof DeactivateCardCommit) {
            return "DeactivateCard";
        } else if (commit instanceof GetCardAccountCommit) {
            return "GetCardAccount";
        } else if (commit instanceof ReactivateCardCommit) {
            return "ReactivateCard";
        } else if (commit instanceof UpdateCardPassCommit) {
            return "UpdateCardPass";
        }
        return null;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        this.time = currentTime;
    }

    public String getPreviousCommitId() {
        return previousCommitId;
    }

    public long getLogId() {
        return logId;
    }
}
