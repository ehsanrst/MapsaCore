package com.mapsa.core.account;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    private String accountId;
    private String userId;
    private Boolean isBlocked;
    private Boolean isActive;
    private String balance;
    private String lastCommitId;

    public Account(String accountId, String userId, String commitID) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = "0";
        this.isActive = true;
        this.isBlocked = false;
        this.lastCommitId = commitID;
    }
    public Account(){

    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBalance() {
        return balance;
    }

    public void block(String commitID) {
        isBlocked = true;
        lastCommitId = commitID;
    }

    public void unblock(String commitID) {
        isBlocked = false;
        lastCommitId = commitID;

    }

    public void activate(String commitID) {
        isActive = true;
        lastCommitId = commitID;
    }

    public void deactive(String commitID) {
        isActive = false;
        lastCommitId = commitID;

    }

    public String getLastCommitId() {
        return lastCommitId;
    }

    public boolean deposit(String amount, String commitID) {
        if (isActive && !isBlocked) {
            BigDecimal bdAmount = new BigDecimal(amount);
            if (bdAmount.compareTo(new BigDecimal("0")) != -1) {
                if (accountId != null) {
                    BigDecimal bdBalance = new BigDecimal(balance);
                    balance = String.valueOf(bdBalance.add(bdAmount));
                    lastCommitId = commitID;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean withdraw(String amount, String commitID) {
        if (isActive && !isBlocked) {
            BigDecimal bdAmount = new BigDecimal(amount);
            BigDecimal bdBalance = new BigDecimal(balance);
            if (bdAmount.compareTo(bdBalance) != 1 && !(amount.startsWith("-"))) {
                balance = String.valueOf(bdBalance.subtract(bdAmount));
                lastCommitId = commitID;
                return true;
            }
        }
        return false;
    }

}