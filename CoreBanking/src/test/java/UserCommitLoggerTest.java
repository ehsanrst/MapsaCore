import com.mapsa.core.commits.user.*;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.logger.UserCommitLogger;
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

public class UserCommitLoggerTest {
    UserCommitLogger logger = new UserCommitLogger();

    @Before
    public void runBeforeTestMethod() {
        SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class).addAnnotatedClass(UserCommitStatus.class).buildSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();
        try {
            Query query = session.createQuery("delete from com.mapsa.core.commits.status.UserCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.UserCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.UserCommitLog");
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
        UserCommitStatus userCommitStatus = new UserCommitStatus("124", "done");
        UserCommitStatus duplicateUserCommitStatus = new UserCommitStatus("124", "failed");

        //test save
        boolean result = logger.saveCommitStatus(userCommitStatus);
        assertTrue(result);
        UserCommitStatus loadUserCommitStatus = logger.findCommitStatus("124");
        assertEquals("done", loadUserCommitStatus.getStatus());

        //test duplicate
        boolean duplicateResult = logger.saveCommitStatus(duplicateUserCommitStatus);
        assertFalse(duplicateResult);
    }

    @Test
    public void saveListCommitStatus() {
        //setup
        List<UserCommitStatus> userCommitStatuses = new ArrayList<>();
        UserCommitStatus userCommitStatus1 = new UserCommitStatus("1", "done");
        UserCommitStatus duplicateUserCommitStatus1 = new UserCommitStatus("1", "failed");
        UserCommitStatus userCommitStatus2 = new UserCommitStatus("2", "done");
        UserCommitStatus userCommitStatus3 = new UserCommitStatus("3", "failed");

        //test save
        userCommitStatuses.add(userCommitStatus1);
        userCommitStatuses.add(userCommitStatus2);
        userCommitStatuses.add(userCommitStatus3);
        boolean result = logger.saveListCommitStatus(userCommitStatuses);
        assertTrue(result);
        List<UserCommitStatus> loadUserCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadUserCommitStatusList.size());
        assertEquals("done", loadUserCommitStatusList.get(0).getStatus());
        assertEquals("done", loadUserCommitStatusList.get(1).getStatus());
        assertEquals("failed", loadUserCommitStatusList.get(2).getStatus());
        assertEquals("1", loadUserCommitStatusList.get(0).getCommitId());
        assertEquals("2", loadUserCommitStatusList.get(1).getCommitId());
        assertEquals("3", loadUserCommitStatusList.get(2).getCommitId());

        //test duplicate in list
        userCommitStatuses.add(duplicateUserCommitStatus1);
        result = logger.saveListCommitStatus(userCommitStatuses);
        assertTrue(result);
        loadUserCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadUserCommitStatusList.size());
        assertNotEquals("failed", loadUserCommitStatusList.get(0).getStatus());
    }

    @Test
    public void loadAllCommitStatus() {
        //setup
        List<UserCommitStatus> userCommitStatuses = new ArrayList<>();
        UserCommitStatus userCommitStatus1 = new UserCommitStatus("1", "done");
        UserCommitStatus duplicateUserCommitStatus1 = new UserCommitStatus("1", "failed");
        UserCommitStatus userCommitStatus2 = new UserCommitStatus("2", "done");
        UserCommitStatus userCommitStatus3 = new UserCommitStatus("3", "failed");
        userCommitStatuses.add(userCommitStatus1);
        userCommitStatuses.add(duplicateUserCommitStatus1);
        userCommitStatuses.add(userCommitStatus2);
        userCommitStatuses.add(userCommitStatus3);
        boolean result = logger.saveListCommitStatus(userCommitStatuses);
        assertTrue(result);

        //test load
        List<UserCommitStatus> loadUserCommitStatusList = logger.loadAllCommitStatus();
        assertEquals(3, loadUserCommitStatusList.size());
        assertEquals("failed", loadUserCommitStatusList.get(0).getStatus());
        assertEquals("done", loadUserCommitStatusList.get(1).getStatus());
        assertEquals("failed", loadUserCommitStatusList.get(2).getStatus());
        assertEquals("1", loadUserCommitStatusList.get(0).getCommitId());
        assertEquals("2", loadUserCommitStatusList.get(1).getCommitId());
        assertEquals("3", loadUserCommitStatusList.get(2).getCommitId());
    }

    @Test
    public void findCommitStatus() {
        //setup
        UserCommitStatus userCommitStatus = new UserCommitStatus("124", "done");
        UserCommitStatus userCommitStatus2 = new UserCommitStatus("125", "failed");
        UserCommitStatus duplicateUserCommitStatus = new UserCommitStatus("124", "failed");
        boolean result = logger.saveCommitStatus(userCommitStatus);
        assertTrue(result);
        result = logger.saveCommitStatus(duplicateUserCommitStatus);
        assertTrue(result);
        result = logger.saveCommitStatus(userCommitStatus2);
        assertTrue(result);

        //test find
        UserCommitStatus loadUserCommitStatus = logger.findCommitStatus("125");
        assertEquals("failed", loadUserCommitStatus.getStatus());
        assertEquals("125", loadUserCommitStatus.getCommitId());
        assertNotNull(loadUserCommitStatus);

        //test duplicate
        loadUserCommitStatus = logger.findCommitStatus("124");
        assertEquals("failed", loadUserCommitStatus.getStatus());

    }

