import com.mapsa.core.UserIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.*;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.logger.UserCommitLogger;
import com.mapsa.core.user.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserIMDImpleTest {
    UserCommitLogger logger = new UserCommitLogger();
    UserIMDImple IMD = new UserIMDImple();

    public UserIMDImpleTest(){}

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

    @Test
    public void findCommitResponseById_FromDB(){
        //setup
        AddUserAccountCommit commit = new AddUserAccountCommit();
        commit.setCUID("123");
        UserCommitResponse response = new AddUserAccountCommitResponse(commit,true);
        UserCommitResponseLog responseLog = new UserCommitResponseLog(response);
        logger.saveCommitResponse(responseLog);
        //action
        AddUserAccountCommitResponse returned_response =(AddUserAccountCommitResponse) IMD.findCommitResponseById("123");
        AddUserAccountCommitResponse returned_response1 =(AddUserAccountCommitResponse) IMD.findCommitResponseById("1234");
        //test
        Assert.assertEquals("123",returned_response.getCommitId());
        Assert.assertEquals(true,returned_response.isDone());
        Assert.assertNull(returned_response1);
    }

    @Test
    public void addCommitStatus_ToDB(){
        //setup
        DeactivateUserCommit commit = new DeactivateUserCommit();
        commit.setCUID("123123123");
        UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(),"done");
        //action
        IMD.addCommitStatus(commitStatus);
        //test
        UserCommitStatus returned_commit_status = logger.findCommitStatus("123123123");
        Assert.assertEquals("done",returned_commit_status.getStatus());
        Assert.assertEquals("123123123",returned_commit_status.getCommitId());
    }

    @Test
    public void findCommitStatusById_FromDB(){
        //setup
        ReactivateUserCommit commit = new ReactivateUserCommit();
        commit.setCUID("987");
        UserCommitStatus commitStatus = new UserCommitStatus(commit.getCUID(),"done");
        logger.saveCommitStatus(commitStatus);
        //action
        UserCommitStatus returned_commitStatus =(UserCommitStatus) IMD.findCommitStatusById("987");
        //test
        Assert.assertEquals("987",returned_commitStatus.getCommitId());
        Assert.assertEquals("done",returned_commitStatus.getStatus());
    }
}


