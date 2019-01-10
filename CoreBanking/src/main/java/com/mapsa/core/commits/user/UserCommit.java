package com.mapsa.core.commits.user;

import com.mapsa.core.UserIMD;
import com.mapsa.core.commits.Commit;

import java.io.Serializable;

public abstract class UserCommit extends Commit implements Serializable {
    public void apply(UserIMD userIMD){

    }
}
