package com.mapsa.core.commits.card;

import java.io.Serializable;

public class CheckCardPassCommit extends CardCommit implements Serializable {
    private String cardId;
    private String cardPass;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardPass() {
        return cardPass;
    }

    public void setCardPass(String cardPass) {
        this.cardPass = cardPass;
    }
}
