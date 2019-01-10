package com.mapsa.core.log;

import com.mapsa.core.commits.user.*;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table
public class UserCommitResponseLog{

    @Id
    private String commitId;

    @Lob@Column(length = 10000)

    private UserCommitResponse response;

    private String time;

    public UserCommitResponseLog() {
    }

    public UserCommitResponseLog(UserCommitResponse response) {
        this.response = response;
        setTime();
        this.commitId = response.getCommitId();
    }

    public String getCommitId() {
        return commitId;
    }

    public UserCommitResponse getResponse() {
        return response;
    }

    public String getResponseType() {
        if (response instanceof AddUserCommitResponse) {
            return "AddUserCommitResponse";
        } else if (response instanceof AddUserAccountCommitResponse) {
            return "AddUserAccountCommitResponse";
        } else if (response instanceof GetUserByNationalIdCommitResponse) {
            return "GetUserByNationalIdCommitResponse";
        }else if (response instanceof ReactiveUserCommitResponse) {
            return "ReactivateUserCommitResponse";
        }else if (response instanceof DeactiveUserCommitResponse) {
            return "DeactivateUserCommitResponse";
        }else if (response instanceof GetUserAccountsCommitResponse) {
            return "GetUserAccountsCommitResponse";
        }else if (response instanceof GetUserByIdCommitResponse) {
            return "GetUserByIdCommitResponse";
        }else if (response instanceof ReactiveUserAccountCommitResponse) {
            return "ReactivateUserAccountCommitResponse";
        }else if (response instanceof DeactiveUserAccountCommitResponse) {
            return "DeactivateUserAccountCommitResponse";
        }
        return null;
    }

    public void setTime() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        this.time = currentTime;
    }
}
