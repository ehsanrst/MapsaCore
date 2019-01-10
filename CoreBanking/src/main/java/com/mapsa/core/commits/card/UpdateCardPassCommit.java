package com.mapsa.core.commits.card;

import java.io.Serializable;

public class UpdateCardPassCommit extends CardCommit implements Serializable {
    private String cardId;
    private String newCardPass;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getNewCardPass() {
        return newCardPass;
    }

    public void setNewCardPass(String newCardPass) {
        this.newCardPass = newCardPass;
    }
}
