import com.mapsa.core.UserIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.user.AddUserCommit;
import com.mapsa.core.commits.user.AddUserCommitResponse;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import com.mapsa.core.user.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddUserCommitApplyTest {
    private Configuration configuration;
    private ServiceRegistry registry;
    private SessionFactory sessionFactory;
    private Session s;

    @Before
    public void beforeRun() {
        this.configuration = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class).addAnnotatedClass(UserCommitStatus.class)
                .addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class);
        this.registry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        this.sessionFactory = configuration.buildSessionFactory(registry);
        s = sessionFactory.openSession();
        try {
            Transaction transaction = s.beginTransaction();
            Query query = s.createQuery("delete from User");
            query.executeUpdate();
            query = s.createQuery("delete from Account");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitResponseLog");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitStatus");
            query.executeUpdate();
            query = s.createQuery("delete from UserCommitLog");
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sessionFactory.close();
        }
    }
    @After
    public void AfterRun(){
        beforeRun();
    }

    @Test
    public void AddUserCommitApplyTest() {
        //setup
        AddUserCommit user = new AddUserCommit("pedram", "soltani", "031105");
        UserIMDImple imd = new UserIMDImple();
        user.setCUID("1234");
        user.apply(imd);
        AddUserCommitResponse userCommitResponse = (AddUserCommitResponse) imd.findCommitResponseById("1234");
        String userId = userCommitResponse.getUserId();
        User returnedUser = imd.findUserById(userId);
        //Test
        Assert.assertEquals("pedram", returnedUser.getName());
        Assert.assertEquals("soltani", returnedUser.getFamilyName());
        Assert.assertEquals("031105", returnedUser.getNationalId());
        Assert.assertEquals(userId, returnedUser.getId());
        Assert.assertNull(returnedUser.getLastCommit());
        Assert.assertTrue(returnedUser.isActive());
        returnedUser.deactivate("5654");
        Assert.assertNotNull(returnedUser.getLastCommit());
        Assert.assertEquals("5654",returnedUser.getLastCommit());
        Assert.assertTrue(!returnedUser.isActive());
    }
}
