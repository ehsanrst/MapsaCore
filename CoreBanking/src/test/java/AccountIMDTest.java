import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountDAL;
import com.mapsa.core.commits.Commit;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.account.CreateAccountCommit;
import com.mapsa.core.commits.account.CreateAccountCommitResponse;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class AccountIMDTest {
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
    public void saveAccount_DatabaseSave(){
        AccountDAL accountDAL=new AccountDAL();
        AccountIMDImple accountIMDImpl=new AccountIMDImple();
        Account account=new Account("101","201","301");

        accountIMDImpl.addAccount(account);

        Account loadAccount=accountDAL.loadAccount("101");
        assertEquals("101",loadAccount.getAccountId());
        assertEquals("201",loadAccount.getUserId());
        assertEquals("301",loadAccount.getLastCommitId());
    }
    @Test
    public void findAccountByIdFromdatabaseTest(){
        AccountDAL accountDAL=new AccountDAL();
        AccountCommitLogger accountCommitLogger=new AccountCommitLogger();
        AccountIMDImple accountIMDImpl=new AccountIMDImple();
        //setup
        Account account=new Account("101","201","301");
        accountIMDImpl.addAccount(account);
        //action
        Account loadAcount1=accountIMDImpl.findAccountById("101");
        //assert
        assertEquals("101",loadAcount1.getAccountId());
        assertEquals("201",loadAcount1.getUserId());
        assertEquals("301",loadAcount1.getLastCommitId());
    }
    @Test
    public void findCommitById(){
        AccountCommitLogger accountCommitLogger=new AccountCommitLogger();
        AccountIMDImple accountIMDImpl=new AccountIMDImple();
        //setup
        CreateAccountCommit createAccountCommit=new CreateAccountCommit("123");
        createAccountCommit.setCUID("1000");
        AccountCommitLog accountCommitLog=new AccountCommitLog(createAccountCommit,null);
        accountCommitLogger.saveCommit(accountCommitLog);
        //action
        CreateAccountCommit accountCommit=(CreateAccountCommit) accountIMDImpl.findCommitById("1000");
        //assert
        assertEquals("1000",accountCommit.getCUID());
    }
    @Test
    public void AddAccountCommitResponse(){
        AccountCommitLogger accountCommitLogger=new AccountCommitLogger();
        AccountIMDImple accountIMDImpl=new AccountIMDImple();
        //setup
        CreateAccountCommit createAccountCommit=new CreateAccountCommit("123");
        createAccountCommit.setCUID("1002");
        AccountCommitResponse accountCommitResponse=new CreateAccountCommitResponse(createAccountCommit,"10001");
        accountIMDImpl.addAccountCommitResponse(accountCommitResponse);
        //ation
        AccountCommitResponseLog accountCommitResponselog=accountCommitLogger.findCommitResponseByCommitId("1002");
        //assert
        assertEquals("1002",accountCommitResponselog.getCommitId());
    }
}
