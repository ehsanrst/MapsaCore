import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.account.AccountStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.*;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;

import static org.junit.Assert.*;

import com.mapsa.core.logger.UserCommitLogger;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;


public class UserManagerTest {
    private Configuration configuration;
    private ServiceRegistry registry;
    private SessionFactory sessionFactory;
    private Session s;
    UserDAL userDAL = new UserDAL();

    @Before
    public void beforeRun() {
        this.configuration = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class).addAnnotatedClass(UserCommitStatus.class)
                .addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class);
        this.registry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        this.sessionFactory = configuration.buildSessionFactory(registry);
        s = sessionFactory.openSession();
        try {
            Transaction transaction = s.beginTransaction();
            Query query = s.createQuery("delete from User");
            query.executeUpdate();
            query = s.createQuery("delete from Account");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitResponseLog");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitStatus");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitLog");
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sessionFactory.close();
        }
    }

    @After
    public void tearDown() {
        beforeRun();
    }

    @Test
    public void getUserAccountCommitConsumer() {

//setup
        UserCommitLogger logger = new UserCommitLogger();
        UserDAL userDAL = new UserDAL();
        User user = new User("20", "ali", "tabatabaii", "090", "46");
        List<AccountStatus> accountStatuses = new ArrayList<AccountStatus>();
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.accountId = "1";
        accountStatus.isActive = true;
        AccountStatus accountStatus1 = new AccountStatus();
        accountStatus1.accountId = "2";
        accountStatus1.isActive = true;
        AccountStatus accountStatus2 = new AccountStatus();
        accountStatus2.accountId = "3";
        accountStatus2.isActive = false;
        accountStatuses.add(accountStatus);
        accountStatuses.add(accountStatus1);
        accountStatuses.add(accountStatus2);

        user.setAccounts((ArrayList<AccountStatus>) accountStatuses);
        userDAL.saveUser(user);
        UserManager userManager = new UserManager();
        GetUserAccountsCommit getUserAccountsCommit = new GetUserAccountsCommit("20");
        getUserAccountsCommit.setCUID("18");

        //test
        boolean ok = userManager.getUserAccountCommitConsumer(getUserAccountsCommit);
        assertTrue(ok);
        UserCommitLog log = logger.findCommitById("18");
        assertEquals("GetUserAccounts", log.getCommitType());
        UserCommitStatus status = logger.findCommitStatus("18");
        assertEquals("Done", status.getStatus());

        UserCommitResponseLog userCommitResponse = logger.findCommitResponseByCommitId("18");
        assertEquals("GetUserAccountsCommitResponse", userCommitResponse.getResponseType());

    }

    @Test
    public void AddUserAccountCommitConsumer() {
        //setup
        UserCommitLogger logger = new UserCommitLogger();
        ArrayList<AccountStatus> accountStatusList = new ArrayList<>();
        AccountDAL accountDAL = new AccountDAL();

        //create new user
        User user = new User("5", "arman", "mk", "1234567890", null);
        UserDAL userDAL = new UserDAL();
        userDAL.saveUser(user);

        //create new account
        Account account1 = new Account("12346", "5", "12");
        accountDAL.saveAccount(account1);


        //receive AddUserAccountCommit from user
        AddUserAccountCommit addUserAccountCommit = new AddUserAccountCommit();
        addUserAccountCommit.setUserId("5");
        addUserAccountCommit.setCUID("11");
        addUserAccountCommit.setAccountId("12346");

        //Start test
        UserManager userManager = new UserManager();
        boolean ok = userManager.AddUserAccountCommitConsumer(addUserAccountCommit);
        assertTrue(ok);

        //test userCommitLog saved correctly
        UserCommitLog userCommitLog = logger.findCommitById("11");
        assertEquals("11", userCommitLog.getCommitId());
        assertNull(userCommitLog.getPreviousCommitId());
        AddUserAccountCommit addUserAccountCommit1 = (AddUserAccountCommit) userCommitLog.getCommit();
        assertEquals("5", addUserAccountCommit1.getUserId());
        assertEquals("12346", addUserAccountCommit1.getAccountId());

        //test UserCommitStatus is saved correctly
        UserCommitStatus userCommitStatus1 = logger.findCommitStatus("11");
        assertEquals("done", userCommitStatus1.getStatus());

        //test UserCommitResponseLog is saved correctly
        UserCommitResponseLog userCommitResponseLog = logger.findCommitResponseByCommitId("11");
        assertEquals("11", userCommitResponseLog.getCommitId());
        AddUserAccountCommitResponse addUserAccountCommitResponse = (AddUserAccountCommitResponse) userCommitResponseLog.getResponse();
        assertNotNull(addUserAccountCommitResponse);
        assertTrue(addUserAccountCommitResponse.isDone());

        //test duplicateCUID
        AddUserAccountCommit addUserAccountCommit2 = new AddUserAccountCommit();
        addUserAccountCommit2.setUserId("5");
        addUserAccountCommit2.setAccountId("12349");
        addUserAccountCommit2.setCUID("11");
        boolean ok1 = userManager.AddUserAccountCommitConsumer(addUserAccountCommit);
        assertTrue(ok1);
        int size = logger.loadAllCommitStatus().size();
        assertEquals(1, size);

    }

    @Test
    public void addUserCommitConsumer() {
//connection
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class).addAnnotatedClass(UserCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();


//setup
        UserManager userManager = new UserManager();
        AddUserCommit addUserCommit = new AddUserCommit("Ehsan", "Roostai", "123456789");
        addUserCommit.setCUID("101");
        boolean check;

//test
        check = userManager.addUserCommitConsumer(addUserCommit);
        assertTrue(check);

        //check UserCommitResponseLog Table
        Query query = session.createQuery("from UserCommitResponseLog where commitId= :commitId");
        query.setParameter("commitId", addUserCommit.getCUID());
        UserCommitResponseLog responseLog = (UserCommitResponseLog) query.list().get(0);
        assertNotNull(((AddUserCommitResponse) responseLog.getResponse()).getUserId());
        assertEquals(responseLog.getCommitId(), addUserCommit.getCUID());
        assertEquals(responseLog.getResponseType(), "AddUserCommitResponse");

        //check UserCommitStatus Table
        query = session.createQuery("from UserCommitStatus where commitId= :commitId");
        query.setParameter("commitId", addUserCommit.getCUID());
        UserCommitStatus status = (UserCommitStatus) query.list().get(0);
        assertEquals(status.getCommitId(), addUserCommit.getCUID());
        assertEquals(status.getStatus(), "Done");

        //check UserCommitLog Table
        query = session.createQuery("from UserCommitLog where commitId= :commitId");
        query.setParameter("commitId", addUserCommit.getCUID());
        UserCommitLog log = (UserCommitLog) query.list().get(0);
        assertEquals(log.getCommitId(), addUserCommit.getCUID());
        assertEquals(((AddUserCommit) log.getCommit()).getNationalId(), addUserCommit.getNationalId());

        //check User Table
        query = session.createQuery("from User where nationalId= :nationalId");
        query.setParameter("nationalId", addUserCommit.getNationalId());
        User obj = (User) query.list().get(0);
        assertNotNull(obj.getId());
        assertTrue(obj.isActive());
        assertEquals(obj.getName(), addUserCommit.getName());
        assertEquals(obj.getFamilyName(), addUserCommit.getFamilyName());

        session.close();
        sessionFactory.close();
    }

    @Test
    public void deactivateUserCommitConsumer() {
        //setup1
        ArrayList<AccountStatus> accounts1 = new ArrayList<AccountStatus>();
        User user1 = new User("1", "omid", "hasani", "10000", "2000");
        AccountStatus accountStatus11 = new AccountStatus();
        accountStatus11.accountId = "11";
        accountStatus11.isActive = true;
        accounts1.add(accountStatus11);
        AccountStatus accountStatus12 = new AccountStatus();
        accountStatus12.accountId = "12";
        accountStatus12.isActive = false;
        accounts1.add(accountStatus12);
        AccountStatus accountStatus13 = new AccountStatus();
        accountStatus13.accountId = "13";
        accountStatus13.isActive = true;
        accounts1.add(accountStatus13);
        user1.setAccounts(accounts1);
        UserDAL userDAL1 = new UserDAL();
        boolean vall = userDAL1.saveUser(user1);
        assertTrue(vall);

        //test1
        DeactivateUserCommit deactivateUserCommit1 = new DeactivateUserCommit();
        deactivateUserCommit1.setCUID("2001");
        deactivateUserCommit1.setUserId("1");
        UserManager userManager1 = new UserManager();
        vall = userManager1.deactivateUserCommitConsumer(deactivateUserCommit1);
        assertTrue(vall);
        User lodUser1 = null;
        //test User table
        lodUser1 = userDAL1.loadUserById("1");
        assertEquals("omid", lodUser1.getName());
        assertEquals("1", lodUser1.getId());
        assertEquals("hasani", lodUser1.getFamilyName());
        assertEquals("10000", lodUser1.getNationalId());
        assertEquals(false, lodUser1.isActive());
        assertEquals("11", lodUser1.getAccounts().get(0).accountId);
        assertEquals(true, lodUser1.getAccounts().get(0).isActive);
        assertEquals("12", lodUser1.getAccounts().get(1).accountId);
        assertEquals(false, lodUser1.getAccounts().get(1).isActive);
        assertEquals("13", lodUser1.getAccounts().get(2).accountId);
        assertEquals(true, lodUser1.getAccounts().get(2).isActive);
        //test UserCommitLog table
        UserCommitLogger userCommitLogger1 = new UserCommitLogger();
        UserCommitLog loadUserCommitLog1 = null;
        loadUserCommitLog1 = userCommitLogger1.findCommitById(deactivateUserCommit1.getCUID());
        assertEquals("2001", loadUserCommitLog1.getCommitId());
        assertEquals("2000", loadUserCommitLog1.getPreviousCommitId());
        assertEquals("DeactivateUser", loadUserCommitLog1.getCommitType());
        assertEquals("1", ((DeactivateUserCommit) (loadUserCommitLog1.getCommit())).getUserId());
        //test UserCommitStatus table
        UserCommitStatus userCommitStatus1 = null;
        userCommitStatus1 = userCommitLogger1.findCommitStatus(deactivateUserCommit1.getCUID());
        assertEquals("2001", userCommitStatus1.getCommitId());
        assertEquals("Done", userCommitStatus1.getStatus());
        //test UserCommitResponseLog table
        UserCommitResponseLog userCommitResponseLog1 = null;
        userCommitResponseLog1 = userCommitLogger1.findCommitResponseByCommitId("2001");
        assertEquals("2001", userCommitResponseLog1.getCommitId());
        assertTrue(((DeactiveUserCommitResponse) (userCommitResponseLog1.getResponse())).isDone());
        assertEquals("DeactivateUserCommitResponse", userCommitResponseLog1.getResponseType());

        //setup2
        ArrayList<AccountStatus> accounts2 = new ArrayList<AccountStatus>();
        User user2 = new User("2", "hoseyn", "naghavi", "10001", "4000");
        AccountStatus accountStatus21 = new AccountStatus();
        accountStatus21.accountId = "21";
        accountStatus21.isActive = true;
        accounts2.add(accountStatus21);
        AccountStatus accountStatus22 = new AccountStatus();
        accountStatus22.accountId = "22";
        accountStatus22.isActive = false;
        accounts2.add(accountStatus22);
        user2.setAccounts(accounts2);
        UserDAL userDAL2 = new UserDAL();
        vall = userDAL2.saveUser(user2);
        assertTrue(vall);

        //test2
        DeactivateUserCommit deactivateUserCommit2 = new DeactivateUserCommit();
        deactivateUserCommit2.setCUID("4001");
        deactivateUserCommit2.setUserId("2");
        UserManager userManager2 = new UserManager();
        vall = userManager2.deactivateUserCommitConsumer(deactivateUserCommit2);
        assertTrue(vall);
        User lodUser2 = null;
        //test User table
        lodUser2 = userDAL2.loadUserById("2");
        assertEquals("hoseyn", lodUser2.getName());
        assertEquals("2", lodUser2.getId());
        assertEquals("naghavi", lodUser2.getFamilyName());
        assertEquals("10001", lodUser2.getNationalId());
        assertEquals(false, lodUser2.isActive());
        assertEquals("21", lodUser2.getAccounts().get(0).accountId);
        assertEquals(true, lodUser2.getAccounts().get(0).isActive);
        assertEquals("22", lodUser2.getAccounts().get(1).accountId);
        assertEquals(false, lodUser2.getAccounts().get(1).isActive);

        //test UserCommitLog table
        UserCommitLogger userCommitLogger2 = new UserCommitLogger();
        UserCommitLog loadUserCommitLog2 = null;
        loadUserCommitLog2 = userCommitLogger2.findCommitById(deactivateUserCommit2.getCUID());
        assertEquals("4001", loadUserCommitLog2.getCommitId());
        assertEquals("4000", loadUserCommitLog2.getPreviousCommitId());
        assertEquals("DeactivateUser", loadUserCommitLog2.getCommitType());
        assertEquals("2", ((DeactivateUserCommit) (loadUserCommitLog2.getCommit())).getUserId());
        //test UserCommitStatus table
        UserCommitStatus userCommitStatus2 = null;
        userCommitStatus2 = userCommitLogger2.findCommitStatus(deactivateUserCommit2.getCUID());
        assertEquals("4001", userCommitStatus2.getCommitId());
        assertEquals("Done", userCommitStatus2.getStatus());
        //test UserCommitResponseLog table
        UserCommitResponseLog userCommitResponseLog2 = null;
        userCommitResponseLog2 = userCommitLogger2.findCommitResponseByCommitId("4001");
        assertEquals("4001", userCommitResponseLog2.getCommitId());
        assertTrue(((DeactiveUserCommitResponse) (userCommitResponseLog2.getResponse())).isDone());
        assertEquals("DeactivateUserCommitResponse", userCommitResponseLog2.getResponseType());


        //test3 reactivateUserCommit is Failed
        DeactivateUserCommit deactivateUserCommit3 = new DeactivateUserCommit();
        deactivateUserCommit3.setCUID("5003");
        deactivateUserCommit3.setUserId("60");
        UserManager UserManager3 = new UserManager();
        vall = UserManager3.deactivateUserCommitConsumer(deactivateUserCommit3);
        assertFalse(vall);

        //test UserCommitLog table
        UserCommitLogger userCommitLogger3 = new UserCommitLogger();
        UserCommitLog loadUserCommitLog3 = null;
        loadUserCommitLog3 = userCommitLogger3.findCommitById(deactivateUserCommit3.getCUID());
        assertEquals("5003", loadUserCommitLog3.getCommitId());
        assertNull(loadUserCommitLog3.getPreviousCommitId());
        assertEquals("DeactivateUser", loadUserCommitLog3.getCommitType());
        assertEquals("60", ((DeactivateUserCommit) (loadUserCommitLog3.getCommit())).getUserId());
        //test UserCommitStatus table
        UserCommitStatus userCommitStatus3 = null;
        userCommitStatus3 = userCommitLogger3.findCommitStatus(deactivateUserCommit3.getCUID());
        assertEquals("5003", userCommitStatus3.getCommitId());
        assertEquals("Failed", userCommitStatus3.getStatus());
        //test UserCommitResponseLog table
        UserCommitResponseLog userCommitResponseLog3 = null;
        userCommitResponseLog3 = userCommitLogger3.findCommitResponseByCommitId("5003");
        assertEquals("5003", userCommitResponseLog3.getCommitId());
        assertFalse(((DeactiveUserCommitResponse) (userCommitResponseLog3.getResponse())).isDone());
        assertEquals("DeactivateUserCommitResponse", userCommitResponseLog3.getResponseType());
        // test duplicate commit
        DeactivateUserCommit deactivateUserCommit4 = new DeactivateUserCommit();
        deactivateUserCommit4.setCUID("2001");
        deactivateUserCommit4.setUserId("1");
        UserManager userManager4 = new UserManager();
        vall = userManager4.deactivateUserCommitConsumer(deactivateUserCommit4);
        assertTrue(vall);
        UserDAL userDAL4 = new UserDAL();
        assertEquals(2, userDAL4.loadUsers().size());
        UserCommitLogger userCommitLogger4 = new UserCommitLogger();
        SessionFactory sessionFactory = new Configuration().addAnnotatedClass(com.mapsa.core.user.User.class).addAnnotatedClass(com.mapsa.core.log.UserCommitLog.class).addAnnotatedClass(com.mapsa.core.log.UserCommitResponseLog.class).addAnnotatedClass(com.mapsa.core.commits.status.UserCommitStatus.class).configure("hibernate.cfg.xml").buildSessionFactory();
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(" from UserCommitLog");
        assertEquals(3, query.list().size());
        Query query1 = session.createQuery(" from UserCommitResponseLog");
        assertEquals(3, query1.list().size());
        Query query2 = session.createQuery(" from UserCommitStatus");
        assertEquals(3, query2.list().size());
        session.close();
        sessionFactory.close();

    }

    @Test
    public void reactivateUserCommitConsumer() {
        //setup
        UserCommitLogger logger = new UserCommitLogger();
        UserDAL userDAL = new UserDAL();

        User user = new User("11", "Reza", "Moshiri", "0078575273", "22");
        AccountStatus status = new AccountStatus();
        status.accountId = "12345";
        status.isActive = true;
        ArrayList<AccountStatus> list = new ArrayList<>();
        list.add(status);
        user.setAccounts(list);
        boolean ok = userDAL.saveUser(user);
        assertTrue(ok);

        UserManager manager = new UserManager();
        ReactivateUserCommit reactivateUserCommit = new ReactivateUserCommit();
        reactivateUserCommit.setUserId("11");
        reactivateUserCommit.setCUID("33");

        //Test save user
        ok = user.deactivate("44");
        assertTrue(ok);
        ok = userDAL.saveUser(user);
        assertTrue(ok);
        ok = manager.reactivateUserCommitConsumer(reactivateUserCommit);
        assertTrue(ok);
        user = userDAL.loadUserById("11");
        assertNotNull(user);
        assertEquals(true, user.isActive());
        assertEquals("Reza", user.getName());
        ok = manager.reactivateUserCommitConsumer(reactivateUserCommit);
        assertTrue(ok);


        //Test User Commit Log and Duplicate
        UserCommitLog log = logger.findCommitById("33");
        assertEquals("33", log.getCommitId());
        assertEquals(reactivateUserCommit.getUserId(), ((ReactivateUserCommit) (log.getCommit())).getUserId());
        List<UserCommit> logList = logger.findCommitsRecursively("33", 2);
        assertEquals(1, logList.size());

        //Test User Commit Status and Duplicate
        UserCommitStatus commitStatus = logger.findCommitStatus("33");
        assertEquals("33", commitStatus.getCommitId());
        assertEquals("Done", commitStatus.getStatus());
        List<UserCommitStatus> statusList = logger.loadAllCommitStatus();
        assertEquals(1, statusList.size());

        //Test User Commit Response
        UserCommitResponseLog responseLog = logger.findCommitResponseByCommitId("33");
        assertEquals("33", responseLog.getCommitId());
        assertEquals("ReactivateUserCommitResponse", responseLog.getResponseType());
    }

    @Test
    public void deactivateUserAccountCommitConsumer() {
        //setup
        AccountDAL accountDAL = new AccountDAL();
        UserDAL userDAL = new UserDAL();
        UserCommitLogger logger = new UserCommitLogger();
        String commitId = "1000";
        String userId = "1";

        User user = new User(userId, "rohallah", "hatami", "3310043940", null);
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.accountId = "40";
        accountStatus.isActive = true;
        ArrayList<AccountStatus> accountStatusList = new ArrayList<>();
        accountStatusList.add(accountStatus);
        AccountStatus accountStatus2 = new AccountStatus();
        accountStatus2.accountId = "41";
        accountStatus2.isActive = true;
        accountStatusList.add(accountStatus2);
        user.setAccounts(accountStatusList);
        userDAL.saveUser(user);

        DeactivateUserAccountCommit commit = new DeactivateUserAccountCommit();
        commit.setAccountId("40");
        commit.setUserId(userId);
        commit.setCUID(commitId);

        //test
        UserManager userManager = new UserManager();
        boolean check = userManager.deactivateUserAccountCommitConsumer(commit);
        assertTrue(check);
        user = userDAL.loadUserById(userId);
        accountStatusList = user.getAccounts();
        for (AccountStatus accountStatus1 : accountStatusList) {
            if (accountStatus1.accountId == "40") {
                AccountStatus accountLoaded = accountStatus1;
                assertNotNull(accountLoaded);
                assertFalse(accountLoaded.isActive);
            }
        }

        //test isSavedCommit
        UserCommitLog userCommitLog = logger.findCommitById(commitId);
        assertEquals(commitId, userCommitLog.getCommitId());
        assertNull(userCommitLog.getPreviousCommitId());
        DeactivateUserAccountCommit deactivateUserAccountCommit = (DeactivateUserAccountCommit) userCommitLog.getCommit();
        assertEquals(userId, deactivateUserAccountCommit.getUserId());
        assertEquals("40", deactivateUserAccountCommit.getAccountId());

        //test isSavedCommitStatus
        UserCommitStatus userCommitStatus = logger.findCommitStatus(commitId);
        assertEquals("done", userCommitStatus.getStatus());

        //test isSavedUserCommitResponseLog
        UserCommitResponseLog userCommitResponseLog = logger.findCommitResponseByCommitId(commitId);
        assertEquals(commitId, userCommitResponseLog.getCommitId());
        DeactiveUserAccountCommitResponse deactivateUserAccountCommitResponse = (DeactiveUserAccountCommitResponse) userCommitResponseLog.getResponse();
        assertNotNull(deactivateUserAccountCommitResponse);
        assertTrue(deactivateUserAccountCommitResponse.isDone());

        //test duplicateCUID
        DeactivateUserAccountCommit deactivateUserAccountCommit1 = new DeactivateUserAccountCommit();
        deactivateUserAccountCommit1.setUserId(userId);
        deactivateUserAccountCommit1.setAccountId("40");
        deactivateUserAccountCommit1.setCUID(commitId);
        check = userManager.deactivateUserAccountCommitConsumer(deactivateUserAccountCommit1);
        assertTrue(check);
        User userById = userDAL.loadUserById(userId);
        assertNotNull(userById);
        assertEquals(1, logger.loadAllCommitStatus().size());
    }

    @Test
    public void getUserByNationalIdCommitConsumer() {
        //setup
        UserDAL userDAL;
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;
        boolean check;
        GetUserByNationalIdCommit getUserByNationalIdCommit;

        UserManager userManager;


        //test
        user = new User();
        userManager = new UserManager();
        user.setId("123456789");
        user.setName("fateme");
        user.setFamilyName("Hojjati");
        user.setNationalId("15731871");
        user.setActive(true);
        user.setLastCommit("123456987");
        listAccountStatus = new ArrayList<>();
        accountStatus = new AccountStatus();
        accountStatus.accountId = "987654321";
        accountStatus.isActive = true;
        listAccountStatus.add(accountStatus);
        accountStatus2 = new AccountStatus();
        accountStatus2.accountId = "654987123";
        accountStatus2.isActive = false;
        listAccountStatus.add(accountStatus2);
        user.setAccounts(listAccountStatus);
        userDAL = new UserDAL();
        userDAL.saveUser(user);
        getUserByNationalIdCommit = new GetUserByNationalIdCommit();
        getUserByNationalIdCommit.setNationalId("15731871");
        getUserByNationalIdCommit.setCUID("1234");
        userManager = new UserManager();

        check = userManager.getUserByNationalIdCommitConsumer(getUserByNationalIdCommit);
        assertTrue(check);


        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitStatus.class).addAnnotatedClass(UserCommitResponseLog.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sf = conf.buildSessionFactory(reg);
        Session session = sf.openSession();

        Query query = session.createQuery("from User ");
        //query.setParameter("accountId", deactivateAccountCommit.getAccountId());
        List<User> userQuery = query.list();
        assertEquals(userQuery.get(0).getLastCommit(), getUserByNationalIdCommit.getCUID());
        assertEquals(userQuery.get(0).getId(), user.getId());
        assertEquals(userQuery.get(0).getName(), user.getName());
        assertEquals(userQuery.get(0).getFamilyName(), user.getFamilyName());
        assertEquals(userQuery.get(0).getNationalId(), user.getNationalId());
        assertEquals(userQuery.get(0).getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertEquals(userQuery.get(0).getAccounts().get(0).isActive, user.getAccounts().get(0).isActive);
        assertEquals(userQuery.get(0).getAccounts().get(1).accountId, user.getAccounts().get(1).accountId);
        assertEquals(userQuery.get(0).getAccounts().get(1).isActive, user.getAccounts().get(1).isActive);
        assertTrue(userQuery.get(0).isActive());

        Query query1 = session.createQuery("from UserCommitLog ");
        List<UserCommitLog> userCommitLogquery = query1.list();
        assertEquals((userCommitLogquery.get(0)).getCommitType(), "GetUserByNationalId");
        assertEquals((userCommitLogquery.get(0)).getPreviousCommitId(), "123456987");

        Query query2 = session.createQuery("from UserCommitStatus");
        List<UserCommitStatus> userCommitStatusequery = query2.list();
        assertEquals((userCommitStatusequery.get(0)).getStatus(), "Done");
        assertEquals((userCommitStatusequery.get(0)).getCommitId(), "1234");

        Query query3 = session.createQuery("from UserCommitResponseLog");
        List<UserCommitResponseLog> userCommitResponseLogquery = query3.list();
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getNationalId(), user.getNationalId());
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getFamilyName(), user.getFamilyName());
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getName(), user.getName());
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getAccounts().get(0).isActive, user.getAccounts().get(0).isActive);
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getAccounts().get(1).isActive, user.getAccounts().get(1).isActive);
        assertEquals(((GetUserByNationalIdCommitResponse) ((userCommitResponseLogquery.get(0)).getResponse())).getUser().getAccounts().get(1).accountId, user.getAccounts().get(1).accountId);
        assertEquals(userCommitResponseLogquery.get(0).getCommitId(), "1234");


    }

    @Test
    public void getUserByIdConsumer() {

        //setup
        UserCommitLogger userCommitLogger = new UserCommitLogger();
        UserDAL userDAL = new UserDAL();

        //add user
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.accountId = "555";
        accountStatus.isActive = true;
        ArrayList<AccountStatus> accountStatusArrayList1 = new ArrayList<>();
        accountStatusArrayList1.add(accountStatus);
        User user = new User("100", "ali", "darabi", "335", "33");
        user.setAccounts(accountStatusArrayList1);
        userDAL.saveUser(user);

        accountStatus.accountId = "123";
        accountStatus.isActive = true;
        ArrayList<AccountStatus> accountStatusArrayList = new ArrayList<>();
        accountStatusArrayList.add(accountStatus);
        accountStatus.accountId = "689";
        accountStatus.isActive = true;
        accountStatusArrayList.add(accountStatus);
        User user1 = new User("8", "ahmad", "hasani", "332", "78");
        user1.setAccounts(accountStatusArrayList);
        userDAL.saveUser(user1);

        UserManager userManager = new UserManager();
        GetUserByIdCommit getUserByIdCommit = new GetUserByIdCommit();
        getUserByIdCommit.setCUID("12");
        getUserByIdCommit.setAccountId("123");
        getUserByIdCommit.setUserId("8");
        boolean check = userManager.getUserByIdConsumer(getUserByIdCommit);
        assertTrue(check);

        //test isSavedCommit
        UserCommitLog userCommitLog = userCommitLogger.findCommitById("12");
        assertEquals("12", userCommitLog.getCommitId());
        getUserByIdCommit = (GetUserByIdCommit) userCommitLog.getCommit();
        assertEquals("123", getUserByIdCommit.getAccountId());
        assertEquals("8", getUserByIdCommit.getUserId());
        User user2 = userDAL.loadUserById("8");
        assertEquals("12", user2.getLastCommit());

        //test isSavedCommitStatus
        UserCommitStatus userCommitStatus = userCommitLogger.findCommitStatus("12");
        assertEquals("done", userCommitStatus.getStatus());
        assertEquals("12", userCommitStatus.getCommitId());

        //test isSavedCommitResponseLog
        UserCommitResponseLog userCommitResponseLog = userCommitLogger.findCommitResponseByCommitId("12");
        assertEquals("12", userCommitResponseLog.getCommitId());
        assertEquals("GetUserByIdCommitResponse", userCommitResponseLog.getResponseType());
        GetUserByIdCommitResponse getUserByIdCommitResponse = (GetUserByIdCommitResponse) userCommitResponseLog.getResponse();
        assertEquals(user2.getId(), getUserByIdCommitResponse.getUser().getId());
        assertEquals(user2.getLastCommit(), getUserByIdCommitResponse.getUser().getLastCommit());
        assertEquals(user2.getFamilyName(), getUserByIdCommitResponse.getUser().getFamilyName());
        assertEquals(user2.getName(), getUserByIdCommitResponse.getUser().getName());
        assertEquals(user2.getNationalId(), getUserByIdCommitResponse.getUser().getNationalId());
        assertEquals(user2.getAccounts().get(0).accountId, getUserByIdCommitResponse.getUser().getAccounts().get(0).accountId);

        //test duplicateCUID
        getUserByIdCommit.setCUID("12");
        getUserByIdCommit.setUserId("8");
        check = userManager.getUserByIdConsumer(getUserByIdCommit);
        assertTrue(check);
        assertEquals(1, userCommitLogger.loadAllCommitStatus().size());
    }

    @Test
    public void reactivateUserAccountCommitConsumer() {
        //connection
        Configuration conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class).addAnnotatedClass(UserCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = conf.buildSessionFactory(reg);
        Session session = sessionFactory.openSession();

        //setup
        User user = new User("12", "hasan", "moghadam", "125487", "45");
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setAccountId("4326");
        accountStatus.setActive(false);
        ArrayList<AccountStatus> listsAccount = new ArrayList<>();
        listsAccount.add(accountStatus);
        user.setAccounts(listsAccount);
        userDAL.saveUser(user);

        ReactivateUserAccountCommit reactivateUserAccountCommit = new ReactivateUserAccountCommit();
        reactivateUserAccountCommit.setUserId("12");
        reactivateUserAccountCommit.setAccountId("4326");
        reactivateUserAccountCommit.setCUID("122");

        //test reactivateUserAccountCommitConsumer for user "12"
        UserManager userManager = new UserManager();
        boolean result = userManager.reactivateUserAccountCommitConsumer(reactivateUserAccountCommit);
        assertTrue(result);

        Query query = session.createQuery("from User where id= :id");
        query.setParameter("id", reactivateUserAccountCommit.getUserId());
        User tempUser = (User) query.list().get(0);
        assertEquals(tempUser.getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertTrue(tempUser.getAccounts().get(0).isActive);

        session.close();
        sessionFactory.close();
    }
}