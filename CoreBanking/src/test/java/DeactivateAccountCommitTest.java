import com.mapsa.core.AccountIMD;
import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.*;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.logger.AccountCommitLogger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeactivateAccountCommitTest {
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
    public void deactiveAccontCommitApply_Test(){
        //setup
        Account account=new Account("101","201","3001");
        DeactivateAccountCommit deactivateAccountCommit=new DeactivateAccountCommit("101");
        deactivateAccountCommit.setCUID("301");

        AccountIMD imd = new AccountIMDImple();
        imd.addAccount(account);
        imd.addCommitStatus(new AccountCommitStatus("301","done"));
        //act
        deactivateAccountCommit.apply(imd);
        //assert
        AccountCommitLogger accountCommitLogger=new AccountCommitLogger();
        AccountCommitStatus accountCommitStatus=accountCommitLogger.findCommitStatus("301");
        Assert.assertEquals("done",accountCommitStatus.getStatus());
    }
}
