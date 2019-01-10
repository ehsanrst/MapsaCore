package com.mapsa.core.commits.user;

import com.mapsa.core.UserIMD;
import com.mapsa.core.commits.account.DepositCommitResponse;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.user.User;

import java.io.Serializable;

public class DeactivateUserCommit extends UserCommit implements Serializable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void apply(UserIMD userIMD){
        User user=userIMD.findUserById(this.userId);
        if (user!=null && user.isActive()){
            user.deactivate(this.getCUID());
            userIMD.addUser(user);
            userIMD.addCommitResponse(new DeactiveUserCommitResponse(this,true));
            userIMD.addCommitStatus(new UserCommitStatus(this.getCUID(),"Done"));
        }else {
            userIMD.addCommitStatus(new UserCommitStatus(this.getCUID(), "Failed"));
            userIMD.addCommitResponse(new DeactiveUserCommitResponse(this, false));
        }
    }
}
