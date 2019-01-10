package com.mapsa.core.log;

import com.mapsa.core.commits.card.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class CardCommitResponseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String commitId;
    @Lob@Column (length = 10000)
    private CardCommitResponse response;
    private String time;

    public CardCommitResponseLog() {
    }

    public CardCommitResponseLog(CardCommitResponse response) {
        this.response = response;
        setTime();
        this.commitId = response.getCommitId();
    }

    public String getCommitId() {
        return commitId;
    }

    public CardCommitResponse getResponse() {
        return response;
    }

    public String getResponseType() {
        if (response instanceof AddCardCommitResponse) {
            return "AddCardCommit";
        } else if (response instanceof CheckCardPassResponse) {
            return "CheckCardPassCommit";
        } else if (response instanceof DeactivateCardResponse) {
            return "DeactivateCardCommit";
        } else if (response instanceof GetCardAccountResponse) {
            return "GetCardAccountCommit";
        } else if (response instanceof ReactivateCardResponse) {
            return "ReactivateCardCommit";
        } else if (response instanceof UpdateCardPassResponse) {
            return "UpdateCardPassCommit";
        }
        return null;
    }

    public void setTime() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        this.time = currentTime;
    }
}
