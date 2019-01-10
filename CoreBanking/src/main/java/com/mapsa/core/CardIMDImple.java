package com.mapsa.core;

import com.mapsa.core.card.Card;
import com.mapsa.core.card.CardDAL;
import com.mapsa.core.commits.card.AddCardCommit;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CardCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.CardCommitLog;
import com.mapsa.core.log.CardCommitResponseLog;
import com.mapsa.core.logger.CardCommitLogger;

public class CardIMDImple implements CardIMD {
    CardDAL cardDAL = new CardDAL();
    CardCommitLogger cardCommitLogger = new CardCommitLogger();

    @Override
    public void addCard(Card card) {
        cardDAL.saveCard(card);
    }

    @Override
    public Card findCardById(String id) {
        return cardDAL.getCardById(id);
    }

    @Override
    public CardCommit findCommitById(String id) {
        CardCommitLog cardCommitLog= cardCommitLogger.findCommitById(id);
        if (cardCommitLog == null){
            return null;
        }
        return cardCommitLog.getCommit();
    }

    @Override
    public void AddCardCommitResponse(CardCommitResponse cardCommitResponse) {
        cardCommitLogger.saveCommitResponse(new CardCommitResponseLog(cardCommitResponse));
    }

    @Override
    public CardCommitResponse findCardCommitResponseById(String id) {
        CardCommitResponseLog cardCommitResponseLog = cardCommitLogger.findCommitResponseByCommitId(id);
        if (cardCommitResponseLog == null){
            return null;
        }
        return cardCommitResponseLog.getResponse();
    }

    @Override
    public void addCommitStatus(CommitStatus commitStatus) {
        cardCommitLogger.saveCommitStatus((CardCommitStatus) commitStatus);
    }

    @Override
    public CommitStatus findCommitStatusById(String id) {
        CommitStatus commitStatus = cardCommitLogger.loadCommitStatusbyId(id);
        return commitStatus;
    }
}