//    @Test
//    public void saveCommitResponse() {
//        //setup
//        WithdrawCommit withdrawCommit = new WithdrawCommit();
//        withdrawCommit.setUserId("1111");
//        withdrawCommit.setAmount("10000");
//        withdrawCommit.setCUID("222222");
//        UserCommitResponse userCommitResponse = new WithdrawCommitResponse(withdrawCommit, true);
//
//        //test save
//        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(userCommitResponse);
//        boolean result = logger.saveCommitResponse(userCommitResponseLog);
//        assertTrue(result);
//        userCommitResponseLog = logger.findCommitResponseByCommitId("222222");
//        assertEquals("222222", userCommitResponseLog.getCommitId());
//        assertEquals("WithdrawCommitResponse", userCommitResponseLog.getResponseType());
//        userCommitResponse = userCommitResponseLog.getResponse();
//        assertEquals(true, ((WithdrawCommitResponse) userCommitResponse).getDone());
//    }

//    @Test
//    public void findCommitResponseByCommitId() {
//        //setup
//        AddUserCommit addUserCommit = new AddUserCommit("ali","hatami","10000");
//        addUserCommit.setCUID("222222");
//        UserCommitResponse userCommitResponse = new AddUserCommitResponse();
//        ((AddUserCommitResponse) userCommitResponse).setUserId("2000");
//        UserCommitResponseLog userCommitResponseLog = new UserCommitResponseLog(userCommitResponse);
//        logger.saveCommitResponse(userCommitResponseLog);
//
//        //test find
//        userCommitResponseLog = logger.findCommitResponseByCommitId("222222");
//        assertEquals("222222", userCommitResponseLog.getCommitId());
//        assertEquals("WithdrawCommitResponse", userCommitResponseLog.getResponseType());
//        userCommitResponse = userCommitResponseLog.getResponse();
//        assertEquals("2000", ((AddUserCommitResponse) userCommitResponse).getUserId());
//        userCommitResponseLog = logger.findCommitResponseByCommitId("111");
//        assertNull(userCommitResponseLog);
//    }

