import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserDALTest {


    @Before
    public void beforeRun(){
        Configuration cfg = new Configuration().configure().addAnnotatedClass(Account.class).addAnnotatedClass(User.class)
                .addAnnotatedClass(UserCommitStatus.class).addAnnotatedClass(UserCommitLog.class)
                .addAnnotatedClass(UserCommitResponseLog.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
        SessionFactory SF = cfg.buildSessionFactory(reg);
        Session session = SF.openSession();
        try{
            session.beginTransaction();
            Query query = session.createQuery("delete from UserCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from UserCommitLog");
            query.executeUpdate();
            query = session.createQuery("delete from UserCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from Account");
            query.executeUpdate();
            query = session.createQuery("delete from User");
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            session.close();
            SF.close();
        }
    }

    @After
    public void deleteTablesAfter() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sf = conf.buildSessionFactory(reg);
        Session session = sf.openSession();
        try {
            Query query = session.createQuery("drop table User");
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sf.close();
        }
    }

    @Test
    public void saveUser() {
        UserDAL userDAL = new UserDAL();
        //setup
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;
        boolean check;
        User user2;
        ArrayList<AccountStatus> listAccountStatus2;
        AccountStatus accountStatus3;
        AccountStatus accountStatus4;
        boolean check2;

        //test
        user = new User();
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
        check = userDAL.saveUser(user);
        assertTrue(check);

        user2 = new User();
        user2.setId("987654321");
        user2.setName("Zahra");
        user2.setFamilyName("ghaffari");
        user2.setNationalId("9873216540");
        user2.setActive(true);
        user2.setLastCommit("46546846646");
        listAccountStatus2 = new ArrayList<>();
        accountStatus3 = new AccountStatus();
        accountStatus3.accountId = "5546466354";
        accountStatus3.isActive = true;
        listAccountStatus2.add(accountStatus3);
        accountStatus4 = new AccountStatus();
        accountStatus4.accountId = "4646411335";
        accountStatus4.isActive = false;
        listAccountStatus2.add(accountStatus4);
        user2.setAccounts(listAccountStatus2);
        assertEquals(user2.getAccounts().size(), 2);
        check2 = userDAL.saveUser(user2);
        assertTrue(check2);

        //Read
        Configuration conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sf = conf.buildSessionFactory(reg);
        Session session = sf.openSession();
        Query query = session.createQuery("from User");

        List<User> havij = query.list();
        assertEquals((havij.get(0)).getId(), user.getId());
        assertEquals((havij.get(0)).getName(), user.getName());
        assertEquals((havij.get(0)).getFamilyName(), user.getFamilyName());
        assertEquals((havij.get(0)).getNationalId(), user.getNationalId());
        assertEquals((havij.get(0)).isActive(), user.isActive());
        assertEquals((havij.get(0)).getLastCommit(), user.getLastCommit());
        assertEquals((havij.get(0)).getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertEquals((havij.get(0)).getAccounts().get(0).isActive, user.getAccounts().get(0).isActive);
        assertEquals((havij.get(0)).getAccounts().get(1).accountId, user.getAccounts().get(1).accountId);
        assertEquals((havij.get(0)).getAccounts().get(1).isActive, user.getAccounts().get(1).isActive);

        assertEquals((havij.size()), 2);
        assertEquals((havij.get(1)).getName(), user2.getName());
        assertEquals((havij.get(1)).getFamilyName(), user2.getFamilyName());
        assertEquals((havij.get(1)).getNationalId(), user2.getNationalId());
        assertEquals((havij.get(1)).isActive(), user2.isActive());
        assertEquals((havij.get(1)).getLastCommit(), user2.getLastCommit());
        assertEquals((havij.get(1)).getAccounts().get(0).accountId, user2.getAccounts().get(0).accountId);
        assertEquals((havij.get(1)).getAccounts().get(0).isActive, user2.getAccounts().get(0).isActive);
        assertEquals((havij.get(1)).getAccounts().get(1).accountId, user2.getAccounts().get(1).accountId);
        assertEquals((havij.get(1)).getAccounts().get(1).isActive, user2.getAccounts().get(1).isActive);
    }

    @Test
    public void saveUsers() {
        UserDAL userDAL = new UserDAL();
        //setup
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;
        boolean check;
        User user2;
        ArrayList<AccountStatus> listAccountStatus2;
        AccountStatus accountStatus3;
        AccountStatus accountStatus4;
        List<User> users;

        //test
        users = new ArrayList<>();
        listAccountStatus = new ArrayList<>();
        user = new User();
        user.setId("123456789");
        user.setName("fateme");
        user.setFamilyName("Hojjati");
        user.setNationalId("15731871");
        user.setActive(true);
        user.setLastCommit("123456987");
        accountStatus = new AccountStatus();
        accountStatus.accountId = "987654321";
        accountStatus.isActive = true;
        listAccountStatus.add(accountStatus);
        accountStatus2 = new AccountStatus();
        accountStatus2.accountId = "654987123";
        accountStatus2.isActive = false;
        listAccountStatus.add(accountStatus2);
        user.setAccounts(listAccountStatus);
        users.add(user);

        listAccountStatus2 = new ArrayList<>();
        user2 = new User();
        user2.setId("987654321");
        user2.setName("Zahra");
        user2.setFamilyName("ghaffari");
        user2.setNationalId("9873216540");
        user2.setActive(true);
        user2.setLastCommit("46546846646");
        accountStatus3 = new AccountStatus();
        accountStatus3.accountId = "987654321";
        accountStatus3.isActive = true;
        listAccountStatus2.add(accountStatus3);
        accountStatus4 = new AccountStatus();
        accountStatus4.accountId = "654987123";
        accountStatus4.isActive = false;
        listAccountStatus2.add(accountStatus4);
        user2.setAccounts(listAccountStatus2);
        users.add(user2);

        check = userDAL.saveUsers(users);
        assertTrue(check);

        //Read
        Configuration conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory sf = conf.buildSessionFactory(reg);
        Session session = sf.openSession();
        Query query = session.createQuery("from User");

        List<User> havij = query.list();
        assertEquals((havij.get(0)).getId(), user.getId());
        assertEquals((havij.get(0)).getName(), user.getName());
        assertEquals((havij.get(0)).getFamilyName(), user.getFamilyName());
        assertEquals((havij.get(0)).getNationalId(), user.getNationalId());
        assertEquals((havij.get(0)).isActive(), user.isActive());
        assertEquals((havij.get(0)).getLastCommit(), user.getLastCommit());
        assertEquals((havij.get(0)).getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertEquals((havij.get(0)).getAccounts().get(0).isActive, user.getAccounts().get(0).isActive);
        assertEquals((havij.get(0)).getAccounts().get(1).accountId, user.getAccounts().get(1).accountId);
        assertEquals((havij.get(0)).getAccounts().get(1).isActive, user.getAccounts().get(1).isActive);

        assertEquals((havij.get(1)).getName(), user2.getName());
        assertEquals((havij.get(1)).getFamilyName(), user2.getFamilyName());
        assertEquals((havij.get(1)).getNationalId(), user2.getNationalId());
        assertEquals((havij.get(1)).isActive(), user2.isActive());
        assertEquals((havij.get(1)).getLastCommit(), user2.getLastCommit());
        assertEquals((havij.get(1)).getAccounts().get(0).accountId, user2.getAccounts().get(0).accountId);
        assertEquals((havij.get(1)).getAccounts().get(0).isActive, user2.getAccounts().get(0).isActive);
        assertEquals((havij.get(1)).getAccounts().get(1).accountId, user2.getAccounts().get(1).accountId);
        assertEquals((havij.get(1)).getAccounts().get(1).isActive, user2.getAccounts().get(1).isActive);

    }

    @Test
    public void loadUserById() {
        UserDAL userDAL = new UserDAL();
        //setup
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;

        //test
        user = new User();
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
        userDAL.saveUser(user);

        User user2 = userDAL.loadUserById(user.getId());
        assertEquals(user.getNationalId(), user2.getNationalId());
        assertEquals(user.isActive(), user2.isActive());
        assertEquals(user.getName(), user2.getName());
        assertEquals(user.getFamilyName(), user2.getFamilyName());
        assertEquals(user.getLastCommit(), user.getLastCommit());
        assertEquals(user.getAccounts().get(0).accountId, user2.getAccounts().get(0).accountId);
        assertEquals(user.getAccounts().get(0).isActive, user2.getAccounts().get(0).isActive);
        assertEquals(user.getAccounts().get(1).accountId, user2.getAccounts().get(1).accountId);
        assertEquals(user.getAccounts().get(1).isActive, user2.getAccounts().get(1).isActive);
    }

    @Test
    public void loadUserByNationalId() {
        UserDAL userDAL = new UserDAL();
        //setup
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;

        //test
        user = new User();
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
        userDAL.saveUser(user);

        User user2 = userDAL.loadUserByNationalId(user.getNationalId());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.isActive(), user2.isActive());
        assertEquals(user.getName(), user2.getName());
        assertEquals(user.getFamilyName(), user2.getFamilyName());
        assertEquals(user.getLastCommit(), user.getLastCommit());
        assertEquals(user.getAccounts().get(0).accountId, user2.getAccounts().get(0).accountId);
        assertEquals(user.getAccounts().get(0).isActive, user2.getAccounts().get(0).isActive);
        assertEquals(user.getAccounts().get(1).accountId, user2.getAccounts().get(1).accountId);
        assertEquals(user.getAccounts().get(1).isActive, user2.getAccounts().get(1).isActive);
    }

    @Test
    public void loadUsers() {
        UserDAL userDAL = new UserDAL();
        //setup
        User user;
        ArrayList<AccountStatus> listAccountStatus;
        AccountStatus accountStatus;
        AccountStatus accountStatus2;
        boolean check;
        User user2;
        ArrayList<AccountStatus> listAccountStatus2;
        AccountStatus accountStatus3;
        AccountStatus accountStatus4;
        boolean check2;

        //test
        user = new User();
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
        userDAL.saveUser(user);


        user2 = new User();
        user2.setId("987654321");
        user2.setName("Zahra");
        user2.setFamilyName("ghaffari");
        user2.setNationalId("9873216540");
        user2.setActive(true);
        user2.setLastCommit("46546846646");
        listAccountStatus2 = new ArrayList<>();
        accountStatus3 = new AccountStatus();
        accountStatus3.accountId = "5546466354";
        accountStatus3.isActive = true;
        listAccountStatus2.add(accountStatus3);
        accountStatus4 = new AccountStatus();
        accountStatus4.accountId = "4646411335";
        accountStatus4.isActive = false;
        listAccountStatus2.add(accountStatus4);
        user2.setAccounts(listAccountStatus2);
        userDAL.saveUser(user2);

        //test
        List<User> users = userDAL.loadUsers();
        assertEquals((users.get(0)).getId(), user.getId());
        assertEquals((users.get(0)).getName(), user.getName());
        assertEquals((users.get(0)).getFamilyName(), user.getFamilyName());
        assertEquals((users.get(0)).getNationalId(), user.getNationalId());
        assertEquals((users.get(0)).isActive(), user.isActive());
        assertEquals((users.get(0)).getLastCommit(), user.getLastCommit());
        assertEquals((users.get(0)).getAccounts().get(0).accountId, user.getAccounts().get(0).accountId);
        assertEquals((users.get(0)).getAccounts().get(0).isActive, user.getAccounts().get(0).isActive);
        assertEquals((users.get(0)).getAccounts().get(1).accountId, user.getAccounts().get(1).accountId);
        assertEquals((users.get(0)).getAccounts().get(1).isActive, user.getAccounts().get(1).isActive);

        assertEquals((users.size()), 2);
        assertEquals((users.get(1)).getName(), user2.getName());
        assertEquals((users.get(1)).getFamilyName(), user2.getFamilyName());
        assertEquals((users.get(1)).getNationalId(), user2.getNationalId());
        assertEquals((users.get(1)).isActive(), user2.isActive());
        assertEquals((users.get(1)).getLastCommit(), user2.getLastCommit());
        assertEquals((users.get(1)).getAccounts().get(0).accountId, user2.getAccounts().get(0).accountId);
        assertEquals((users.get(1)).getAccounts().get(0).isActive, user2.getAccounts().get(0).isActive);
        assertEquals((users.get(1)).getAccounts().get(1).accountId, user2.getAccounts().get(1).accountId);
        assertEquals((users.get(1)).getAccounts().get(1).isActive, user2.getAccounts().get(1).isActive);
    }

}