import com.mapsa.core.CardIMDImple;
import com.mapsa.core.card.Card;
import com.mapsa.core.card.CardDAL;
import com.mapsa.core.commits.CommitResponse;
import com.mapsa.core.commits.card.AddCardCommit;
import com.mapsa.core.commits.card.AddCardCommitResponse;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.card.DeactivateCardCommit;
import com.mapsa.core.commits.status.CardCommitStatus;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CardIMDImpelTest {
    private Configuration configuration;
    private ServiceRegistry serviceRegistry;
    private SessionFactory sessionFactory;
    private Session session;
    @Before
    public void beforeRun(){
        this.configuration = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(CardCommitStatus.class)
                .addAnnotatedClass(Card.class)
                .addAnnotatedClass(CardCommitLog.class)
                .addAnnotatedClass(CardCommitLoggerTest.class);
        this.serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        session=sessionFactory.openSession();
        try {
            Transaction transaction = session.beginTransaction();
            Query query1 = session.createQuery("delete from CardCommitStatus");
            query1.executeUpdate();
            Query query2 = session.createQuery("delete from Card");
            query2.executeUpdate();
            Query query3 = session.createQuery("delete from CardCommitLog");
            query3.executeUpdate();
            Query query4 = session.createQuery("delete from CardCommitLoggerTest");
            query4.executeUpdate();
            transaction.commit();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            session.close();
            sessionFactory.close();
        }
    }

    @Test
    public void findCardCoimmitResponseById_loadFromDatabase(){
        //setup
        AddCardCommit commit= new AddCardCommit();
        commit.setCUID("bikhod");
        commit.setNumber(new BigInteger("31212661"));
        commit.setAccountId("1272127182178");
        CardCommitResponse cardCommitResponse = new AddCardCommitResponse(commit,"12345667","1234");
        CardCommitLogger logger = new CardCommitLogger();
        //action
        logger.saveCommitResponse(new CardCommitResponseLog( cardCommitResponse));
        //test
        CardIMDImple CardIMDImple= new CardIMDImple();
        CommitResponse response1 = CardIMDImple.findCardCommitResponseById("123");
        CommitResponse response=  CardIMDImple.findCardCommitResponseById("bikhod");
        Assert.assertNull(response1);
        Assert.assertEquals("bikhod",response.getCommitId());
    }

    @Test
    public void addCommitStatus_toDataBase(){
        CardCommitLogger logger= new CardCommitLogger();
        CardIMDImple imd= new CardIMDImple();
//        AddCardCommit commit = new AddCardCommit();
        CardCommitStatus commitStatus = new CardCommitStatus("chert","waiting");
        //action
        imd.addCommitStatus(commitStatus);
        //test
        List<CardCommitStatus> comitStatusList= logger.loadCommitStatus();
        Assert.assertEquals(1,comitStatusList.size());
        Assert.assertEquals("chert",comitStatusList.get(0).getCommitId());
        Assert.assertEquals("waiting",comitStatusList.get(0).getCommitStatus());

    }

    @Test
    public void findCommitStatusById_fromDataBase(){
        CardCommitLogger logger = new CardCommitLogger();
        CardIMDImple CardIMDImple = new CardIMDImple();
        DeactivateCardCommit deactivateCardCommit = new DeactivateCardCommit();
        deactivateCardCommit.setCUID("pert");
        CardCommitStatus cardCommitStatus = new CardCommitStatus(deactivateCardCommit.getCUID(),"tomammm");
        logger.saveCommitStatus(cardCommitStatus);
        CardCommitStatus return_cardCommitStatus = (CardCommitStatus)CardIMDImple.findCommitStatusById("pert");
        Assert.assertEquals("pert",return_cardCommitStatus.getCommitId());
        Assert.assertEquals("tomammm",return_cardCommitStatus.getCommitStatus());

    }

    @Test
    public void addCard_testDatabaseSave() {
        //setup
        Card testCard = new Card("11", "201", "11111", "2201", "20001");
        CardDAL cardDAL = new CardDAL();
        CardIMDImple CardIMDImple = new CardIMDImple();
        CardIMDImple.addCard(testCard);
        //action
        Card recordedCard = cardDAL.getCardById("11");
        //test
        assertEquals("11", recordedCard.getCardId());
        assertEquals("2201", recordedCard.getAccountId());
        assertEquals(recordedCard.getCvv2(), "201");
        assertEquals("20001", recordedCard.getLastCommitId());
    }
    @Test
    public void findCardById_FindFromDataBaseTest() {
        //setup
        Card testCard = new Card("12", "202", "11112", "2202", "20002");
        CardDAL cardDAL = new CardDAL();
        CardIMDImple CardIMDImple = new CardIMDImple();
        cardDAL.saveCard(testCard);
        //action
        Card recordedCard = CardIMDImple.findCardById("12");
        //test
        assertEquals("12", recordedCard.getCardId());
        assertEquals("2202", recordedCard.getAccountId());
        assertEquals("202", recordedCard.getCvv2());
        assertEquals("20002", recordedCard.getLastCommitId());
    }
    @Test
    public void findCommitById_FromDataBase() {
        //setup
        CardCommitLogger logger = new CardCommitLogger();
        CardIMDImple CardIMDImple = new CardIMDImple();
        AddCardCommit commit = new AddCardCommit();
        commit.setCUID("12");
        CardCommitLog commitLog = new CardCommitLog(commit, null);
        logger.saveCommit(commitLog);
        //action
        AddCardCommit recordedCard = (AddCardCommit) CardIMDImple.findCommitById("12");
        AddCardCommit recordedCard1 = (AddCardCommit) CardIMDImple.findCommitById("13");
        //test
        assertEquals("12", recordedCard.getCUID());
        assertNull(recordedCard1);
    }
    @Test
    public void AddCardCommitResponse_ToDatabase() {
        //setup
        CardIMDImple CardIMDImple = new CardIMDImple();
        CardCommitLogger logger = new CardCommitLogger();
        AddCardCommit addCardCommit = new AddCardCommit();
        addCardCommit.setCUID("1000");
        addCardCommit.setAccountId("1001");
        AddCardCommitResponse commitResponse = new AddCardCommitResponse(addCardCommit, "", "");
        CardIMDImple.AddCardCommitResponse(commitResponse);
        //action
        CardCommitResponseLog holo = logger.findCommitResponseByCommitId("1000");
        //test
        Assert.assertEquals("1000", holo.getCommitId());
    }
}
