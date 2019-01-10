import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.commits.account.*;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.mapsa.core.commits.account.DeactivateAccountCommit;
import com.mapsa.core.commits.account.DeactivateAccountCommitResponse;
import com.mapsa.core.commits.account.UnblockAccountCommit;
import com.mapsa.core.commits.account.UnblockAccountCommitResponse;
import com.mapsa.core.commits.account.BlockAccountCommit;
import com.mapsa.core.commits.account.BlockAccountCommitResponse;
import com.mapsa.core.commits.account.CreateAccountCommit;
import com.mapsa.core.commits.account.CreateAccountCommitResponse;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;

import static org.junit.Assert.*;


public class AccountManagerTest {
    AccountCommitLogger logger = new AccountCommitLogger();
    AccountManager accountManager;
    AccountDAL accountDAL = new AccountDAL();

    @Before
    public void makeEmptyDataBaseTables() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class).addAnnotatedClass(Account.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Query query = session.createQuery("delete from AccountCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitLog");
            query.executeUpdate();
            query = session.createQuery("delete from Account");
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
    public void accountHistoryCommitConsumer() {
        //setup
        AccountDAL accountDAL = new AccountDAL();
        String accountId = "124578";
        String userId = "1234";
        Account account = new Account(accountId, userId, "7");
        accountDAL.saveAccount(account);
        AccountCommit commit = new CreateAccountCommit(userId);
        commit.setCUID("1");
        AccountCommitLog accountCommitLog = new AccountCommitLog(commit, null);
        logger.saveCommit(accountCommitLog);

        AccountHistoryCommit accountHistoryCommit = new AccountHistoryCommit();
        accountHistoryCommit.setAccountId(accountId);
        accountHistoryCommit.setNumberOfHistoryEvent(5);
        accountHistoryCommit.setCUID("14");

        AccountHistoryCommit accountHistoryCommitOutOfBound = new AccountHistoryCommit();
        accountHistoryCommitOutOfBound.setAccountId(accountId);
        accountHistoryCommitOutOfBound.setNumberOfHistoryEvent(6);
        accountHistoryCommitOutOfBound.setCUID("16");


        commit = new WithdrawCommit();
        ((WithdrawCommit) commit).setAccountId(accountId);
        ((WithdrawCommit) commit).setAmount("10000");
        commit.setCUID("2");
        accountCommitLog = new AccountCommitLog(commit, "1");
        logger.saveCommit(accountCommitLog);

        commit = new DepositCommit(accountId,"100");
        commit.setCUID("3");
        accountCommitLog = new AccountCommitLog(commit, "2");
        logger.saveCommit(accountCommitLog);

        commit = new BlockAccountCommit();
        ((BlockAccountCommit) commit).setAccountId(accountId);
        commit.setCUID("4");
        accountCommitLog = new AccountCommitLog(commit, "3");
        logger.saveCommit(accountCommitLog);

        commit = new UnblockAccountCommit();
        ((UnblockAccountCommit) commit).setAccountId(accountId);
        commit.setCUID("5");
        accountCommitLog = new AccountCommitLog(commit, "4");
        logger.saveCommit(accountCommitLog);

        commit = new DeactivateAccountCommit(accountId);
        commit.setCUID("6");
        accountCommitLog = new AccountCommitLog(commit, "5");
        logger.saveCommit(accountCommitLog);

        commit = new ReactivateAccountCommit();
        ((ReactivateAccountCommit) commit).setAccountId(accountId);
        commit.setCUID("7");
        accountCommitLog = new AccountCommitLog(commit, "6");
        logger.saveCommit(accountCommitLog);

        accountManager = new AccountManager();


        boolean check = accountManager.accountHistoryCommitConsumer(accountHistoryCommit);
        assertEquals(true, check);

        //check status
        AccountCommitStatus accountCommitStatus = logger.findCommitStatus("14");
        assertEquals("done", accountCommitStatus.getStatus());

        //test isSavedCardCommitResponseLog
        AccountCommitResponseLog accountCommitResponseLog = logger.findCommitResponseByCommitId("14");
        assertEquals("14", accountCommitResponseLog.getCommitId());

        AccountHistoryCommitResponse accountHistoryCommitResponse = (AccountHistoryCommitResponse) accountCommitResponseLog.getResponse();
        assertEquals(5, (int) accountHistoryCommitResponse.getDate().size());
        assertEquals(5, (int) accountHistoryCommitResponse.getEvent().size());
        assertEquals("CreateAccount", accountHistoryCommitResponse.getEvent().get(4));
        assertEquals("BlockAccount", accountHistoryCommitResponse.getEvent().get(3));
        assertEquals("UnblockAccount", accountHistoryCommitResponse.getEvent().get(2));
        assertEquals("DeactivateAccount", accountHistoryCommitResponse.getEvent().get(1));
        assertEquals("ReactivateAccount", accountHistoryCommitResponse.getEvent().get(0));

        //duplicate
        accountHistoryCommit.setAccountId("010");
        check = accountManager.accountHistoryCommitConsumer(accountHistoryCommit);
        assertTrue(check);
        accountCommitStatus = logger.findCommitStatus("14");
        assertEquals("done", accountCommitStatus.getStatus());

        //getOutOfBound
        check = accountManager.accountHistoryCommitConsumer(accountHistoryCommitOutOfBound);
        assertTrue(check);
        accountCommitStatus = logger.findCommitStatus("16");
        assertEquals("done", accountCommitStatus.getStatus());
        accountCommitResponseLog = logger.findCommitResponseByCommitId("16");
        assertEquals("16", accountCommitResponseLog.getCommitId());
        accountHistoryCommitResponse = (AccountHistoryCommitResponse) accountCommitResponseLog.getResponse();
        assertEquals(5, (int) accountHistoryCommitResponse.getDate().size());

    }

    @Test
    public void reactivateAccountCommitConsumer() {
        //setup1
        Account account = new Account("1", "2", "2000");
        account.deposit("1000", "2001");
        account.deactive("2002");
        assertEquals("1", account.getAccountId());
        assertEquals("2", account.getUserId());
        assertEquals("1000", account.getBalance());
        assertEquals("2002", account.getLastCommitId());
        assertEquals(false, account.getActive());
        assertEquals(false, account.getBlocked());
        AccountDAL accountDAL = new AccountDAL();
        boolean vall = accountDAL.saveAccount(account);
        assertTrue(vall);

        //test1
        ReactivateAccountCommit reactivateAccountCommit1 = new ReactivateAccountCommit();
        reactivateAccountCommit1.setCUID("2003");
        reactivateAccountCommit1.setAccountId("1");
        AccountManager accountManager = new AccountManager();
        vall = accountManager.reactivateAccountCommitConsumer(reactivateAccountCommit1);
        assertTrue(vall);
        Account lodAccount1 = null;
        //test Account table
        lodAccount1 = accountDAL.loadAccount("1");
        assertEquals("1", lodAccount1.getAccountId());
        assertEquals("2", lodAccount1.getUserId());
        assertEquals("1000", lodAccount1.getBalance());
        assertEquals("2003", lodAccount1.getLastCommitId());
        assertEquals(true, lodAccount1.getActive());
        assertEquals(false, lodAccount1.getBlocked());
        //test AccuntCommitLog table
        AccountCommitLogger accountCommitLogger = new AccountCommitLogger();
        AccountCommitLog loadAccuntCommitLog1 = null;
        loadAccuntCommitLog1 = accountCommitLogger.findCommitById(reactivateAccountCommit1.getCUID());
        assertEquals("2003", loadAccuntCommitLog1.getCommitId());
        assertEquals("2002", loadAccuntCommitLog1.getPreviousCommitId());
        assertEquals("ReactivateAccount", loadAccuntCommitLog1.getCommitType());
        assertEquals("1", ((ReactivateAccountCommit) (loadAccuntCommitLog1.getCommit())).getAccountId());
        //test AccountCommitStatus table
        AccountCommitStatus accountCommitStatus1 = null;
        accountCommitStatus1 = accountCommitLogger.findCommitStatus(reactivateAccountCommit1.getCUID());
        assertEquals("2003", accountCommitStatus1.getCommitId());
        assertEquals("Done", accountCommitStatus1.getStatus());
        //test AccountCommitResponseLog table
        AccountCommitResponseLog accountCommitResponseLog1 = null;
        accountCommitResponseLog1 = accountCommitLogger.findCommitResponseByCommitId("2003");
        assertEquals("2003", accountCommitResponseLog1.getCommitId());
        assertTrue(((ReactivateAccountCommitResponse) (accountCommitResponseLog1.getResponse())).isDone());
        assertEquals("ReactivateAccountCommitResponse", accountCommitResponseLog1.getResponseType());

        //setup2
        Account account1 = new Account("2", "3", "4000");
        account1.deposit("1000", "4001");
        account1.deactive("4002");
        assertEquals("2", account1.getAccountId());
        assertEquals("3", account1.getUserId());
        assertEquals("1000", account1.getBalance());
        assertEquals("4002", account1.getLastCommitId());
        assertEquals(false, account1.getActive());
        assertEquals(false, account1.getBlocked());
        AccountDAL accountDAL1 = new AccountDAL();
        vall = accountDAL1.saveAccount(account1);
        assertTrue(vall);

        //test2
        ReactivateAccountCommit reactivateAccountCommit2 = new ReactivateAccountCommit();
        reactivateAccountCommit2.setCUID("4003");
        reactivateAccountCommit2.setAccountId("2");
        AccountManager accountManager1 = new AccountManager();
        vall = accountManager1.reactivateAccountCommitConsumer(reactivateAccountCommit2);
        assertTrue(vall);
        Account lodAccount2 = null;
        //test Account table
        lodAccount2 = accountDAL1.loadAccount("2");
        assertEquals("2", lodAccount2.getAccountId());
        assertEquals("3", lodAccount2.getUserId());
        assertEquals("1000", lodAccount2.getBalance());
        assertEquals("4003", lodAccount2.getLastCommitId());
        assertEquals(true, lodAccount2.getActive());
        assertEquals(false, lodAccount2.getBlocked());
        //test AccuntCommitLog table
        AccountCommitLogger accountCommitLogger2 = new AccountCommitLogger();
        AccountCommitLog loadAccuntCommitLog2 = null;
        loadAccuntCommitLog2 = accountCommitLogger2.findCommitById(reactivateAccountCommit2.getCUID());
        assertEquals("4003", loadAccuntCommitLog2.getCommitId());
        assertEquals("4002", loadAccuntCommitLog2.getPreviousCommitId());
        assertEquals("ReactivateAccount", loadAccuntCommitLog2.getCommitType());
        assertEquals("2", ((ReactivateAccountCommit) (loadAccuntCommitLog2.getCommit())).getAccountId());
        //test AccountCommitStatus table
        AccountCommitStatus accountCommitStatus2 = null;
        accountCommitStatus2 = accountCommitLogger2.findCommitStatus(reactivateAccountCommit2.getCUID());
        assertEquals("4003", accountCommitStatus2.getCommitId());
        assertEquals("Done", accountCommitStatus2.getStatus());
        //test AccountCommitResponseLog table
        AccountCommitResponseLog accountCommitResponseLog2 = null;
        accountCommitResponseLog2 = accountCommitLogger2.findCommitResponseByCommitId("4003");
        assertEquals("4003", accountCommitResponseLog2.getCommitId());
        assertTrue(((ReactivateAccountCommitResponse) (accountCommitResponseLog2.getResponse())).isDone());
        assertEquals("ReactivateAccountCommitResponse", accountCommitResponseLog2.getResponseType());


        //test3 reactivateAccountCommit is Failed
        ReactivateAccountCommit reactivateAccountCommit3 = new ReactivateAccountCommit();
        reactivateAccountCommit3.setCUID("5003");
        reactivateAccountCommit3.setAccountId("60");
        AccountManager accountManager3 = new AccountManager();
        vall = accountManager3.reactivateAccountCommitConsumer(reactivateAccountCommit3);
        assertFalse(vall);

        //test AccuntCommitLog table
        AccountCommitLogger accountCommitLogger3 = new AccountCommitLogger();
        AccountCommitLog loadAccuntCommitLog3 = null;
        loadAccuntCommitLog3 = accountCommitLogger3.findCommitById(reactivateAccountCommit3.getCUID());
        assertEquals("5003", loadAccuntCommitLog3.getCommitId());
        assertNull(loadAccuntCommitLog3.getPreviousCommitId());
        assertEquals("ReactivateAccount", loadAccuntCommitLog3.getCommitType());
        assertEquals("60", ((ReactivateAccountCommit) (loadAccuntCommitLog3.getCommit())).getAccountId());
        //test AccountCommitStatus table
        AccountCommitStatus accountCommitStatus3 = null;
        accountCommitStatus3 = accountCommitLogger3.findCommitStatus(reactivateAccountCommit3.getCUID());
        assertEquals("5003", accountCommitStatus3.getCommitId());
        assertEquals("Failed", accountCommitStatus3.getStatus());
        //test AccountCommitResponseLog table
        AccountCommitResponseLog accountCommitResponseLog3 = null;
        accountCommitResponseLog3 = accountCommitLogger3.findCommitResponseByCommitId("5003");
        assertEquals("5003", accountCommitResponseLog3.getCommitId());
        assertFalse(((ReactivateAccountCommitResponse) (accountCommitResponseLog3.getResponse())).isDone());
        assertEquals("ReactivateAccountCommitResponse", accountCommitResponseLog3.getResponseType());
    }

    @Test
    public void depositCommitConsumer() {
        //setup
        AccountCommitLogger logger = new AccountCommitLogger();
        AccountDAL accountDAL = new AccountDAL();
        Account account = new Account("1", "20", "90");
        boolean ok = accountDAL.saveAccount(account);
        assertTrue(ok);
        AccountManager accountManager = new AccountManager();
        DepositCommit depositCommit = new DepositCommit("1","2000");
        depositCommit.setCUID("100");

        //test depositCommitConsumer save correctly with different CommitId
        boolean ok1 = accountManager.depositCommitConsumer(depositCommit);
        assertTrue(ok1);
        AccountCommitStatus commitStatus = logger.findCommitStatus("100");
        assertEquals("100", commitStatus.getCommitId());
        assertEquals("done", commitStatus.getStatus());

        //duplicate commitId adn dont save again
        boolean ok5 = accountManager.depositCommitConsumer(depositCommit);
        assertFalse(ok5);
        Account account5 = accountDAL.loadAccount("1");
        assertEquals("2000", account5.getBalance());

        //check account is loaded correctly
        Account account2 = accountDAL.loadAccount("1");
        assertEquals("2000", account2.getBalance());
        assertEquals("20", account2.getUserId());

        //check AccountCommitLog is loaded correctly
        AccountCommitLog AccountCommitLog = logger.findCommitById("100");
        assertEquals("100", AccountCommitLog.getCommitId());
        assertEquals("90", AccountCommitLog.getPreviousCommitId());

        //check AccountCommitResponse is loaded correctly
        AccountCommitResponseLog accountCommitResponseLog = logger.findCommitResponseByCommitId("100");
        assertEquals("100", accountCommitResponseLog.getCommitId());
        AccountCommitResponse accountCommitResponse = accountCommitResponseLog.getResponse();
        assertNotNull(accountCommitResponse);

        //test depositCommitConsumer with  "-33333333333333" amount is saved or not
        Account account1 = accountDAL.loadAccount("1");
        depositCommit = new DepositCommit(account1.getAccountId(),"-33333333333333");
        depositCommit.setCUID("110");
        boolean ok2 = accountManager.depositCommitConsumer(depositCommit);
        assertFalse(ok2);

        //add money to account with Id=1
        Account account3 = accountDAL.loadAccount("1");
        depositCommit = new DepositCommit(account1.getAccountId(),"6000");
        depositCommit.setCUID("111");
        boolean ok3 = accountManager.depositCommitConsumer(depositCommit);
        assertTrue(ok3);
        Account account4 = accountDAL.loadAccount("1");
        assertEquals("8000", account4.getBalance());

    }


    @Test
    public void unblockAccountCommitConsumer() {
        //setup
        AccountDAL accountDAL = new AccountDAL();
        AccountCommitLogger accountCommitLogger = new AccountCommitLogger();
        Account account = new Account("400", "9", "227");
        accountDAL.saveAccount(account);

        account = new Account("500", "6", "447");
        accountDAL.saveAccount(account);
        account = new Account("324", "7", "567");
        accountDAL.saveAccount(account);
        account.block("567");

        AccountManager accountManager = new AccountManager();

        //Test UnblockAccountCommit
        UnblockAccountCommit unblockAccountCommit = new UnblockAccountCommit();
        unblockAccountCommit.setAccountId("324");
        unblockAccountCommit.setCUID("123456");
        boolean test = accountManager.unblockAccountCommitConsumer(unblockAccountCommit);
        assertTrue(test);
        account = accountDAL.loadAccount("324");
        assertNotNull(account);
        assertFalse(account.getBlocked());
        assertEquals("324", account.getAccountId());

        //Test AccountCommitLog
        AccountCommitLog accountCommitLog = accountCommitLogger.findCommitById("123456");
        unblockAccountCommit = (UnblockAccountCommit) accountCommitLog.getCommit();
        assertNotNull(unblockAccountCommit);
        assertEquals("324", unblockAccountCommit.getAccountId());
        assertEquals("123456", accountCommitLog.getCommitId());

        //test isSavedCommitStatus
        AccountCommitStatus accountCommitStatus = accountCommitLogger.findCommitStatus("123456");
        assertEquals("Done", accountCommitStatus.getStatus());
        assertEquals("123456", accountCommitStatus.getCommitId());

        //test isSavedAccountCommitResponseLog
        AccountCommitResponseLog accountCommitResponseLog = accountCommitLogger.findCommitResponseByCommitId("123456");
        assertEquals("123456", accountCommitResponseLog.getCommitId());
        assertEquals("UnblockAccountCommitResponse", accountCommitResponseLog.getResponseType());
        UnblockAccountCommitResponse unblockAccountCommitResponse = (UnblockAccountCommitResponse) accountCommitResponseLog.getResponse();
        assertTrue(unblockAccountCommitResponse.isDone());

        //test duplicateCUID
        unblockAccountCommit.setAccountId("500");
        unblockAccountCommit.setCUID("123456");
        boolean check = accountManager.unblockAccountCommitConsumer(unblockAccountCommit);
        assertTrue(check);
        assertEquals(1, accountCommitLogger.loadAllCommitStatus().size());
        accountCommitLog = accountCommitLogger.findCommitById("123456");
        unblockAccountCommit = (UnblockAccountCommit) accountCommitLog.getCommit();
        assertNotNull(unblockAccountCommit);
        assertEquals("324", unblockAccountCommit.getAccountId());

        //Test
        unblockAccountCommit.setAccountId("400");
        unblockAccountCommit.setCUID("456");
        test = accountManager.unblockAccountCommitConsumer(unblockAccountCommit);
        assertTrue(test);
        assertFalse(account.getBlocked());

        //Test Unblock notExist Account
        unblockAccountCommit.setAccountId("1000");
        unblockAccountCommit.setCUID("100");
        check = accountManager.unblockAccountCommitConsumer(unblockAccountCommit);
        assertFalse(check);
        //status
        accountCommitStatus = accountCommitLogger.findCommitStatus("100");
        assertNotNull(accountCommitStatus);
        assertEquals("Failed", accountCommitStatus.getStatus());
        assertEquals("100", accountCommitStatus.getCommitId());
        //ResponseLog
        accountCommitResponseLog = accountCommitLogger.findCommitResponseByCommitId("100");
        assertEquals("100", accountCommitResponseLog.getCommitId());
        assertEquals("UnblockAccountCommitResponse", accountCommitResponseLog.getResponseType());
        unblockAccountCommitResponse = (UnblockAccountCommitResponse) accountCommitResponseLog.getResponse();
        assertFalse(unblockAccountCommitResponse.isDone());
    }

    @After
    public void emptyDataBaseTablesAgain() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class).addAnnotatedClass(Account.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("delete from AccountCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitLog");
            query.executeUpdate();
            query = session.createQuery("delete from Account");
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
    public void deactivateAccountCommitConsumer() {
        //setup
        AccountManager accountManager;
        DeactivateAccountCommit deactivateAccountCommit;
        boolean check;
        AccountDAL accountDAL;
        Account account;

        DeactivateAccountCommit deactivateAccountCommit2;
        boolean check2;
        AccountDAL accountDAL2;
        Account account2;

        //test
        account = new Account("123456789", "987654321", "456");
        accountDAL = new AccountDAL();
        accountDAL.saveAccount(account);
        accountManager = new AccountManager();
        deactivateAccountCommit = new DeactivateAccountCommit("123456789");
        deactivateAccountCommit.setCUID("123");
        check = accountManager.deactivateAccountCommitConsumer(deactivateAccountCommit);
        assertTrue(check);

        account2 = new Account("87644874784", "56466434866", "789");
        accountDAL2 = new AccountDAL();
        accountDAL2.saveAccount(account2);
        accountManager = new AccountManager();
        deactivateAccountCommit2 = new DeactivateAccountCommit("87644874784");
        deactivateAccountCommit2.setCUID("46846");
        accountManager.deactivateAccountCommitConsumer(deactivateAccountCommit2);
        accountManager.deactivateAccountCommitConsumer(deactivateAccountCommit2);


        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Account.class).addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitStatus.class).addAnnotatedClass(AccountCommitResponseLog.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sf = conf.buildSessionFactory(reg);
        Session session = sf.openSession();


        Query query = session.createQuery("from Account ");
        //query.setParameter("accountId", deactivateAccountCommit.getAccountId());
        List<Account> accountQuery = query.list();
        assertEquals((accountQuery.get(0)).getLastCommitId(), deactivateAccountCommit.getCUID());
        assertEquals((accountQuery.get(0)).getUserId(), account.getUserId());
        assertEquals((accountQuery.get(0)).getUserId(), account.getUserId());
        assertFalse((accountQuery.get(0)).getActive());
        assertFalse((accountQuery.get(0)).getBlocked());
        assertEquals((accountQuery.get(0)).getBalance(), account.getBalance());

        assertEquals((accountQuery.get(1)).getLastCommitId(), deactivateAccountCommit2.getCUID());
        assertEquals((accountQuery.get(1)).getUserId(), account2.getUserId());
        assertEquals((accountQuery.get(1)).getUserId(), account2.getUserId());
        assertFalse((accountQuery.get(1)).getActive());
        assertFalse((accountQuery.get(1)).getBlocked());
        assertEquals((accountQuery.get(1)).getBalance(), account2.getBalance());

        Query query1 = session.createQuery("from AccountCommitLog ");
        //query1.setParameter("commitId", deactivateAccountCommit.getCUID());

        List<AccountCommitLog> accountCommitLogquery = query1.list();
        assertEquals((accountCommitLogquery.get(0)).getCommitType(), "DeactivateAccount");
        assertEquals((accountCommitLogquery.get(0)).getPreviousCommitId(), "456");

        assertEquals((accountCommitLogquery.get(1)).getCommitType(), "DeactivateAccount");
        assertEquals((accountCommitLogquery.get(1)).getPreviousCommitId(), "789");


        Query query2 = session.createQuery("from AccountCommitStatus");
        //query2.setParameter("commitId", deactivateAccountCommit.getCUID());
        List<AccountCommitStatus> accountCommitStatusequery = query2.list();
        assertEquals((accountCommitStatusequery.get(0)).getStatus(), "Done");
        assertEquals((accountCommitStatusequery.get(0)).getCommitId(), "123");

        assertEquals((accountCommitStatusequery.get(1)).getStatus(), "Done");
        assertEquals((accountCommitStatusequery.get(1)).getCommitId(), "46846");


        Query query3 = session.createQuery("from AccountCommitResponseLog");
        //query3.setParameter("commitId", deactivateAccountCommit.getCUID());
        List<AccountCommitResponseLog> accountCommitResponseLogquery = query3.list();

        assertEquals(((DeactivateAccountCommitResponse) ((accountCommitResponseLogquery.get(0)).getResponse())).isDone(), true);
        assertEquals(accountCommitResponseLogquery.get(0).getCommitId(), "123");

        assertEquals(((DeactivateAccountCommitResponse) ((accountCommitResponseLogquery.get(1)).getResponse())).isDone(), true);
        assertEquals(accountCommitResponseLogquery.get(1).getCommitId(), "46846");

    }

    public void createAccountCommitConsumer() {
        //connection
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class).addAnnotatedClass(Account.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();

        //setup
        AccountManager accountManager = new AccountManager();
        CreateAccountCommit createAccountCommit = new CreateAccountCommit("12345");
        boolean check;
        createAccountCommit.setCUID("1");

        //test
//check return
        check = accountManager.createAccountCommitConsumer(createAccountCommit);
        assertTrue(check);

//check AccountCommitResponseLog Table
        Query query = session.createQuery("from AccountCommitResponseLog where commitId= :commitId");
        query.setParameter("commitId", createAccountCommit.getCUID());
        AccountCommitResponseLog accountCommitResponseLog = (AccountCommitResponseLog) query.list().get(0);
        assertNotNull(((CreateAccountCommitResponse) (accountCommitResponseLog.getResponse())).getAccountId());
        assertEquals(accountCommitResponseLog.getCommitId(), createAccountCommit.getCUID());
        assertEquals(accountCommitResponseLog.getResponseType(), "CreateAccountCommit");

//check AccountCommitStatus Table
        query = session.createQuery("from AccountCommitStatus where commitId= :commitId");
        query.setParameter("commitId", createAccountCommit.getCUID());
        AccountCommitStatus accountCommitStatus = (AccountCommitStatus) query.list().get(0);
        assertEquals(accountCommitStatus.getCommitId(), createAccountCommit.getCUID());
        assertEquals(accountCommitStatus.getStatus(), "Done");

//check AccountCommitLog Table
        query = session.createQuery("from AccountCommitLog where commitId= :commitId");
        query.setParameter("commitId", createAccountCommit.getCUID());
        AccountCommitLog accountCommitLog = (AccountCommitLog) query.list().get(0);
        assertEquals(((CreateAccountCommit) (accountCommitLog.getCommit())).getUserId(), createAccountCommit.getUserId());
        assertEquals(accountCommitLog.getCommitId(), createAccountCommit.getCUID());

//check Account Table
        query = session.createQuery("from Account where userId= :userId");
        query.setParameter("userId", createAccountCommit.getUserId());
        Account account = (Account) query.list().get(0);
        assertNotNull(account.getAccountId());
        assertTrue(account.getActive());
        assertFalse(account.getBlocked());
        assertEquals(account.getLastCommitId(), createAccountCommit.getCUID());
        assertNotNull(account.getBalance());

        session.close();
        sessionFactory.close();

    }

    @Test
    public void blockAccountCommitConsumer() throws Exception {
        //setup
        AccountCommitLogger logger = new AccountCommitLogger();
        AccountCommitLog log;
        AccountCommitStatus status;
        AccountCommitResponseLog responseLog;
        AccountDAL accountDAL = new AccountDAL();
        Account account = new Account("1", "11", "111");
        account.deposit("1000", "200");
        accountDAL.saveAccount(account);
        account = new Account("2", "22", "222");
        accountDAL.saveAccount(account);
        AccountManager accountManager = new AccountManager();
        BlockAccountCommit blockAccountCommit = new BlockAccountCommit();
        blockAccountCommit.setAccountId("1");
        blockAccountCommit.setCUID("3333");

        //Check Persisted Account Is Blocked or Not
        boolean ok = accountManager.blockAccountCommitConsumer(blockAccountCommit);
        assertTrue(ok);
        Account persistedAccount = accountDAL.loadAccount("1");
        assertTrue(persistedAccount.getBlocked());
        assertEquals("1000", persistedAccount.getBalance());
        assertEquals("3333", persistedAccount.getLastCommitId());

        //Check Account Commit Log Is Persisted Correctly or Not
        log = logger.findCommitById("3333");
        assertNotNull(log);
        assertEquals("3333", log.getCommitId());

        //Check Commit Status Is Persisted Correctly or Not
        status = logger.findCommitStatus("3333");
        assertEquals("Done", status.getStatus());
        assertEquals("3333", status.getCommitId());

        //Test Account Commit Response Is Persistence Correctly or Not
        responseLog = logger.findCommitResponseByCommitId("3333");
        assertEquals("BlockAccountCommitResponse", responseLog.getResponseType());
        assertEquals(true, ((BlockAccountCommitResponse) (responseLog.getResponse())).isDone());
    }

    @After
    public void runAfterTestMethod() {
        makeEmptyDataBaseTables();
    }

    @Test
    public void withdrawCommitConsumer() throws Exception {

        //setup
        Account account1 = new Account("43", "20", "120");
        account1.deposit("5000","20");
        boolean result = accountDAL.saveAccount(account1);
        assertTrue(result);

        WithdrawCommit withdrawCommit=new WithdrawCommit();
        withdrawCommit.setAccountId("43");
        withdrawCommit.setAmount("1500");
        withdrawCommit.setCUID("121");

        //test withdrawCommitConsumer in class AccountManager
        AccountManager accountManager=new AccountManager();
        result=accountManager.withdrawCommitConsumer(withdrawCommit);
        assertTrue(result);

        String finalAmount="3500"; // 5000-1500
        Account account = accountDAL.loadAccount("43");
        assertEquals("43",account.getAccountId());
        assertEquals(finalAmount,account.getBalance());

        AccountCommitLogger logger=new AccountCommitLogger();
        AccountCommitStatus commitStatus = logger.findCommitStatus("121");
        assertEquals("121",commitStatus.getCommitId());
        assertEquals("done",commitStatus.getStatus());

        AccountCommitLog commitLog = logger.findCommitById("121");
        assertEquals("121",commitLog.getCommitId());
        assertEquals("121",commitLog.getPreviousCommitId());

        AccountCommitResponseLog commitResponse = logger.findCommitResponseByCommitId("121");
        assertEquals("121",commitResponse.getCommitId());
        AccountCommitResponse response = commitResponse.getResponse();
        assertNotNull(response);

        // check for by amount biger than balance
        WithdrawCommit withdrawCommit2=new WithdrawCommit();
        withdrawCommit2.setAccountId("43");
        withdrawCommit2.setAmount("6000");
        withdrawCommit2.setCUID("122");

        result=accountManager.withdrawCommitConsumer(withdrawCommit2);
        assertFalse(result);
        AccountCommitStatus commitStatus2 = logger.findCommitStatus("122");
        assertEquals("122",commitStatus2.getCommitId());
        assertEquals("failed",commitStatus2.getStatus());


        Account account2 = accountDAL.loadAccount("43");
        assertEquals("43",account2.getAccountId());
        assertEquals("3500",account2.getBalance());

        // check add new commit with amount Negative
        WithdrawCommit withdrawCommit3=new WithdrawCommit();
        withdrawCommit3.setAccountId("43");
        withdrawCommit3.setAmount("-2500");
        withdrawCommit3.setCUID("123");
        result=accountManager.withdrawCommitConsumer(withdrawCommit3);
        assertFalse(result);
        assertEquals("3500",account.getBalance());

        //check add new commit with  amount more than balanc
        WithdrawCommit withdrawCommit4=new WithdrawCommit();
        withdrawCommit4.setAccountId("43");
        withdrawCommit4.setAmount("4500");
        withdrawCommit4.setCUID("124");
        result=accountManager.withdrawCommitConsumer(withdrawCommit4);
        assertFalse(result);
        assertEquals("3500",account.getBalance());


        // add a new Commit for check previousCommitId and lastCommitId
        WithdrawCommit withdrawCommit5=new WithdrawCommit();
        withdrawCommit5.setAccountId("43");
        withdrawCommit5.setAmount("200");
        withdrawCommit5.setCUID("125");
        result=accountManager.withdrawCommitConsumer(withdrawCommit5);
        assertTrue(result);
        account = accountDAL.loadAccount("43");
        assertEquals("43",account.getAccountId());
        assertEquals("3300",account.getBalance());
        assertEquals("125",account.getLastCommitId());
    }
}

