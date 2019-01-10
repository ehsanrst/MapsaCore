package com.mapsa.core;

import com.mapsa.core.commits.CommitResponse;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.*;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.logger.UserCommitLogger;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;

public class UserIMDImple implements UserIMD {
    UserCommitLogger logger = new UserCommitLogger();
    UserDAL userDAL = new UserDAL();
    @Override
    public void addUser(User user) {
        userDAL.saveUser(user);
    }

    @Override
    public User findUserById(String userId) {
        return userDAL.loadUserById(userId);
    }

    @Override
    public User findUserByNationalId(String nationalId) {
        return userDAL.loadUserByNationalId(nationalId);
    }

    @Override
    public UserCommit findCommitById(String CId) {
        return logger.findCommitById(CId).getCommit();
    }

    @Override
    public void addCommitResponse(UserCommitResponse response) {
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(response);
        logger.saveCommitResponse(userCommitResponseLog);
    }

    @Override
    public CommitResponse findCommitResponseById(String CID) {
        UserCommitResponseLog responseLog = logger.findCommitResponseByCommitId(CID);
        if (responseLog==null){
            return null;
        }
        return responseLog.getResponse();
    }

    @Override
    public void addCommitStatus(UserCommitStatus status) {
        logger.saveCommitStatus(status);
    }

    @Override
    public UserCommitStatus findCommitStatusById(String id) {
        return logger.findCommitStatus(id);
    }
}
