import com.mapsa.core.AccountIMD;
import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.UserIMD;
import com.mapsa.core.UserIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.account.CreateAccountCommit;
import com.mapsa.core.commits.account.CreateAccountCommitResponse;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.logger.AccountCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CreateAccountCommitTest {
    @Before
    public void makeEmptyDataBaseTables() {
        Configuration conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class)
                .addAnnotatedClass(AccountCommitStatus.class).addAnnotatedClass(Account.class);
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
    public void checkAccountCreatedIsValidApply_Test(){
        //setup
        AccountIMD accountIMD=new AccountIMDImple();
        CreateAccountCommit createAccountCommit=new CreateAccountCommit("321");
        createAccountCommit.setCUID("1234");
        //act
        createAccountCommit.apply(accountIMD);
        CreateAccountCommitResponse returnCreateAccountCommitResponse=(CreateAccountCommitResponse) accountIMD.findCommitResponseById("1234");
        String accountId= returnCreateAccountCommitResponse.getAccountId();
        Account returnAccount=accountIMD.findAccountById(accountId);
        //assert
        Assert.assertNotNull(returnAccount);
        Assert.assertTrue(returnAccount.getActive());
        Assert.assertFalse(returnAccount.getBlocked());
        Assert.assertEquals("0",returnAccount.getBalance());
        Assert.assertEquals(createAccountCommit.getUserId(),returnAccount.getUserId());
        Assert.assertEquals("321",returnAccount.getUserId());
    }
    @Test
    public void applyWithdrawCheck_Test(){
        //setup
        AccountIMD accountIMD=new AccountIMDImple();
        CreateAccountCommit createAccountCommit=new CreateAccountCommit("123");
        createAccountCommit.setCUID("321");
        //act
        createAccountCommit.apply(accountIMD);
        CreateAccountCommitResponse createAccountCommitResponse=(CreateAccountCommitResponse)accountIMD.findCommitResponseById(createAccountCommit.getCUID());
        String returnAccountId=createAccountCommitResponse.getAccountId();
        Account returnAccount=accountIMD.findAccountById(returnAccountId);
        returnAccount.deposit("2000","366");
        //assert
        Assert.assertEquals("2000",returnAccount.getBalance());
        Assert.assertFalse(returnAccount.withdraw("30000","852"));
        Assert.assertFalse(returnAccount.withdraw("-1000","853"));
        Assert.assertTrue(returnAccount.withdraw("500","854"));
        returnAccount.withdraw("500","855");
        Assert.assertEquals("1000",returnAccount.getBalance());
        returnAccount.deactive("111");
        Assert.assertFalse(returnAccount.withdraw("1000","112"));
        returnAccount.activate("113");
        returnAccount.block("114");
        Assert.assertFalse(returnAccount.withdraw("1000","115"));
    }
    @Test
    public void applyDepositCheck_Test(){
        //setup
        AccountIMD accountIMD=new AccountIMDImple();
        CreateAccountCommit createAccountCommit=new CreateAccountCommit("123");
        createAccountCommit.setCUID("321");
        //act
        createAccountCommit.apply(accountIMD);
        CreateAccountCommitResponse createAccountCommitResponse= (CreateAccountCommitResponse) accountIMD.findCommitResponseById(createAccountCommit.getCUID());
        String returnAccountId=createAccountCommitResponse.getAccountId();
        Account returnAccount=accountIMD.findAccountById(returnAccountId);
        //assert
        Assert.assertTrue(returnAccount.deposit("1000","111"));
        Assert.assertEquals("1000",returnAccount.getBalance());
        Assert.assertFalse(returnAccount.deposit("-1000","112"));
        returnAccount.block("113");
        Assert.assertFalse(returnAccount.deposit("1000","114"));
        returnAccount.unblock("115");
        returnAccount.deactive("116");
        Assert.assertFalse(returnAccount.deposit("1000","117"));


    }
}
