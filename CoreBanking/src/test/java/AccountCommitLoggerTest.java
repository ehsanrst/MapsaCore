import com.mapsa.core.commits.account.*;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AccountCommitLoggerTest {
    AccountCommitLogger logger = new AccountCommitLogger();

    @Before
    public void runBeforeTestMethod() {
        SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class).buildSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();
        try {
            Query query = session.createQuery("delete from com.mapsa.core.commits.status.AccountCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.AccountCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.AccountCommitLog");
            query.executeUpdate();
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sf.close();
        }

    }

    @After
    public void runAfterTestMethod() {
        runBeforeTestMethod();
    }

    @Test
    public void saveCommitStatus() {
        //setup
        AccountCommitStatus accountCommitStatus = new AccountCommitStatus("124", "done");
        AccountCommitStatus duplicateAccountCommitStatus = new AccountCommitStatus("124", "failed");

        //test save
        boolean result = logger.saveCommitStatus(accountCommitStatus);
        assertTrue(result);
        AccountCommitStatus loadAccountCommitStatus = logger.findCommitStatus("124");
        assertEquals("done", loadAccountCommitStatus.getStatus());

        //test duplicate
        boolean duplicateResult = logger.saveCommitStatus(duplicateAccountCommitStatus);
        assertFalse(duplicateResult);
    }

    @Test
    public void saveListCommitStatus() {
        //setup
        List<AccountCommitStatus> accountCommitStatuses = new ArrayList<>();
        AccountCommitStatus accountCommitStatus1 = new AccountCommitStatus("1", "done");
        AccountCommitStatus duplicateAccountCommitStatus1 = new AccountCommitStatus("1", "failed");
        AccountCommitStatus accountCommitStatus2 = new AccountCommitStatus("2", "done");
        AccountCommitStatus accountCommitStatus3 = new AccountCommitStatus("3", "failed");

        //test save
        accountCommitStatuses.add(accountCommitStatus1);
        accountCommitStatuses.add(accountCommitStatus2);
        accountCommitStatuses.add(accountCommitStatus3);
        boolean result = logger.saveListCommitStatus(accountCommitStatuses);
        assertTrue(result);
        List<AccountCommitStatus> loadAccountCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadAccountCommitStatusList.size());
        assertEquals("done", loadAccountCommitStatusList.get(0).getStatus());
        assertEquals("done", loadAccountCommitStatusList.get(1).getStatus());
        assertEquals("failed", loadAccountCommitStatusList.get(2).getStatus());
        assertEquals("1", loadAccountCommitStatusList.get(0).getCommitId());
        assertEquals("2", loadAccountCommitStatusList.get(1).getCommitId());
        assertEquals("3", loadAccountCommitStatusList.get(2).getCommitId());

        //test duplicate in list
        accountCommitStatuses.add(duplicateAccountCommitStatus1);
        result = logger.saveListCommitStatus(accountCommitStatuses);
        assertTrue(result);
        loadAccountCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadAccountCommitStatusList.size());
        assertNotEquals("failed", loadAccountCommitStatusList.get(0).getStatus());
    }

    @Test
    public void loadAllCommitStatus() {
        //setup
        List<AccountCommitStatus> accountCommitStatuses = new ArrayList<>();
        AccountCommitStatus accountCommitStatus1 = new AccountCommitStatus("1", "done");
        AccountCommitStatus duplicateAccountCommitStatus1 = new AccountCommitStatus("1", "failed");
        AccountCommitStatus accountCommitStatus2 = new AccountCommitStatus("2", "done");
        AccountCommitStatus accountCommitStatus3 = new AccountCommitStatus("3", "failed");
        accountCommitStatuses.add(accountCommitStatus1);
        accountCommitStatuses.add(duplicateAccountCommitStatus1);
        accountCommitStatuses.add(accountCommitStatus2);
        accountCommitStatuses.add(accountCommitStatus3);
        boolean result = logger.saveListCommitStatus(accountCommitStatuses);
        assertTrue(result);

        //test load
        List<AccountCommitStatus> loadAccountCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadAccountCommitStatusList.size());
        assertEquals("failed", loadAccountCommitStatusList.get(0).getStatus());
        assertEquals("done", loadAccountCommitStatusList.get(1).getStatus());
        assertEquals("failed", loadAccountCommitStatusList.get(2).getStatus());
        assertEquals("1", loadAccountCommitStatusList.get(0).getCommitId());
        assertEquals("2", loadAccountCommitStatusList.get(1).getCommitId());
        assertEquals("3", loadAccountCommitStatusList.get(2).getCommitId());
    }

    @Test
    public void findCommitStatus() {
        //setup
        AccountCommitStatus accountCommitStatus = new AccountCommitStatus("124", "done");
        AccountCommitStatus accountCommitStatus2 = new AccountCommitStatus("125", "failed");
        AccountCommitStatus duplicateAccountCommitStatus = new AccountCommitStatus("124", "failed");
        boolean result = logger.saveCommitStatus(accountCommitStatus);
        assertTrue(result);
        result = logger.saveCommitStatus(duplicateAccountCommitStatus);
        assertTrue(result);
        result = logger.saveCommitStatus(accountCommitStatus2);
        assertTrue(result);

        //test find
        AccountCommitStatus loadAccountCommitStatus = logger.findCommitStatus("125");
        assertEquals("failed", loadAccountCommitStatus.getStatus());
        assertEquals("125", loadAccountCommitStatus.getCommitId());
        assertNotNull(loadAccountCommitStatus);

        //test duplicate
        loadAccountCommitStatus = logger.findCommitStatus("124");
        assertEquals("failed", loadAccountCommitStatus.getStatus());

    }

    @Test
    public void saveCommitResponse() {
        //setup
        WithdrawCommit withdrawCommit = new WithdrawCommit();
        withdrawCommit.setAccountId("1111");
        withdrawCommit.setAmount("10000");
        withdrawCommit.setCUID("222222");
        AccountCommitResponse accountCommitResponse = new WithdrawCommitResponse(withdrawCommit, true);

        //test save
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
        boolean result = logger.saveCommitResponse(accountCommitResponseLog);
        assertTrue(result);
        accountCommitResponseLog = logger.findCommitResponseByCommitId("222222");
        assertEquals("222222", accountCommitResponseLog.getCommitId());
        assertEquals("WithdrawCommitResponse", accountCommitResponseLog.getResponseType());
        accountCommitResponse = accountCommitResponseLog.getResponse();
        assertEquals(true, ((WithdrawCommitResponse) accountCommitResponse).getDone());
    }

    @Test
    public void findCommitResponseByCommitId() {
        //setup
        WithdrawCommit withdrawCommit = new WithdrawCommit();
        withdrawCommit.setAccountId("1111");
        withdrawCommit.setAmount("10000");
        withdrawCommit.setCUID("222222");
        AccountCommitResponse accountCommitResponse = new WithdrawCommitResponse(withdrawCommit, true);
        AccountCommitResponseLog accountCommitResponseLog = new AccountCommitResponseLog(accountCommitResponse);
        logger.saveCommitResponse(accountCommitResponseLog);

        //test find
        AccountCommitResponseLog returned_accountCommitResponseLog = logger.findCommitResponseByCommitId("222222");
        assertEquals("222222", returned_accountCommitResponseLog.getCommitId());
        assertEquals("WithdrawCommitResponse", returned_accountCommitResponseLog.getResponseType());
        accountCommitResponse = returned_accountCommitResponseLog.getResponse();
        assertEquals(true, ((WithdrawCommitResponse) accountCommitResponse).getDone());
        returned_accountCommitResponseLog = logger.findCommitResponseByCommitId("111");
        assertNull(returned_accountCommitResponseLog);
    }

    @Test
    public void saveCommit() {

        //Setup
        AccountCommit commit1 = new WithdrawCommit();
        ((WithdrawCommit) commit1).setAccountId("110");
        ((WithdrawCommit) commit1).setAmount("5000000");
        commit1.setCUID("51");
        AccountCommitLog accountCommitLog1 = new AccountCommitLog(commit1, "null");
        AccountCommitLog accountCommitLog;

        //test withdraw

        boolean val1 = logger.saveCommit(accountCommitLog1);
        assertTrue(val1);
        accountCommitLog = logger.findCommitById("51");
        assertEquals("51", accountCommitLog.getCommitId());
        WithdrawCommit commit3 = (WithdrawCommit) accountCommitLog.getCommit();
        assertEquals("110", commit3.getAccountId());
        assertEquals("5000000", commit3.getAmount());
//        assertEquals(null, accountCommitLog.getPreviousCommitId());


        AccountCommit commit2 = new WithdrawCommit();
        ((WithdrawCommit) commit2).setAccountId("4342");
        ((WithdrawCommit) commit2).setAmount("5000000");
        commit1.setCUID("76");
        AccountCommitLog accountCommitLog2 = new AccountCommitLog(commit1, "888");
        val1 = logger.saveCommit(accountCommitLog2);
        assertTrue(val1);


        //Deposit

        AccountCommit commit4 = new DepositCommit("77","5000000");
        commit4.setCUID("44");
        AccountCommitLog accountCommitLog4 = new AccountCommitLog(commit4, "4532");
        boolean val5 = logger.saveCommit(accountCommitLog4);
        assertTrue(val5);
        AccountCommitLog accountCommitLog5 = logger.findCommitById("44");
        assertEquals("44", accountCommitLog5.getCommitId());
        DepositCommit commit5 = (DepositCommit) accountCommitLog5.getCommit();
        assertEquals("77", commit5.getAccountId());


    }

    @Test
    public void findCommitById() {
        AccountCommit commit1 = new WithdrawCommit();
        ((WithdrawCommit) commit1).setAccountId("110");
        ((WithdrawCommit) commit1).setAmount("5000000");
        commit1.setCUID("51");
        AccountCommitLog accountCommitLog1 = new AccountCommitLog(commit1, null);
        logger.saveCommit(accountCommitLog1);

        AccountCommitLog obj1 = logger.findCommitById("51");
        assertEquals(obj1.getCommitType(), "Withdraw");
    }

    @Test
    public void findCommitsRecursively() {

        AccountCommit commit10 = new WithdrawCommit();
        ((WithdrawCommit) commit10).setAccountId("160");
        ((WithdrawCommit) commit10).setAmount("6000000");
        commit10.setCUID("60");
        AccountCommitLog accountCommitLog20 = new AccountCommitLog(commit10, "1");
        logger.saveCommit(accountCommitLog20);

        AccountCommit commit11 = new WithdrawCommit();
        ((WithdrawCommit) commit11).setAccountId("160");
        ((WithdrawCommit) commit11).setAmount("6100000");
        commit11.setCUID("61");
        AccountCommitLog accountCommitLog21 = new AccountCommitLog(commit11, "60");
        logger.saveCommit(accountCommitLog21);

        AccountCommit commit12 = new WithdrawCommit();
        ((WithdrawCommit) commit12).setAccountId("160");
        ((WithdrawCommit) commit12).setAmount("6200000");
        commit12.setCUID("62");
        AccountCommitLog accountCommitLog22 = new AccountCommitLog(commit12, "62");
        logger.saveCommit(accountCommitLog22);

        AccountCommit commit13 = new WithdrawCommit();
        ((WithdrawCommit) commit13).setAccountId("160");
        ((WithdrawCommit) commit13).setAmount("6300000");
        commit13.setCUID("63");
        AccountCommitLog accountCommitLog23 = new AccountCommitLog(commit13, "63");
        logger.saveCommit(accountCommitLog23);

        AccountCommit commit14 = new WithdrawCommit();
        ((WithdrawCommit) commit14).setAccountId("164");
        ((WithdrawCommit) commit14).setAmount("6400000");
        commit14.setCUID("64");
        AccountCommitLog accountCommitLog24 = new AccountCommitLog(commit14, "64");
        logger.saveCommit(accountCommitLog24);

        AccountCommit commit15 = new WithdrawCommit();
        ((WithdrawCommit) commit15).setAccountId("165");
        ((WithdrawCommit) commit15).setAmount("6500000");
        commit15.setCUID("65");
        AccountCommitLog accountCommitLog25 = new AccountCommitLog(commit15, "65");
        logger.saveCommit(accountCommitLog25);

        List<AccountCommit> output = logger.findCommitsRecursively("64", 2);
        assertEquals(2, output.size());
        output = logger.findCommitsRecursively("61", 6);
        assertEquals(2, output.size());
        output = logger.findCommitsRecursively("65", 6);
        assertEquals(6, output.size());

    }
}