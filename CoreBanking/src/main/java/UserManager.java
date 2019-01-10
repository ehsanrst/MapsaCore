import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.AddUserCommit;
import com.mapsa.core.commits.user.AddUserCommitResponse;
import com.mapsa.core.commits.user.UserCommit;
import com.mapsa.core.commits.user.UserCommitResponse;
import com.mapsa.core.commits.user.GetUserAccountsCommit;
import com.mapsa.core.commits.user.GetUserAccountsCommitResponse;
import com.mapsa.core.commits.user.AddUserAccountCommit;
import com.mapsa.core.commits.user.AddUserAccountCommitResponse;
import com.mapsa.core.commits.user.ReactivateUserCommit;
import com.mapsa.core.commits.user.ReactiveUserCommitResponse;
import com.mapsa.core.commits.user.*;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.logger.UserCommitLogger;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

public class UserManager {
    private List<User> users;
    private List<UserCommitStatus> commitStatusList;
    private List<UserCommitResponse> commitResponseList;
    private Queue<UserCommit> commitQueue;
    private UserCommitLogger logger;
    private UserDAL userDAL;

    public UserManager() {
        start();
    }

    public void start() {
        userDAL = new UserDAL();
        logger = new UserCommitLogger();
        users = userDAL.loadUsers();
        if (users == null) {
            users = new ArrayList<>();
        }
        commitStatusList = logger.loadAllCommitStatus();
        if (commitStatusList == null) {
            commitStatusList = new ArrayList<>();
        }
        if (commitResponseList == null) {
            commitResponseList = new ArrayList<UserCommitResponse>();
        }
    }

    public void process(UserCommit userCommit) {
//        if (UserCommit instanceof) {
//            boolean ok =
//            if (ok) {
//                commitQueue.remove();
//            }
//        }
//        if (UserCommit instanceof) {
//            boolean ok =
//            if (ok) {
//                commitQueue.remove();
//            }
//        }
//        if (UserCommit instanceof) {
//            boolean ok =
//            if (ok) {
//                commitQueue.remove();
//            }
//        }
        //...
    }

    public boolean getUserByIdConsumer(GetUserByIdCommit getUserByIdCommit) {
        for (UserCommitStatus commitstus : commitStatusList) {
            if (commitstus.getCommitId().equals(getUserByIdCommit.getCUID())) {
                return true;
            }
        }

        for (User user : users) {
            if (user.getId().equals(getUserByIdCommit.getUserId())) {
                UserCommitLog userCommitLog = new UserCommitLog(getUserByIdCommit, user.getLastCommit());
                logger.saveCommit(userCommitLog);
                user.setLastCommit(getUserByIdCommit.getCUID());
                userDAL.saveUser(user);
                UserCommitStatus userCommitStatus = new UserCommitStatus(getUserByIdCommit.getCUID(), "done");
                commitStatusList.add(userCommitStatus);
                logger.saveCommitStatus(userCommitStatus);
                UserCommitResponse response = new GetUserByIdCommitResponse(getUserByIdCommit, user);
                UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(response);
                logger.saveCommitResponse(userCommitResponseLog);
                commitResponseList.add(response);
                return true;
            }
        }
        UserCommitLog userCommitLog = new UserCommitLog(getUserByIdCommit, null);
        logger.saveCommit(userCommitLog);
        UserCommitStatus userCommitStatus = new UserCommitStatus(getUserByIdCommit.getCUID(), "failed");
        commitStatusList.add(userCommitStatus);
        logger.saveCommitStatus(userCommitStatus);
        GetUserByIdCommitResponse getUserByIdCommitResponse = new GetUserByIdCommitResponse(getUserByIdCommit, null);
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(getUserByIdCommitResponse);
        logger.saveCommitResponse(userCommitResponseLog);
        commitResponseList.add(getUserByIdCommitResponse);
        return false;
    }

