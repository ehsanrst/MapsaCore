import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.card.Card;
import com.mapsa.core.card.CardDAL;
import com.mapsa.core.commits.card.AddCardCommit;
import com.mapsa.core.commits.card.AddCardCommitResponse;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CardCommitStatus;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.log.CardCommitLog;
import com.mapsa.core.log.CardCommitResponseLog;
import com.mapsa.core.logger.CardCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardManagerTest {
    private Configuration configuration;
    private ServiceRegistry registry;
    private SessionFactory sessionFactory;
    private Session s;

    @Before
    public void beforeRun() {
        this.configuration = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(CardCommitStatus.class)
                .addAnnotatedClass(Card.class)
                .addAnnotatedClass(CardCommitLog.class)
                .addAnnotatedClass(CardCommitResponseLog.class);
        this.registry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        this.sessionFactory = configuration.buildSessionFactory(registry);
        s = sessionFactory.openSession();
        try{
        Transaction transaction = s.beginTransaction();
        Query query1 = s.createQuery("delete from Card");
        query1.executeUpdate();
        Query query2 = s.createQuery("delete from CardCommitResponseLog");
        query2.executeUpdate();
        Query query3 = s.createQuery("delete from CardCommitStatus");
        query3.executeUpdate();
        Query query4 = s.createQuery("delete from CardCommitLog");
        query4.executeUpdate();
        transaction.commit();}
        catch (Exception e){
            e.printStackTrace();
        }finally {
            s.close();
            sessionFactory.close();
        }
    }

    @Test
    public void createCardCommitConsumer() {
        //setup
        AddCardCommit addCardCommit = new AddCardCommit();
        String accountId = "1000";
        String commitId = "2345";
        addCardCommit.setAccountId(accountId);
        addCardCommit.setCUID(commitId);
        AddCardCommit duplicateAddCardCommit = new AddCardCommit();
        String duplicateAccountId = "6037997109441234";
        duplicateAddCardCommit.setAccountId(duplicateAccountId);
        duplicateAddCardCommit.setCUID(commitId);
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardDAL cardDAL = new CardDAL();
        CardManager cardManager=new CardManager();

        //test create card
        boolean check = cardManager.CreateCardCommitConsumer(addCardCommit);
        assertTrue(check);

        Card card = cardDAL.getCardByAccountId(accountId);
        assertEquals("1000", card.getAccountId());
        assertNotNull(card);
        assertEquals(accountId, card.getAccountId());
        assertTrue(card.isActive());

        //test isSavedCommit
        CardCommitLog cardCommitLog = cardCommitLogger.findCommitById(commitId);
        assertEquals(commitId, cardCommitLog.getCommitId());
        assertNull(cardCommitLog.getPreviousCommitId());

        //test isSavedCommitStatus
        CardCommitStatus cardCommitStatus = cardCommitLogger.loadCommitStatusbyId(commitId);
        assertEquals("done", cardCommitStatus.getCommitStatus());

        //test isSavedCardCommitResponseLog
        CardCommitResponseLog cardCommitResponseLog = cardCommitLogger.findCommitResponseByCommitId(commitId);
        assertEquals(commitId, cardCommitResponseLog.getCommitId());

        AddCardCommitResponse addCardCommitResponse= (AddCardCommitResponse) cardCommitResponseLog.getResponse();
        assertNotNull(addCardCommitResponse.getCardNumber());
        assertNotNull(addCardCommitResponse.getCardPass());

        //test duplicateCUID
        check = cardManager.CreateCardCommitConsumer(duplicateAddCardCommit);
        assertTrue(check);

        card = cardDAL.getCardByAccountId(duplicateAccountId);
        assertNull(card);

        assertEquals(1, cardCommitLogger.loadCommitStatus().size());
    }
    @After
    public void afterRun() {
        beforeRun();
    }
}
