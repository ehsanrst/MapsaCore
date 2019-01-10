package com.mapsa.core;

import com.mapsa.core.commits.CommitResponse;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.UserCommit;
import com.mapsa.core.commits.user.UserCommitResponse;
import com.mapsa.core.user.User;

public abstract interface UserIMD {
    abstract void addUser(User user);
    abstract User findUserById(String userId);
    abstract User findUserByNationalId(String nationalId);
    abstract UserCommit findCommitById(String CId);
    abstract void addCommitResponse(UserCommitResponse response);
    abstract CommitResponse findCommitResponseById(String CID);
    abstract void addCommitStatus(UserCommitStatus status);
    abstract UserCommitStatus findCommitStatusById(String id);

}