//    @Test
//    public void saveCommit() {
//
//        //Setup
//        UserCommit commit1 = new WithdrawCommit();
//        ((WithdrawCommit) commit1).setUserId("110");
//        ((WithdrawCommit) commit1).setAmount("5000000");
//        commit1.setCUID("51");
//        UserCommitLog userCommitLog1 = new UserCommitLog(commit1, "null");
//        UserCommitLog userCommitLog;
//
//        //test withdraw
//
//        boolean val1 = logger.saveCommit(userCommitLog1);
//        assertTrue(val1);
//        userCommitLog = logger.findCommitById("51");
//        assertEquals("51", userCommitLog.getCommitId());
//        WithdrawCommit commit3 = (WithdrawCommit) userCommitLog.getCommit();
//        assertEquals("110", commit3.getUserId());
//        assertEquals("5000000", commit3.getAmount());
////        assertEquals(null, userCommitLog.getPreviousCommitId());
//
//
//        UserCommit commit2 = new WithdrawCommit();
//        ((WithdrawCommit) commit2).setUserId("4342");
//        ((WithdrawCommit) commit2).setAmount("5000000");
//        commit1.setCUID("76");
//        UserCommitLog userCommitLog2 = new UserCommitLog(commit1, "888");
//        val1 = logger.saveCommit(userCommitLog2);
//        assertTrue(val1);
//
//
//        //Deposit
//
//        UserCommit commit4 = new DepositCommit();
//        ((DepositCommit) commit4).setUserId("77");
//        ((DepositCommit) commit4).setAmount("5000000");
//        commit4.setCUID("44");
//        UserCommitLog userCommitLog4 = new UserCommitLog(commit4, "4532");
//        boolean val5 = logger.saveCommit(userCommitLog4);
//        assertTrue(val5);
//        UserCommitLog userCommitLog5 = logger.findCommitById("44");
//        assertEquals("44", userCommitLog5.getCommitId());
//        DepositCommit commit5 = (DepositCommit) userCommitLog5.getCommit();
//        assertEquals("77", commit5.getUserId());
//
//
//    }

    @Test
    public void findCommitById() {
        UserCommit commit1 = new AddUserCommit("ali","hatami","22222");
        commit1.setCUID("51");
        UserCommitLog userCommitLog1 = new UserCommitLog(commit1, null);
        logger.saveCommit(userCommitLog1);

        UserCommitLog obj1 = logger.findCommitById("51");
        assertEquals(obj1.getCommitType(), "AddUser");
    }
//
//    @Test
//    public void findCommitsRecursively() {
//
//        UserCommit commit10 = new WithdrawCommit();
//        ((WithdrawCommit) commit10).setUserId("160");
//        ((WithdrawCommit) commit10).setAmount("6000000");
//        commit10.setCUID("60");
//        UserCommitLog userCommitLog20 = new UserCommitLog(commit10, "1");
//        logger.saveCommit(userCommitLog20);
//
//        UserCommit commit11 = new WithdrawCommit();
//        ((WithdrawCommit) commit11).setUserId("160");
//        ((WithdrawCommit) commit11).setAmount("6100000");
//        commit11.setCUID("61");
//        UserCommitLog userCommitLog21 = new UserCommitLog(commit11, "60");
//        logger.saveCommit(userCommitLog21);
//
//        UserCommit commit12 = new WithdrawCommit();
//        ((WithdrawCommit) commit12).setUserId("160");
//        ((WithdrawCommit) commit12).setAmount("6200000");
//        commit12.setCUID("62");
//        UserCommitLog userCommitLog22 = new UserCommitLog(commit12, "62");
//        logger.saveCommit(userCommitLog22);
//
//        UserCommit commit13 = new WithdrawCommit();
//        ((WithdrawCommit) commit13).setUserId("160");
//        ((WithdrawCommit) commit13).setAmount("6300000");
//        commit13.setCUID("63");
//        UserCommitLog userCommitLog23 = new UserCommitLog(commit13, "63");
//        logger.saveCommit(userCommitLog23);
//
//        UserCommit commit14 = new WithdrawCommit();
//        ((WithdrawCommit) commit14).setUserId("164");
//        ((WithdrawCommit) commit14).setAmount("6400000");
//        commit14.setCUID("64");
//        UserCommitLog userCommitLog24 = new UserCommitLog(commit14, "64");
//        logger.saveCommit(userCommitLog24);
//
//        UserCommit commit15 = new WithdrawCommit();
//        ((WithdrawCommit) commit15).setUserId("165");
//        ((WithdrawCommit) commit15).setAmount("6500000");
//        commit15.setCUID("65");
//        UserCommitLog userCommitLog25 = new UserCommitLog(commit15, "65");
//        logger.saveCommit(userCommitLog25);
//
//        List<UserCommit> output = logger.findCommitsRecursively("64", 2);
//        assertEquals(2, output.size());
//        output = logger.findCommitsRecursively("61", 6);
//        assertEquals(2, output.size());
//        output = logger.findCommitsRecursively("65", 6);
//        assertEquals(6, output.size());
//
//    }
}