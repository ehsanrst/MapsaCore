package com.mapsa.core.commits.user;

import com.mapsa.core.UserIMDImple;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.user.User;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUserCommit extends UserCommit implements Serializable {
    private String name;
    private String familyName;
    private String nationalId;

    public AddUserCommit(String name, String familyName, String nationalId) {
        this.name = name;
        this.familyName = familyName;
        this.nationalId = nationalId;
    }

    public String getName() {
        return name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void apply(UserIMDImple imd) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String userId = simpleDateFormat.format(date) + this.getNationalId().substring(4);
        User user = new User(userId, this.getName(), this.getFamilyName(), this.getNationalId(), null);
        user.setAccounts(null);
        imd.addUser(user);
        UserCommitStatus userCommitStatus = new UserCommitStatus(this.getCUID(), "Done");
        imd.addCommitStatus(userCommitStatus);
        AddUserCommitResponse addUserCommitResponse = new AddUserCommitResponse(this, userId);
        imd.addCommitResponse(addUserCommitResponse);
    }
}
