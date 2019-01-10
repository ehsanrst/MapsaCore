import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AccountDALTest {

    AccountDAL accountDAL = new AccountDAL();
    SessionFactory sessionFactory;
    Session session;

    @Before
    public void runBeforeTestMethod() {
        SessionFactory sessionFactory = new Configuration().addAnnotatedClass(com.mapsa.core.account.Account.class).configure("hibernate.cfg.xml").buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from com.mapsa.core.account.Account");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void saveAccount() {

        //setup
        Account account1 = new Account("1", "20", "120");
        Account account2 = new Account("1", "20", "120");
        Account account3 = new Account("1", "20", "120");
        Account account4 = new Account("2", "40", "130");
        Account loadAccount1 = null;
        Account loadAccount2=null;
        Account loadAccount3=null;

        //test save and load correctly
        boolean val1 = accountDAL.saveAccount(account1);
        assertTrue(val1);
        loadAccount1 = accountDAL.loadAccount("1");
        assertEquals("1", loadAccount1.getAccountId());
        assertEquals("20", loadAccount1.getUserId());
        assertEquals("0", loadAccount1.getBalance());
        assertEquals(true, loadAccount1.getActive());
        assertEquals(false, loadAccount1.getBlocked());

        //test duplicate id
        boolean val2 = accountDAL.saveAccount(account2);
        assertTrue(val2);


        //test duplicate commitId
        boolean val3 = accountDAL.saveAccount(account1);
        assertTrue(val3);
        boolean val4 = accountDAL.saveAccount(account2);
        assertTrue(val4);
        boolean val5 = accountDAL.saveAccount(account3);
        assertTrue(val5);

        //test load all of fields
        boolean val6 = accountDAL.saveAccount(account4);
        assertTrue(val6);
        loadAccount2 = accountDAL.loadAccount("2");
        assertEquals(true, loadAccount2.getActive());
        assertEquals(false, loadAccount2.getBlocked());
        loadAccount2.block("120");
        loadAccount2.unblock("120");
        loadAccount2.deactive("120");
        loadAccount2.activate("120");
        boolean val7 = accountDAL.saveAccount(loadAccount2);
        assertTrue(val7);

        //test different deposit(up 4)
        boolean val8 = accountDAL.saveAccount(account4);
        assertTrue(val8);
         loadAccount3 = accountDAL.loadAccount("2");
        assertEquals(false, loadAccount3.deposit("-1000", "120"));
        assertEquals(false, loadAccount3.deposit("-10.345600", "120"));
        assertEquals(true, loadAccount3.deposit("23232323232323232323232323232323232", "120"));
        assertEquals(true, loadAccount3.deposit("232323232323.32", "120"));

    }

    @Test
    public void loadAccount() {

        //setup
        Account account1 = new Account("1", "20", "120");
        Account account2 = new Account("2", "30", "130");
        Account loadedAccount1 = null;
        Account loadedAccount2 = null;
        Account loadedAccount3 = null;
        Account loadedAccount4 = null;
        Account loadedAccount5 = null;

        //test save and load correctly
        boolean val1 = accountDAL.saveAccount(account1);
        assertTrue(val1);
        loadedAccount1 = accountDAL.loadAccount("1");
        assertEquals("1", loadedAccount1.getAccountId());
        assertEquals("20", loadedAccount1.getUserId());
        assertEquals("0", loadedAccount1.getBalance());
        assertEquals(true, loadedAccount1.getActive());
        assertEquals(false, loadedAccount1.getBlocked());

        //test wrong accountId
        loadedAccount2 = accountDAL.loadAccount("3");
        assertEquals(null, loadedAccount2);


        //test blocked account with withdraw
        boolean val2 = accountDAL.saveAccount(account2);
        assertTrue(val2);
        loadedAccount3 = accountDAL.loadAccount("2");
        loadedAccount3.deposit("3000", "130");
        boolean val3 = accountDAL.saveAccount(loadedAccount3);
        assertTrue(val3);
        loadedAccount4 = accountDAL.loadAccount("2");
         loadedAccount4.block("130");
        boolean val4 = accountDAL.saveAccount(loadedAccount4);
        assertTrue(val4);
        loadedAccount5 = accountDAL.loadAccount("2");
        assertEquals(false, loadedAccount5.withdraw("1000", "130"));

    }

    @Test
    public void loadAccounts() {
        //setup
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account("4", "14", "123"));
        accountList.add(new Account("5", "15", "124"));
        accountList.add(new Account("6", "16", "125"));

        //Test save Accounts correctly
        boolean ok = accountDAL.saveAccounts(accountList);
        assertTrue(ok);

        //Test loadAccounts correctly
        List<Account> output = accountDAL.loadAccounts();
        assertEquals("4", output.get(0).getAccountId());
        assertEquals("14", output.get(0).getUserId());
        assertEquals("0", output.get(0).getBalance());
        assertEquals(true, output.get(0).getActive());
        assertEquals(false, output.get(0).getBlocked());

        assertEquals("5", output.get(1).getAccountId());
        assertEquals("15", output.get(1).getUserId());
        assertEquals("0", output.get(1).getBalance());
        assertEquals(true, output.get(1).getActive());
        assertEquals(false, output.get(1).getBlocked());

        assertEquals("6", output.get(2).getAccountId());
        assertEquals("16", output.get(2).getUserId());
        assertEquals("0", output.get(2).getBalance());
        assertEquals(true, output.get(2).getActive());
        assertEquals(false, output.get(2).getBlocked());


    }

    @Test
    public void saveAccounts() {
        //setup
        List<Account> accountList = new ArrayList<>();
        List<Account> accountList1 = new ArrayList<>();
        List<Account> loadAccounts = null;

        accountList.add(new Account("4", "14", "123"));
        accountList.add(new Account("5", "15", "124"));
        accountList.add(new Account("6", "16", "125"));
        //update accounts
        accountList1.add(new Account("4", "14", "123"));
        accountList1.add(new Account("5", "15", "124"));
        accountList1.add(new Account("6", "16", "125"));

        //test save all accounts
        boolean val = accountDAL.saveAccounts(accountList);
        assertTrue(val);

        //test update (duplicate) of accounts
        boolean val1 = accountDAL.saveAccounts(accountList1);
        assertTrue(val1);

        loadAccounts = accountDAL.loadAccounts();
        assertEquals("4", loadAccounts.get(0).getAccountId());
        assertEquals("14", loadAccounts.get(0).getUserId());
        assertEquals("0", loadAccounts.get(0).getBalance());
        assertEquals(true, loadAccounts.get(0).getActive());
        assertEquals(false, loadAccounts.get(0).getBlocked());

        assertEquals("5", loadAccounts.get(1).getAccountId());
        assertEquals("15", loadAccounts.get(1).getUserId());
        assertEquals("0", loadAccounts.get(1).getBalance());
        assertEquals(true, loadAccounts.get(1).getActive());
        assertEquals(false, loadAccounts.get(1).getBlocked());

        assertEquals("6", loadAccounts.get(2).getAccountId());
        assertEquals("16", loadAccounts.get(2).getUserId());
        assertEquals("0", loadAccounts.get(2).getBalance());
        assertEquals(true, loadAccounts.get(2).getActive());
        assertEquals(false, loadAccounts.get(2).getBlocked());

        assertEquals(3, loadAccounts.size());

    }


    @After
    public void finishTestMethod() {
        SessionFactory sessionFactory = new Configuration().addAnnotatedClass(com.mapsa.core.account.Account.class).configure("hibernate.cfg.xml").buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from com.mapsa.core.account.Account");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }
}