package com.mapsa.core.account;

import com.mapsa.core.user.User;

import javax.persistence.*;
import java.io.Serializable;

//@Entity
public class AccountStatus implements Serializable {
    //@Id
    public String accountId;
    public boolean isActive;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    //    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    public User getUser() {
//        return user;
//   }
////
//    public void setUser(User user) {
//        this.user = user;
//    }
}
