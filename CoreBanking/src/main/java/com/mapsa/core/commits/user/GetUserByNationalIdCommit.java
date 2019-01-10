package com.mapsa.core.commits.user;

import com.mapsa.core.UserIMD;
import com.mapsa.core.UserIMDImple;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;

import java.io.Serializable;

public class GetUserByNationalIdCommit extends UserCommit implements Serializable {

    private String nationalId;

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationaltId) {
        this.nationalId = nationaltId;
    }

    @Override
    public void apply(UserIMD userIMD){
        User user=userIMD.findUserByNationalId(this.nationalId);
        if(user!=null) {
            userIMD.addUser(user);
            userIMD.addCommitStatus(new UserCommitStatus(this.getCUID(), "Done"));
            userIMD.addCommitResponse(new GetUserByNationalIdCommitResponse(this, user));
        }else {
            userIMD.addCommitStatus(new UserCommitStatus(this.getCUID(),"Failed"));
        }
    }
}
