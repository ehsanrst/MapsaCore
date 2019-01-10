package com.mapsa.core;
import com.mapsa.core.card.Card;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CommitStatus;

public interface CardIMD {
    abstract void addCard(Card card);
    abstract Card findCardById(String id);
    abstract CardCommit findCommitById(String id);
    abstract void AddCardCommitResponse(CardCommitResponse cardCommitResponse);
    abstract CardCommitResponse findCardCommitResponseById(String id);
    abstract void addCommitStatus(CommitStatus commitStatus);
    abstract CommitStatus findCommitStatusById(String id);
}