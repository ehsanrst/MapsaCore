import com.mapsa.core.AccountIMDImple;
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
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountIMDImpleTest {
    AccountCommitLogger logger = new AccountCommitLogger();
    AccountIMDImple IMD = new AccountIMDImple();

    public AccountIMDImpleTest(){
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
    public void findCommitResponseById_FromDB(){
        //setup
        BlockAccountCommit commit = new BlockAccountCommit();
        commit.setCUID("123");
        AccountCommitResponse response = new BlockAccountCommitResponse(commit,true);
        AccountCommitResponseLog responseLog = new AccountCommitResponseLog(response);
        logger.saveCommitResponse(responseLog);
        //action
        BlockAccountCommitResponse returned_response =(BlockAccountCommitResponse) IMD.findCommitResponseById("123");
        BlockAccountCommitResponse returned_response1 =(BlockAccountCommitResponse) IMD.findCommitResponseById("321");
        //test
        Assert.assertEquals("123",returned_response.getCommitId());
        Assert.assertEquals(true,returned_response.isDone());
        Assert.assertNull(returned_response1);
    }

    @Test
    public void addCommitStatus_ToDB(){
        //setup
        CreateAccountCommit commit = new CreateAccountCommit("123");
        commit.setCUID("123123123");
        AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(),"done");
        //action
        IMD.addCommitStatus(commitStatus);
        //test
        AccountCommitStatus returned_commit_status = logger.findCommitStatus("123123123");
        Assert.assertEquals("done",returned_commit_status.getStatus());
        Assert.assertEquals("123123123",returned_commit_status.getCommitId());
    }

    @Test
    public void findCommitStatusById_FromDB(){
        //setup
        UnblockAccountCommit commit = new UnblockAccountCommit();
        commit.setCUID("0o9");
        AccountCommitStatus commitStatus = new AccountCommitStatus(commit.getCUID(),"done");
        logger.saveCommitStatus(commitStatus);
        //action
        AccountCommitStatus returned_commitStatus =(AccountCommitStatus) IMD.findCommitStatusById("0o9");
        //test
        Assert.assertEquals("0o9",returned_commitStatus.getCommitId());
        Assert.assertEquals("done",returned_commitStatus.getStatus());
    }
}
