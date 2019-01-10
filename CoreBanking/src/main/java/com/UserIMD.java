package com;

import com.mapsa.core.commits.Commit;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.UserCommit;
import com.mapsa.core.commits.user.UserCommitResponse;
import com.mapsa.core.user.User;
import jdk.net.SocketFlow;

import javax.xml.ws.Response;

public abstract class UserIMD {
  abstract void addUser(User user);
  abstract User findUserById(String userId);
  abstract UserCommit findCommitById(String CId);
  abstract void addResponse(UserCommitResponse response);
  abstract Response findResponseById(String CID);
  abstract void addCommitStatus(UserCommitStatus status);
  abstract UserCommitStatus findCommitStatusById(String id);
}
