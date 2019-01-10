package com.mapsa.core.card;

import javax.persistence.*;

@Entity
public class Card {
    @Id
    private String cardID;
    private String cvv2;
    private String password;
    private boolean isActive;
    private String accountId;
    private String lastCommitId;

    public Card() {
    }

    public Card(String cardID, String cvv2, String password, String accountId, String commitID) {
        this.cardID = cardID;
        this.cvv2 = cvv2;
        this.password = password;
        this.isActive = true;
        this.accountId = accountId;
        this.lastCommitId = commitID;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCardId() {
        return cardID;
    }

    public String getCvv2() {
        return cvv2;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getLastCommitId() {
        return lastCommitId;
    }

    public void activate(String commitID) {
        isActive = true;
        lastCommitId = commitID;
    }

    public void deactive(String commitID) {
        isActive = false;
        lastCommitId = commitID;
    }

    public boolean checkCardPass(String pass, String commitID) {
        if (isActive && pass.equals(password)) {
            lastCommitId = commitID;
            return true;
        }
        return false;
    }

    public boolean upDateCardPass(String oldPass, String newPass, String commitID) {
        if (isActive && oldPass.equals(password)) {
            password = newPass;
            lastCommitId = commitID;
            return true;
        }
        return false;
    }
}
