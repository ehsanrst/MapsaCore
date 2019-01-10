import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.account.UnblockAccountCommit;
import com.mapsa.core.commits.account.UnblockAccountCommitResponse;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class UnblockAccountCommitTest {

    @Before
    public void beforeRun(){
        Configuration cfg = new Configuration().configure().addAnnotatedClass(Account.class).addAnnotatedClass(AccountCommitLog.class)
                .addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class);
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
        SessionFactory SF = cfg.buildSessionFactory(reg);
        Session session = SF.openSession();
        try{
            session.beginTransaction();
            Query query = session.createQuery("delete from AccountCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitLog");
            query.executeUpdate();
            query = session.createQuery("delete from AccountCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from Account");
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            session.close();
            SF.close();
        }
    }

    @Test
    public void UnblockAccountCommit_changesApplied(){
        //setup
        AccountIMDImple IMD = new AccountIMDImple();
        Account account=new Account("101","201","301");
        account.block("123");
        IMD.addAccount(account);

        //action
        UnblockAccountCommit accountCommit = new UnblockAccountCommit();
        accountCommit.setCUID("987");
        accountCommit.setAccountId("101");
        accountCommit.apply(IMD);
        //test
        Account returned_account = IMD.findAccountById("101");
        UnblockAccountCommitResponse commitResponse =(UnblockAccountCommitResponse) IMD.findCommitResponseById("987");
        AccountCommitStatus commitStatus =(AccountCommitStatus) IMD.findCommitStatusById("987");
        boolean returned_account_isblocked = returned_account.getBlocked();
        boolean returned_commit_response = commitResponse.isDone();
        Assert.assertTrue(!returned_account_isblocked);
        Assert.assertTrue(returned_commit_response);
        Assert.assertEquals("done",commitStatus.getStatus());

    }

}
