package com.mapsa.core.user;

import com.mapsa.core.account.AccountStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class User implements Serializable {
    @Id
    private String id;
    private String name;
    private String familyName;
    private String nationalId;
    private boolean isActive;

    public void setAccounts(ArrayList<AccountStatus> accounts) {
        this.accounts = accounts;
    }

    //@OneToMany (mappedBy = "user", fetch = FetchType.EAGER)
    @Lob
    private ArrayList<AccountStatus> accounts=new ArrayList<>();
    private String lastCommit;

    public User() {
    }

    public User(String id, String name, String familyName, String nationalId, String lastCommit) {
        this.id = id;
        this.name = name;
        this.familyName = familyName;
        this.nationalId = nationalId;
        this.isActive = true;
        this.lastCommit = lastCommit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<AccountStatus> getAccounts() {
        return accounts;
    }


    public boolean addAccount(String accountId, String commitId) {
        for (AccountStatus status : accounts) {
            if (status.accountId.equals(accountId)) {
                this.setLastCommit(commitId);
                return false;
            }
        }
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.accountId = accountId;
        accountStatus.isActive = true;
        this.accounts.add(accountStatus);
        this.setLastCommit(commitId);
        return true;
    }

    public String getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
    }

    public boolean activate (String commitId){
        this.isActive = true;
        this.lastCommit = commitId;
        return true;
    }

    public boolean deactivate (String commitId){
        this.isActive = false;
        this.lastCommit = commitId;
        return true;
    }

    public boolean deactivateAccount(String accountId,String commitId){
        if (accounts.size() > 0) {
            for (AccountStatus account : accounts) {
                if (account.accountId.equals(accountId)) {
                    account.isActive = false;
                    lastCommit = commitId;
                    return true;
                }
            }
        }
        lastCommit = commitId;
        return false;
    }
}
