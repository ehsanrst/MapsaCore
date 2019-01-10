import com.mapsa.core.AccountIMD;
import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.account.AccountCommitResponse;
import com.mapsa.core.commits.account.BlockAccountCommit;
import com.mapsa.core.commits.account.BlockAccountCommitResponse;
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

public class BlockAccountCommitTest {

    AccountCommitLogger logger = new AccountCommitLogger();
    AccountIMDImple IMD = new AccountIMDImple();

    public BlockAccountCommitTest(){
    }

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
    public void apply_AccountChange(){
        //setup
        AccountIMDImple IMD = new AccountIMDImple();
        Account account=new Account("101","201","301");
        IMD.addAccount(account);
        BlockAccountCommit commit = new BlockAccountCommit();
        commit.setAccountId("101");
        commit.setCUID("10000");
        commit.apply(IMD);
        IMD.addCommitStatus(new AccountCommitStatus(commit.getCUID(),"done"));
        //action
        Account returned_account = IMD.findAccountById("101");
        BlockAccountCommitResponse returned_commitResponse =(BlockAccountCommitResponse) IMD.findCommitResponseById("10000");
        AccountCommitStatus returned_commitStatus = IMD.findCommitStatusById("10000");
        boolean isBlockedAfterCommitApply = returned_account.getBlocked();
        boolean isDone = returned_commitResponse.isDone();
        //test
        Assert.assertTrue(isBlockedAfterCommitApply);
        Assert.assertTrue(isDone);
        Assert.assertEquals("done",returned_commitStatus.getStatus());
    }
}
