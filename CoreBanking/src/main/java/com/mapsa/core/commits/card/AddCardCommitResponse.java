package com.mapsa.core.commits.card;

import java.io.Serializable;

public class AddCardCommitResponse extends CardCommitResponse implements Serializable {
    private String cardNumber;
    private String cardPass;

    public AddCardCommitResponse(AddCardCommit addCardCommit, String cardNumber, String cardPass) {
        this.cardNumber = cardNumber;
        this.cardPass = cardPass;
        super.setCommitId(addCardCommit.getCUID());
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardPass() {
        return cardPass;
    }
}
