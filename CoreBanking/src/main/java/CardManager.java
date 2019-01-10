import com.mapsa.core.card.Card;
import com.mapsa.core.card.CardDAL;
import com.mapsa.core.commits.card.AddCardCommit;
import com.mapsa.core.commits.card.AddCardCommitResponse;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CardCommitStatus;
import com.mapsa.core.log.CardCommitLog;
import com.mapsa.core.log.CardCommitResponseLog;
import com.mapsa.core.logger.CardCommitLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class CardManager {

    private CardDAL cardDAL;
    private List<Card> cards;
    private List<CardCommitStatus> commitStatusList;
    private List<CardCommitResponse> commitResponseList;
    private CardCommitLogger logger;
    private Queue<CardCommit> commitQueue;

    public CardManager() {
        start();
    }

    private void start() {
        cardDAL = new CardDAL();
        logger = new CardCommitLogger();
        cards = cardDAL.loadCards();
        if(cards==null){
            cards=new ArrayList<Card>();
        }
        commitStatusList = logger.loadCommitStatus();
        commitResponseList = new ArrayList<>();
    }

    public boolean CreateCardCommitConsumer(AddCardCommit commit) {
        for (CardCommitStatus commitstus : commitStatusList) {
            if (commitstus.getCommitId().equals(commit.getCUID())) {
                return true;
            }
        }
        String cardId = makeCardNumber();
        String cvv2 = makeRandom4digit();
        String password = makeRandom4digit();
        Card newCard = new Card(cardId, cvv2, password, commit.getAccountId(), commit.getCUID());
        System.out.println("AccountId : "+newCard.getAccountId());
        System.out.println("LastCommitId : "+newCard.getLastCommitId());
        System.out.println("pass : "+newCard.getPassword());
        cards.add(newCard);
        boolean o=cardDAL.saveCard(newCard);
        System.out.println("save card : "+o);
        CardCommitLog cardCommitLog = new CardCommitLog(commit, null);
        logger.saveCommit(cardCommitLog);
        CardCommitStatus commitStatus = new CardCommitStatus(commit.getCUID(), "done");
        logger.saveCommitStatus(commitStatus);
        commitStatusList.add(commitStatus);
        CardCommitResponse cardCommitResponse = new AddCardCommitResponse(commit, cardId, password);
        CardCommitResponseLog cardCommitResponseLog = new CardCommitResponseLog(cardCommitResponse);
        logger.saveCommitResponse(cardCommitResponseLog);
        commitResponseList.add(cardCommitResponse);
        return true;
    }

    private String makeRandom4digit() {
        Random random = new Random();
        int randNumber = random.nextInt(8999) + 1000;
        return String.valueOf(randNumber);
    }

    private String makeCardNumber() {
        Random random = new Random();
        char[] cardDigit = new char[10];
        cardDigit[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < 10; i++) {
            cardDigit[i] = (char) (random.nextInt(10) + '0');
        }
        String cardId = "502229" + String.valueOf(cardDigit);
        return cardId;
    }
}