    public boolean getUserByNationalIdCommitConsumer(GetUserByNationalIdCommit getUserByNationalIdCommit) {
        for (User user : users) {
            if (user.getNationalId().equals(getUserByNationalIdCommit.getNationalId())) {
                String tempCommitId = user.getLastCommit();
                UserCommitStatus userCommitStatus = new UserCommitStatus(getUserByNationalIdCommit.getCUID(), "Done");
                commitStatusList.add(userCommitStatus);
                UserCommitLog userCommitLog = new UserCommitLog(getUserByNationalIdCommit, tempCommitId);
                GetUserByNationalIdCommitResponse commitResponse = new GetUserByNationalIdCommitResponse(getUserByNationalIdCommit, user);
                commitResponseList.add(commitResponse);
                UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(commitResponse);
                logger.saveCommitStatus(userCommitStatus);
                logger.saveCommit(userCommitLog);
                logger.saveCommitResponse(userCommitResponseLog);
                user.setLastCommit(getUserByNationalIdCommit.getCUID());
                userDAL.saveUser(user);
                return true;
            }
        }
        UserCommitStatus userCommitStatus = new UserCommitStatus(getUserByNationalIdCommit.getCUID(), "Failed");
        commitStatusList.add(userCommitStatus);
        UserCommitLog userCommitLog = new UserCommitLog(getUserByNationalIdCommit, null);
        GetUserByNationalIdCommitResponse commitResponse = new GetUserByNationalIdCommitResponse(getUserByNationalIdCommit, null);
        commitResponseList.add(commitResponse);
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(commitResponse);
        logger.saveCommitStatus(userCommitStatus);
        logger.saveCommit(userCommitLog);
        logger.saveCommitResponse(userCommitResponseLog);
        return false;
    }

    public boolean addUserCommitConsumer(AddUserCommit commit) {
        for (int j = 0; j < commitStatusList.size(); j++) {
            if (commitStatusList.get(j).getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getNationalId().equals(commit.getNationalId())) {
                UserCommitStatus userCommitStatus = new UserCommitStatus(commit.getCUID(), "Failed");
                this.commitStatusList.add(userCommitStatus);

                AddUserCommitResponse addUserCommitResponse = new AddUserCommitResponse(commit, null);
                this.commitResponseList.add(addUserCommitResponse);

                UserCommitLog userCommitLog = new UserCommitLog(commit, users.get(i).getLastCommit());
                UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(addUserCommitResponse);
                logger.saveCommit(userCommitLog);
                logger.saveCommitResponse(userCommitResponseLog);
                logger.saveCommitStatus(userCommitStatus);

                users.get(i).setLastCommit(commit.getCUID());
                userDAL.saveUser(users.get(i));
                return false;
            }
        }
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String userId = simpleDateFormat.format(date) + commit.getNationalId().substring(4);

        User user = new User(userId, commit.getName(), commit.getFamilyName(), commit.getNationalId(), commit.getCUID());
        user.setAccounts(null);
        this.users.add(user);
        this.userDAL.saveUser(user);

        UserCommitStatus userCommitStatus = new UserCommitStatus(commit.getCUID(), "Done");
        this.commitStatusList.add(userCommitStatus);

        AddUserCommitResponse addUserCommitResponse = new AddUserCommitResponse(commit, userId);
        this.commitResponseList.add(addUserCommitResponse);

        UserCommitLog userCommitLog = new UserCommitLog(commit, null);
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(addUserCommitResponse);
        logger.saveCommit(userCommitLog);
        logger.saveCommitResponse(userCommitResponseLog);
        logger.saveCommitStatus(userCommitStatus);
        return true;

    }

