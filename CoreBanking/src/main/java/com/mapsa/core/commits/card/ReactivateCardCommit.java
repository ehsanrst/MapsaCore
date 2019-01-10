package com.mapsa.core.commits.card;

import java.io.Serializable;

public class ReactivateCardCommit extends CardCommit implements Serializable {
    private String cardId;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
