import com.mapsa.core.AccountIMD;
import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.*;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.user.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.security.provider.certpath.OCSPResponse;

import javax.imageio.spi.ServiceRegistry;

public class DepositCommitTest {
    @Before
    public void runBeforeTestMethod(){
        SessionFactory sessionFactory=
                new Configuration().addAnnotatedClass(Account.class).configure("hibernate.cfg.xml").buildSessionFactory();
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        Query query=session.createQuery("delete from com.mapsa.core.account.Account");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void depositToAccountApply_Test(){
        //setup
        Account account=new Account("111","112","113");
        AccountIMD accountIMD=new AccountIMDImple();
        accountIMD.addAccount(account);
        DepositCommit depositCommit=new DepositCommit("111","1000");
        depositCommit.setCUID("114");
        //act
        depositCommit.apply(accountIMD);
        Account returnAccount=accountIMD.findAccountById(account.getAccountId());
        //assert
        Assert.assertEquals("1000",returnAccount.getBalance());
        AccountCommitStatus accountCommitStatus= (AccountCommitStatus) accountIMD.findCommitStatusById(depositCommit.getCUID());
        Assert.assertEquals("done",accountCommitStatus.getStatus());
    }
    @Test
    public void duplicateCommitForDeposit_Test(){
        //setup
        Account account=new Account("111","112","113");
        AccountIMD accountIMD=new AccountIMDImple();
        accountIMD.addAccount(account);
        DepositCommit depositCommit=new DepositCommit("111","1000");
        depositCommit.setCUID("114");
        //action
        depositCommit.apply(accountIMD);
        depositCommit.apply(accountIMD);
        Account returnedAccount=accountIMD.findAccountById("111");
        //assert
        Assert.assertEquals("1000",returnedAccount.getBalance());
        AccountCommitStatus accountCommitStatus= (AccountCommitStatus) accountIMD.findCommitStatusById(depositCommit.getCUID());
        Assert.assertEquals("failed",accountCommitStatus.getStatus());
        //test negative deposit from account
        //test account dose't exist
        //test duplicate commit
        //assert
//        Assert.assertEquals("1000",account.getBalance());
    }
    @Test
    public void blockAccountCommitForDeposit_Test(){
        //setup
        Account account=new Account("111","112","113");
        account.block("113");
        AccountIMD accountIMD=new AccountIMDImple();
        accountIMD.addAccount(account);
        DepositCommit depositCommit=new DepositCommit("111","1000");
        depositCommit.setCUID("115");
        //action
        depositCommit.apply(accountIMD);
        //assert
        AccountCommitStatus accountCommitStatus= (AccountCommitStatus) accountIMD.findCommitStatusById("115");
        Assert.assertEquals("failed",accountCommitStatus.getStatus());
    }
    @Test
    public void accountDoesNotExistForDeposit_Test(){
        AccountIMD accountIMD=new AccountIMDImple();
        DepositCommit depositCommit=new DepositCommit("111","1000");
        depositCommit.setCUID("115");
        AccountCommitStatus accountCommitStatus=(AccountCommitStatus)accountIMD.findCommitStatusById("115");
        //action
        depositCommit.apply(accountIMD);
        //assert
        Assert.assertEquals("failed",accountCommitStatus.getStatus());
    }
    @Test
    public void negativeDeposit_Test(){
        Account account=new Account("111","112","113");
        AccountIMD accountIMD=new AccountIMDImple();
        accountIMD.addAccount(account);
        DepositCommit depositCommit=new DepositCommit("111","-1000");
        depositCommit.setCUID("114");
        //action
        depositCommit.apply(accountIMD);
        AccountCommitStatus accountCommitStatus= (AccountCommitStatus) accountIMD.findCommitStatusById("114");
        //assert
        Assert.assertEquals("failed",accountCommitStatus.getStatus());
    }
}