    public boolean getUserAccountCommitConsumer(GetUserAccountsCommit commit) {
        String previous = "";
        for (UserCommitStatus status : this.commitStatusList) {
            if (status.getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        for (User user : users) {
            if (user.getId().equals(commit.getUserId())) {
                previous = user.getLastCommit();
                user.setLastCommit(commit.getCUID());
                userDAL.saveUser(user);
                ArrayList<AccountStatus> accounts = user.getAccounts();
                ArrayList<AccountStatus> activeAccounts = new ArrayList<>();
                for (AccountStatus status : accounts) {
                    if (status.isActive) {
                        activeAccounts.add(status);
                    }
                }
                UserCommitLog userCommitLog = new UserCommitLog(commit, previous);
                UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Done");
                commitStatusList.add(commitStatus);
                GetUserAccountsCommitResponse getUserAccountsCommitResponse = new GetUserAccountsCommitResponse(commit, activeAccounts);
                commitResponseList.add(getUserAccountsCommitResponse);
                UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(getUserAccountsCommitResponse);
                logger.saveCommit(userCommitLog);
                logger.saveCommitStatus(commitStatus);
                logger.saveCommitResponse(userCommitResponseLog);
                return true;
            }
        }
        UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Failed");
        commitStatusList.add(commitStatus);
        UserCommitLog userCommitLog = new UserCommitLog(commit, previous);
        GetUserAccountsCommitResponse getUserAccountsCommitResponse = new GetUserAccountsCommitResponse(commit, null);
        commitResponseList.add(getUserAccountsCommitResponse);
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(getUserAccountsCommitResponse);
        logger.saveCommit(userCommitLog);
        logger.saveCommitStatus(commitStatus);
        logger.saveCommitResponse(userCommitResponseLog);
        return false;
    }

    public boolean deactivateUserCommitConsumer(DeactivateUserCommit commit) {
        try {
            String preivous = null;
            for (UserCommitStatus ucs : commitStatusList) {
                if (ucs.getCommitId().equals(commit.getCUID())) {
                    return true;
                }
            }
            for (User user : users) {
                if (user.getId().equals(commit.getUserId())) {
                    preivous = user.getLastCommit();
                    user.deactivate(commit.getCUID());
                    userDAL.saveUser(user);
                    UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Done");
                    commitStatusList.add(commitStatus);
                    UserCommitLog userCommitLog = new UserCommitLog(commit, preivous);
                    DeactiveUserCommitResponse deactivateUserCommitResponse = new DeactiveUserCommitResponse(commit, true);
                    commitResponseList.add(deactivateUserCommitResponse);
                    UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(deactivateUserCommitResponse);
                    logger.saveCommit(userCommitLog);
                    logger.saveCommitStatus(commitStatus);
                    logger.saveCommitResponse(userCommitResponseLog);
                    return true;
                }
            }
            UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Failed");
            commitStatusList.add(commitStatus);
            UserCommitLog userCommitLog = new UserCommitLog(commit, null);
            DeactiveUserCommitResponse deactivateUserCommitResponse = new DeactiveUserCommitResponse(commit, false);
            commitResponseList.add(deactivateUserCommitResponse);
            UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(deactivateUserCommitResponse);
            logger.saveCommit(userCommitLog);
            logger.saveCommitStatus(commitStatus);
            logger.saveCommitResponse(userCommitResponseLog);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean AddUserAccountCommitConsumer(AddUserAccountCommit addUserAccountCommit) {
        for (UserCommitStatus userCommitStatus : commitStatusList) {
            if (userCommitStatus.getCommitId().equals(addUserAccountCommit.getCUID())) {
                return true;
            }
        }
        String prievousId = null;
        User user1 = null;
        for (User user : users) {
            if (user.getId().equals(addUserAccountCommit.getUserId())) {
                user1 = user;
                prievousId = user.getLastCommit();

                if (user.addAccount(addUserAccountCommit.getAccountId(), addUserAccountCommit.getCUID())) {
                    userDAL.saveUser(user);
                    UserCommitLog userCommitLog = new UserCommitLog(addUserAccountCommit, prievousId);
                    logger.saveCommit(userCommitLog);
                    UserCommitStatus userCommitStatus = new UserCommitStatus(addUserAccountCommit.getCUID(), "done");
                    commitStatusList.add(userCommitStatus);
                    logger.saveCommitStatus(userCommitStatus);
                    AddUserAccountCommitResponse addUserAccountCommitResponse = new AddUserAccountCommitResponse(addUserAccountCommit, true);
                    commitResponseList.add(addUserAccountCommitResponse);
                    UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(addUserAccountCommitResponse);
                    logger.saveCommitResponse(userCommitResponseLog);
                    return true;
                }
            }
        }
        UserCommitLog userCommitLog = new UserCommitLog(addUserAccountCommit, prievousId);
        logger.saveCommit(userCommitLog);
        userDAL.saveUser(user1);
        UserCommitStatus userCommitStatus = new UserCommitStatus(addUserAccountCommit.getCUID(), "failed");
        commitStatusList.add(userCommitStatus);
        logger.saveCommitStatus(userCommitStatus);
        AddUserAccountCommitResponse addUserAccountCommitResponse = new AddUserAccountCommitResponse(addUserAccountCommit, false);
        commitResponseList.add(addUserAccountCommitResponse);
        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(addUserAccountCommitResponse);
        logger.saveCommitResponse(userCommitResponseLog);
        return false;


    }

    public boolean reactivateUserCommitConsumer(ReactivateUserCommit commit) {
        try {
            String previousCommitId;
            for (UserCommitStatus status : this.commitStatusList) {
                if (status.getCommitId().equals(commit.getCUID())) {
                    return true;
                }
            }
            for (User user : this.users) {
                if (user.getId().equals(commit.getUserId())) {
                    previousCommitId = user.getLastCommit();
                    user.activate(commit.getCUID());
                    this.userDAL.saveUser(user);
                    UserCommitStatus status = new UserCommitStatus(commit.getCUID(), "Done");
                    this.commitStatusList.add(status);
                    ReactiveUserCommitResponse response = new ReactiveUserCommitResponse(commit, true);
                    this.commitResponseList.add(response);
                    UserCommitLog log = new UserCommitLog(commit, previousCommitId);
                    UserCommitResponseLog responseLog = new UserCommitResponseLog(response);
                    logger.saveCommit(log);
                    logger.saveCommitStatus(status);
                    logger.saveCommitResponse(responseLog);
                    return true;
                }
            }
            UserCommitStatus status = new UserCommitStatus(commit.getCUID(), "Failed");
            this.commitStatusList.add(status);
            ReactiveUserCommitResponse response = new ReactiveUserCommitResponse(commit, false);
            this.commitResponseList.add(response);
            UserCommitResponseLog responseLog = new UserCommitResponseLog(response);
            UserCommitLog log = new UserCommitLog(commit, null);
            logger.saveCommit(log);
            logger.saveCommitStatus(status);
            logger.saveCommitResponse(responseLog);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deactivateUserAccountCommitConsumer(DeactivateUserAccountCommit commit) {
        User user = new User();
        user = null;
        String previous = null;
        for (UserCommitStatus status : commitStatusList) {
            if (status.getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        for (User user1 : users) {
            if (user1.getId().equals(commit.getUserId())) {
                user = user1;
                previous = user.getLastCommit();
                UserCommitLog accountCommitLog = new UserCommitLog(commit, previous);
                logger.saveCommit(accountCommitLog);
                if (user.deactivateAccount(commit.getAccountId(), commit.getCUID())) {
                    userDAL.saveUser(user);
                    UserCommitStatus status = new UserCommitStatus(commit.getCUID(), "done");
                    logger.saveCommitStatus(status);
                    commitStatusList.add(status);
                    UserCommitResponse response = new DeactiveUserAccountCommitResponse(commit, true);
                    UserCommitResponseLog responseLog = new UserCommitResponseLog(response);
                    logger.saveCommitResponse(responseLog);
                    commitResponseList.add(response);
                    return true;
                }
            }
        }
        UserCommitLog accountCommitLog = new UserCommitLog(commit, previous);
        logger.saveCommit(accountCommitLog);
        userDAL.saveUser(user);
        UserCommitStatus status = new UserCommitStatus(commit.getCUID(), "failed");
        logger.saveCommitStatus(status);
        commitStatusList.add(status);
        UserCommitResponse response = new DeactiveUserAccountCommitResponse(commit, false);
        UserCommitResponseLog responseLog = new UserCommitResponseLog(response);
        logger.saveCommitResponse(responseLog);
        commitResponseList.add(response);
        return false;
    }

    public boolean reactivateUserAccountCommitConsumer(ReactivateUserAccountCommit commit) {
        try {
            String previous;
            for (User user : users) {
                if (user.getId().equals(commit.getUserId())) {
                    previous = user.getLastCommit();
                    ArrayList<AccountStatus> accounts = user.getAccounts();
                    for (AccountStatus account : accounts) {
                        if (account.getAccountId().equals(commit.getAccountId())) {
                            account.isActive = true;
                            userDAL.saveUser(user);
                            UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Done");
                            commitStatusList.add(commitStatus);
                            ReactiveUserAccountCommitResponse reactiveUserAccountCommitResponse = new ReactiveUserAccountCommitResponse(commit, true);
                            commitResponseList.add(reactiveUserAccountCommitResponse);
                            UserCommitLog userCommitLog = new UserCommitLog(commit, previous);
                            UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(reactiveUserAccountCommitResponse);
                            logger.saveCommit(userCommitLog);
                            logger.saveCommitStatus(commitStatus);
                            logger.saveCommitResponse(userCommitResponseLog);
                            return true;
                        } else {
                            UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(), "Failed");
                            commitStatusList.add(commitStatus);
                            ReactiveUserAccountCommitResponse reactiveUserAccountCommitResponse = new ReactiveUserAccountCommitResponse(commit, false);
                            commitResponseList.add(reactiveUserAccountCommitResponse);
                            UserCommitLog userCommitLog = new UserCommitLog(commit, null);
                            UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(reactiveUserAccountCommitResponse);
                            logger.saveCommit(userCommitLog);
                            logger.saveCommitStatus(commitStatus);
                            logger.saveCommitResponse(userCommitResponseLog);
                            return false;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
