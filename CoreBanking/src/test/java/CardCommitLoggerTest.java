
import com.mapsa.core.card.Card;
import com.mapsa.core.commits.status.CardCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
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
import com.mapsa.core.commits.card.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CardCommitLoggerTest {
    CardCommitLogger logger = new CardCommitLogger();

    @Before
    public void makeEmptyDataBaseTables() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Card.class).addAnnotatedClass(CardCommitLog.class).addAnnotatedClass(CardCommitResponseLog.class).addAnnotatedClass(CardCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("delete from CardCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from CardCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from CardCommitLog");
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    @After
    public void emptyDataBaseTablesAgain() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Card.class).addAnnotatedClass(CardCommitLog.class).addAnnotatedClass(CardCommitResponseLog.class).addAnnotatedClass(CardCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("delete from CardCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from CardCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from CardCommitLog");
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    @Test
    public void saveCommitStatus() throws Exception {
        //setup
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardCommitStatus cardCommitStatus1 = new CardCommitStatus("1277", "done");
        CardCommitStatus cardCommitStatus2 = new CardCommitStatus("1277", "done");
        CardCommitStatus output = null;


        //test save one commit status
        boolean result = cardCommitLogger.saveCommitStatus(cardCommitStatus1);
        assertTrue(result);
        output = cardCommitLogger.loadCommitStatusbyId("1277");
        assertEquals("1277", output.getCommitId());
        assertEquals("done", output.getCommitStatus());
        assertEquals(1, cardCommitLogger.loadCommitStatus().size());


        //check duplicated commit status save
        cardCommitLogger.saveCommitStatus(cardCommitStatus2);
        output = cardCommitLogger.loadCommitStatusbyId("1277");
        assertEquals("1277", output.getCommitId());
        assertEquals("done", output.getCommitStatus());
        assertEquals(1, cardCommitLogger.loadCommitStatus().size());
    }

    @Test
    public void loadCommitStatus() throws Exception {
        //setup
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardCommitStatus cardCommitStatus1 = new CardCommitStatus("1277", "done");
        CardCommitStatus cardCommitStatus2 = new CardCommitStatus("1288", "failed");
        cardCommitLogger.saveCommitStatus(cardCommitStatus1);
        cardCommitLogger.saveCommitStatus(cardCommitStatus2);
        List<CardCommitStatus> output = cardCommitLogger.loadCommitStatus();


        //test load all commitstatus
        assertEquals(2, output.size());

        assertEquals(output.get(0).getCommitId(), "1277");
        assertEquals(output.get(0).getCommitStatus(), "done");
        assertEquals(output.get(1).getCommitId(), "1288");
        assertEquals(output.get(1).getCommitStatus(), "failed");
    }

    @Test
    public void saveCommitstatus() throws Exception {

        //setup
        List<CardCommitStatus> lists = new ArrayList<>();
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardCommitStatus cardCommitStatus1 = new CardCommitStatus("1288", "failed");
        CardCommitStatus cardCommitStatus2 = new CardCommitStatus("1299", "done");
        CardCommitStatus output = null;


        //check save all commit status
        lists.add(cardCommitStatus1);
        lists.add(cardCommitStatus2);
        cardCommitLogger.saveCommitStatus(lists);
        assertEquals(2, cardCommitLogger.loadCommitStatus().size());


        output = cardCommitLogger.loadCommitStatusbyId("1288");
        assertEquals("1288", output.getCommitId());
        assertEquals("failed", output.getCommitStatus());

        output = cardCommitLogger.loadCommitStatusbyId("1299");
        assertEquals("1299", output.getCommitId());
        assertEquals("done", output.getCommitStatus());

        //check duplicated commitstatus save
        cardCommitLogger.saveCommitStatus(lists);
        output = cardCommitLogger.loadCommitStatusbyId("1288");
        assertEquals("1288", output.getCommitId());
        assertEquals("failed", output.getCommitStatus());
        output = cardCommitLogger.loadCommitStatusbyId("1299");
        assertEquals("1299", output.getCommitId());
        assertEquals("done", output.getCommitStatus());
        assertEquals(2, cardCommitLogger.loadCommitStatus().size());

    }

    @Test
    public void loadCommitStatusbyIdChecknotExist() throws Exception {
        //setup
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardCommitStatus cardCommitStatus1 = new CardCommitStatus("1277", "done");

        //check for notExist information
        cardCommitLogger.saveCommitStatus(cardCommitStatus1);
        assertEquals(1, cardCommitLogger.loadCommitStatus().size());

        CommitStatus commitStatus = cardCommitLogger.loadCommitStatusbyId("7896");
        assertNull(commitStatus);
    }

    @Test
    public void loadCommitStatusbyId() throws Exception {
        //setup
        CardCommitLogger cardCommitLogger = new CardCommitLogger();
        CardCommitStatus cardCommitStatus1 = new CardCommitStatus("1277", "done");
        cardCommitLogger.saveCommitStatus(cardCommitStatus1);

        //check for  serach CommitStatusbyId
        assertEquals(1, cardCommitLogger.loadCommitStatus().size());
        CardCommitStatus commitStatus2 = cardCommitLogger.loadCommitStatusbyId("1277");
        assertNotNull(commitStatus2);

        assertEquals("1277", commitStatus2.getCommitId());
        assertEquals("done", commitStatus2.getCommitStatus());

    }

    @Test
    public void saveCommit() {

        //Setup
        CardCommit commit = new AddCardCommit();
        ((AddCardCommit) commit).setAccountId("4444");
        commit.setCUID("1111");
        CardCommitLog cardCommitLog = new CardCommitLog(commit, "3333");

        //test save one CardCommit and related data
        boolean ok = logger.saveCommit(cardCommitLog);
        assertTrue(ok);
        CardCommitLog persistedCardCommitLog = logger.findCommitById("1111");
        assertEquals("1111", persistedCardCommitLog.getCommitId());
        assertEquals("AddCard", persistedCardCommitLog.getCommitType());
        assertEquals("3333", persistedCardCommitLog.getPreviousCommitId());
        assertEquals(((AddCardCommit) commit).getAccountId(), ((AddCardCommit) persistedCardCommitLog.getCommit()).getAccountId());

        //check duplicate CardCommit save
        boolean ok1 = logger.saveCommit(cardCommitLog);
        boolean ok2 = logger.saveCommit(cardCommitLog);
        assertTrue(ok1);
        assertTrue(ok2);
        assertNotNull(logger.findCommitById("1111"));
    }

    @Test
    public void findCommitById() {

        //setup
        CardCommit commit = new AddCardCommit();
        commit.setCUID("1111");
        CardCommitLog cardCommitLog = new CardCommitLog(commit, "3333");
        CardCommitLog findedCardCommitLog;

        //check related data of commit
        boolean ok = logger.saveCommit(cardCommitLog);
        assertTrue(ok);
        findedCardCommitLog = logger.findCommitById("1111");
        assertEquals("AddCard", findedCardCommitLog.getCommitType());
        assertEquals("1111", findedCardCommitLog.getCommitId());
        assertEquals("3333", findedCardCommitLog.getPreviousCommitId());

        //check whether wrong id handled or not
        findedCardCommitLog = logger.findCommitById("2222");
        assertNull(findedCardCommitLog);

    }

    @Test
    public void findCommitsRecursively() {
        //setup
        CardCommit commit = new AddCardCommit();
        commit.setCUID("1");
        CardCommitLog cardCommitLog = new CardCommitLog(commit, "0");
        CardCommit commit2 = new AddCardCommit();
        commit.setCUID("2");
        CardCommitLog cardCommitLog2 = new CardCommitLog(commit, "1");
        CardCommit commit3 = new AddCardCommit();
        commit.setCUID("3");
        CardCommitLog cardCommitLog3 = new CardCommitLog(commit, "2");
        CardCommit commit4 = new AddCardCommit();
        commit.setCUID("4");
        CardCommitLog cardCommitLog4 = new CardCommitLog(commit, "3");
        CardCommit commit5 = new AddCardCommit();
        commit.setCUID("5");
        CardCommitLog cardCommitLog5 = new CardCommitLog(commit, "4");
        CardCommit commit6 = new AddCardCommit();
        commit.setCUID("6");
        CardCommitLog cardCommitLog6 = new CardCommitLog(commit, "5");
        CardCommit commit7 = new AddCardCommit();
        commit.setCUID("7");
        CardCommitLog cardCommitLog7 = new CardCommitLog(commit, "6");
        CardCommit commit8 = new AddCardCommit();
        commit.setCUID("8");
        CardCommitLog cardCommitLog8 = new CardCommitLog(commit, "7");
        CardCommit commit9 = new AddCardCommit();
        commit.setCUID("9");
        CardCommitLog cardCommitLog9 = new CardCommitLog(commit, "8");
        CardCommit commit10 = new AddCardCommit();
        commit.setCUID("10");
        CardCommitLog cardCommitLog10 = new CardCommitLog(commit, "9");
        CardCommit commit11 = new AddCardCommit();
        commit.setCUID("11");
        CardCommitLog cardCommitLog11 = new CardCommitLog(commit, "10");
        CardCommit commit12 = new AddCardCommit();
        commit.setCUID("12");
        CardCommitLog cardCommitLog12 = new CardCommitLog(commit, "11");

        //test
        boolean ok = logger.saveCommit(cardCommitLog);
        boolean ok1 = logger.saveCommit(cardCommitLog2);
        boolean ok2 = logger.saveCommit(cardCommitLog3);
        boolean ok3 = logger.saveCommit(cardCommitLog4);
        boolean ok4 = logger.saveCommit(cardCommitLog5);
        boolean ok5 = logger.saveCommit(cardCommitLog6);
        boolean ok6 = logger.saveCommit(cardCommitLog7);
        boolean ok7 = logger.saveCommit(cardCommitLog8);
        boolean ok8 = logger.saveCommit(cardCommitLog9);
        boolean ok9 = logger.saveCommit(cardCommitLog10);
        boolean ok10 = logger.saveCommit(cardCommitLog11);
        boolean ok11 = logger.saveCommit(cardCommitLog12);

        List<CardCommit> output;
        output = logger.findCommitsRecursively("12", 10);
        assertEquals(10, output.size());
        output = logger.findCommitsRecursively("12", 5);
        assertEquals(5, output.size());
        output = logger.findCommitsRecursively("5", 3);
        assertEquals(3, output.size());
        output = logger.findCommitsRecursively("5", 0);
        assertEquals(0, output.size());
        output = logger.findCommitsRecursively("8", 19);
        assertEquals(8, output.size());
        output = logger.findCommitsRecursively("9", 1);
        assertEquals(((AddCardCommit) commit9).getAccountId(), ((AddCardCommit) output.get(0)).getAccountId());
    }

    @Test
    public void saveCommitResponse() {
        boolean check;

//connection
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(CardCommitLog.class).addAnnotatedClass(CardCommitResponseLog.class).addAnnotatedClass(CardCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();

        try {
            Query query = session.createQuery("from CardCommitResponseLog where commitId= :commitId");

//ReactivateCard
            // setup
            ReactivateCardCommit reactivateCardCommit;
            ReactivateCardResponse reactivateCardResponse;
            CardCommitResponseLog reactivateCardResponseLog;
            //test
            reactivateCardCommit = new ReactivateCardCommit();
            reactivateCardCommit.setCardId("123456789");
            reactivateCardCommit.setCUID("1");
            reactivateCardResponse = new ReactivateCardResponse(reactivateCardCommit, true);
            reactivateCardResponseLog = new CardCommitResponseLog(reactivateCardResponse);
            check = logger.saveCommitResponse(reactivateCardResponseLog);
            assertTrue(check);
            query.setParameter("commitId", reactivateCardResponseLog.getCommitId());
            assertEquals(reactivateCardResponse.isDone(), ((ReactivateCardResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).isDone());

//AddCard
            //setup
            AddCardCommit addCardCommit;
            AddCardCommitResponse addCardCommitResponse;
            CardCommitResponseLog addCardCommitResponseLog;
            //test
            addCardCommit = new AddCardCommit();
            addCardCommit.setAccountId("3333");
            addCardCommit.setCUID("2");
            addCardCommitResponse = new AddCardCommitResponse(addCardCommit, "54646", "989898");
            addCardCommitResponseLog = new CardCommitResponseLog(addCardCommitResponse);
            check = logger.saveCommitResponse(addCardCommitResponseLog);
            assertTrue(check);
            query.setParameter("commitId", addCardCommitResponseLog.getCommitId());
            assertEquals(addCardCommitResponse.getCardNumber(), ((AddCardCommitResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).getCardNumber());
            assertEquals(addCardCommitResponse.getCardPass(), ((AddCardCommitResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).getCardPass());

//CheckCardPass
            //setup
            CheckCardPassCommit checkCardPassCommit;
            CheckCardPassResponse checkCardPassResponse;
            CardCommitResponseLog checkCardPassResponseLog;
            //test
            checkCardPassCommit = new CheckCardPassCommit();
            checkCardPassCommit.setCardId("54646212");
            checkCardPassCommit.setCardPass("7845");
            checkCardPassCommit.setCUID("3");
            checkCardPassResponse = new CheckCardPassResponse(checkCardPassCommit, true);
            checkCardPassResponseLog = new CardCommitResponseLog(checkCardPassResponse);
            check = logger.saveCommitResponse(checkCardPassResponseLog);
            assertTrue(check);
            query.setParameter("commitId", reactivateCardResponseLog.getCommitId());
            assertEquals(checkCardPassResponse.isPassIsCorrect(), ((CheckCardPassResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).isPassIsCorrect());

//DeactivateCard
            //setup
            DeactivateCardCommit deactivateCardCommit;
            DeactivateCardResponse deactivateCardResponse;
            CardCommitResponseLog deactivateCardResponseLog;
            //test
            deactivateCardCommit = new DeactivateCardCommit();
            deactivateCardCommit.setCardId("541521251");
            deactivateCardCommit.setCUID("4");
            deactivateCardResponse = new DeactivateCardResponse(deactivateCardCommit, true);
            deactivateCardResponseLog = new CardCommitResponseLog(deactivateCardResponse);
            check = logger.saveCommitResponse(deactivateCardResponseLog);
            assertTrue(check);
            query.setParameter("commitId", reactivateCardResponseLog.getCommitId());
            assertEquals(deactivateCardResponse.isDone(), ((DeactivateCardResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).isDone());

//GetCardAccount
            //setup
            GetCardAccountCommit getCardAccountCommit;
            GetCardAccountResponse getCardAccountResponse;
            CardCommitResponseLog getCardAccountResponseLog;
            //test
            getCardAccountCommit = new GetCardAccountCommit();
            getCardAccountCommit.setCardId("98979797897");
            getCardAccountCommit.setCUID("5");
            getCardAccountResponse = new GetCardAccountResponse(getCardAccountCommit, "4789245");
            getCardAccountResponseLog = new CardCommitResponseLog(getCardAccountResponse);
            check = logger.saveCommitResponse(getCardAccountResponseLog);
            assertTrue(check);
            query.setParameter("commitId", reactivateCardResponseLog.getCommitId());
            assertEquals(getCardAccountResponse.getAccountId(), ((GetCardAccountResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).getAccountId());

//UpdateCardPass
            //setup
            UpdateCardPassCommit updateCardPassCommit;
            UpdateCardPassResponse updateCardPassResponse;
            CardCommitResponseLog updateCardPassResponseLog;
            //test
            updateCardPassCommit = new UpdateCardPassCommit();
            updateCardPassCommit.setCardId("845541");
            updateCardPassCommit.setNewCardPass("545454545454");
            updateCardPassCommit.setCUID("6");
            updateCardPassResponse = new UpdateCardPassResponse(updateCardPassCommit, true);
            updateCardPassResponseLog = new CardCommitResponseLog(updateCardPassResponse);
            check = logger.saveCommitResponse(updateCardPassResponseLog);
            assertTrue(check);
            query.setParameter("commitId", reactivateCardResponseLog.getCommitId());
            assertEquals(updateCardPassResponse.isDone(), ((UpdateCardPassResponse) ((CardCommitResponseLog) query.list().get(0)).getResponse()).isDone());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }

    }

    @Test
    public void findCommitResponseByCommitId() {
//connection
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(CardCommitLog.class).addAnnotatedClass(CardCommitResponseLog.class).addAnnotatedClass(CardCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();


        try {
//ReactivateCard
            //setup
            ReactivateCardCommit reactivateCardCommit;
            ReactivateCardResponse reactivateCardResponse;
            CardCommitResponseLog reactivateCardResponseLog;
            //test
            reactivateCardCommit = new ReactivateCardCommit();
            reactivateCardCommit.setCardId("123456789");
            reactivateCardCommit.setCUID("1");
            reactivateCardResponse = new ReactivateCardResponse(reactivateCardCommit, true);
            reactivateCardResponseLog = new CardCommitResponseLog(reactivateCardResponse);
            session.saveOrUpdate(reactivateCardResponseLog);
            transaction.commit();
            assertEquals(reactivateCardResponseLog.getResponseType(), logger.findCommitResponseByCommitId("1").getResponseType());

//AddCard
            //setup
            AddCardCommit addCardCommit;
            AddCardCommitResponse addCardCommitResponse;
            CardCommitResponseLog addCardCommitResponseLog;
            //test
            addCardCommit = new AddCardCommit();
            addCardCommit.setAccountId("3333");
            addCardCommit.setCUID("2");
            addCardCommitResponse = new AddCardCommitResponse(addCardCommit, "54646", "989898");
            addCardCommitResponseLog = new CardCommitResponseLog(addCardCommitResponse);
            session.saveOrUpdate(addCardCommitResponseLog);
            transaction.commit();
            assertEquals(addCardCommitResponseLog.getResponseType(), logger.findCommitResponseByCommitId("2").getResponseType());

//CheckCardPass
            //setup
            CheckCardPassCommit checkCardPassCommit;
            CheckCardPassResponse checkCardPassResponse;
            CardCommitResponseLog checkCardPassResponseLog;
            //test
            checkCardPassCommit = new CheckCardPassCommit();
            checkCardPassCommit.setCardId("54646212");
            checkCardPassCommit.setCardPass("7845");
            checkCardPassCommit.setCUID("3");
            checkCardPassResponse = new CheckCardPassResponse(checkCardPassCommit, true);
            checkCardPassResponseLog = new CardCommitResponseLog(checkCardPassResponse);
            session.saveOrUpdate(checkCardPassResponseLog);
            transaction.commit();
            assertEquals(checkCardPassResponseLog.getResponseType(), logger.findCommitResponseByCommitId("3").getResponseType());

//DeactivateCard
            //setup
            DeactivateCardCommit deactivateCardCommit;
            DeactivateCardResponse deactivateCardResponse;
            CardCommitResponseLog deactivateCardResponseLog;
            //test
            deactivateCardCommit = new DeactivateCardCommit();
            deactivateCardCommit.setCardId("541521251");
            deactivateCardCommit.setCUID("4");
            deactivateCardResponse = new DeactivateCardResponse(deactivateCardCommit, true);
            deactivateCardResponseLog = new CardCommitResponseLog(deactivateCardResponse);
            session.saveOrUpdate(deactivateCardResponseLog);
            transaction.commit();
            assertEquals(deactivateCardResponseLog.getResponseType(), logger.findCommitResponseByCommitId("4").getResponseType());

//GetCardAccount
            //setup
            GetCardAccountCommit getCardAccountCommit;
            GetCardAccountResponse getCardAccountResponse;
            CardCommitResponseLog getCardAccountResponseLog;
            //test
            getCardAccountCommit = new GetCardAccountCommit();
            getCardAccountCommit.setCardId("98979797897");
            getCardAccountCommit.setCUID("5");
            getCardAccountResponse = new GetCardAccountResponse(getCardAccountCommit, "4789245");
            getCardAccountResponseLog = new CardCommitResponseLog(getCardAccountResponse);
            session.saveOrUpdate(getCardAccountResponseLog);
            transaction.commit();
            assertEquals(getCardAccountResponseLog.getResponseType(), logger.findCommitResponseByCommitId("5").getResponseType());

//UpdateCardPass
            //setup
            UpdateCardPassCommit updateCardPassCommit;
            UpdateCardPassResponse updateCardPassResponse;
            CardCommitResponseLog updateCardPassResponseLog;
            //test
            updateCardPassCommit = new UpdateCardPassCommit();
            updateCardPassCommit.setCardId("845541");
            updateCardPassCommit.setNewCardPass("545454545454");
            updateCardPassCommit.setCUID("6");
            updateCardPassResponse = new UpdateCardPassResponse(updateCardPassCommit, true);
            updateCardPassResponseLog = new CardCommitResponseLog(updateCardPassResponse);
            session.saveOrUpdate(updateCardPassResponseLog);
            transaction.commit();
            assertEquals(updateCardPassResponseLog.getResponseType(), logger.findCommitResponseByCommitId("6").getResponseType());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }

    }

}