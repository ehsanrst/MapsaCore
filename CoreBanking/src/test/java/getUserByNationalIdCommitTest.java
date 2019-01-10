import com.mapsa.core.UserIMD;
import com.mapsa.core.UserIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.account.AccountStatus;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.GetUserByNationalIdCommit;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
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

import java.util.ArrayList;

public class getUserByNationalIdCommitTest {
    @Before
    public void beforeRun(){
        Configuration cfg = new Configuration().configure().addAnnotatedClass(Account.class).addAnnotatedClass(AccountCommitLog.class)
                .addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class);
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
    public void getUserByNationalIdCommitApplyTest(){
        //setup
        User user=new User("111","milad","aminzade","55","6532");
        UserIMD userIMD=new UserIMDImple();
        userIMD.addUser(user);
        GetUserByNationalIdCommit getUserByNationalIdCommit=new GetUserByNationalIdCommit();
        getUserByNationalIdCommit.setNationalId("55");
        getUserByNationalIdCommit.setCUID("255");
        //act
        getUserByNationalIdCommit.apply(userIMD);
        //assert
        User returnedUser=userIMD.findUserById(user.getId());
        Assert.assertEquals("111",returnedUser.getId());
        Assert.assertEquals("milad",returnedUser.getName());
        Assert.assertEquals("aminzade",returnedUser.getFamilyName());
        Assert.assertEquals("55",returnedUser.getNationalId());
        UserCommitStatus userCommitStatus=userIMD.findCommitStatusById(getUserByNationalIdCommit.getCUID());
        Assert.assertEquals("Done",userCommitStatus.getStatus());
    }
    @Test
    public void getUserByNationalIdCommitIfUserNotExist(){
        //setup
        User user=new User("111","milad","aminzade","55","6532");
        UserIMD userIMD=new UserIMDImple();
        userIMD.addUser(user);
        GetUserByNationalIdCommit getUserByNationalIdCommit=new GetUserByNationalIdCommit();
        getUserByNationalIdCommit.setCUID("255");
        getUserByNationalIdCommit.setNationalId("56");
        //act
        getUserByNationalIdCommit.apply(userIMD);
        //assert
        UserCommitStatus userCommitStatus=userIMD.findCommitStatusById(getUserByNationalIdCommit.getCUID());
        Assert.assertEquals("Failed",userCommitStatus.getStatus());
    }
}
