import com.mapsa.core.UserIMD;
import com.mapsa.core.UserIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.DeactivateUserCommit;
import com.mapsa.core.commits.user.DeactiveUserCommitResponse;
import com.mapsa.core.commits.user.UserCommitResponse;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.user.User;
import com.mapsa.core.user.UserDAL;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeactivateUserCommitTest {
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
    public void deactivateUserApplyTest(){
        //setup
        User user=new User("111","milad","aminizade","2003220011","222");
        user.setActive(true);
        UserIMD userIMD=new UserIMDImple();
        userIMD.addUser(user);
        DeactivateUserCommit deactivateUserCommit=new DeactivateUserCommit();
        deactivateUserCommit.setUserId("111");
        deactivateUserCommit.setCUID("333");
        //action
        deactivateUserCommit.apply(userIMD);
        //assert
        User returnedUser=userIMD.findUserById(user.getId());
        Assert.assertFalse(returnedUser.isActive());
        UserCommitStatus userCommitStatus=userIMD.findCommitStatusById("333");
        Assert.assertEquals("Done",userCommitStatus.getStatus());
        DeactiveUserCommitResponse returnDeactivateUserCommitResponse=(DeactiveUserCommitResponse) userIMD.findCommitResponseById(deactivateUserCommit.getCUID());
        Assert.assertTrue(returnDeactivateUserCommitResponse.isDone());
    }
    @Test
    public void deactivateUserIfUserIsNotExist(){
        //setup
        User user=new User("111","milad","aminizade","2003220011","222");
        UserIMD userIMD=new UserIMDImple();
        userIMD.addUser(user);
        DeactivateUserCommit deactivateUserCommit=new DeactivateUserCommit();
        deactivateUserCommit.setCUID("112");
        deactivateUserCommit.setUserId("333");
        //action
        deactivateUserCommit.apply(userIMD);
        //assert
        User returnedUser=userIMD.findUserById("111");
        Assert.assertTrue(returnedUser.isActive());
        UserCommitStatus userCommitStatus=userIMD.findCommitStatusById("112");
        Assert.assertEquals("Failed",userCommitStatus.getStatus());
        DeactiveUserCommitResponse deactiveUserCommitResponse=(DeactiveUserCommitResponse)userIMD.findCommitResponseById("112");
        Assert.assertFalse(deactiveUserCommitResponse.isDone());
    }
}
