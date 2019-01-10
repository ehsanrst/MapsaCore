package com.mapsa.core.commits.user;
import com.mapsa.core.user.User;

import java.io.Serializable;

public class GetUserByNationalIdCommitResponse extends UserCommitResponse implements Serializable {
    private User user;

    public GetUserByNationalIdCommitResponse(GetUserByNationalIdCommit getUserByNationalIdCommit,User user) {
        this.user=user;
        super.setCommitId(getUserByNationalIdCommit.getCUID());
    }

    public User getUser() {
        return user;
    }

}
