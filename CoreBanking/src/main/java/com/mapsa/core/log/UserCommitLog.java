package com.mapsa.core.log;

import com.mapsa.core.commits.user.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class UserCommitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long logId;
    private String commitId;
    @Lob
    @Column(length = 10000)
    private UserCommit commit;
    private String time;
    private String previousCommitId;

    public UserCommitLog() {

    }

    public UserCommitLog(UserCommit commit, String previous) {
        this.commit = commit;
        this.commitId = commit.getCUID();
        this.setTime();
        this.previousCommitId = previous;
    }

    public UserCommit getCommit() {
        return this.commit;
    }

    public String getCommitType() {
        if (commit instanceof AddUserAccountCommit) {
            return "AddUserAccount";
        } else if (commit instanceof AddUserCommit) {
            return "AddUser";
        } else if (commit instanceof DeactivateUserAccountCommit) {
            return "DeactivateUserAccount";
        } else if (commit instanceof DeactivateUserCommit) {
            return "DeactivateUser";
        } else if (commit instanceof GetUserAccountsCommit) {
            return "GetUserAccounts";
        } else if (commit instanceof GetUserByIdCommit) {
            return "GetUserById";
        } else if (commit instanceof GetUserByNationalIdCommit) {
            return "GetUserByNationalId";
        } else if (commit instanceof ReactivateUserAccountCommit) {
            return "ReactivateUserAccount";
        }else if (commit instanceof ReactivateUserCommit) {
            return "ReactivateUser";
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

}
