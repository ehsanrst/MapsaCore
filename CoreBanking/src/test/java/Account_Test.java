import com.mapsa.core.account.Account;
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

public class Account_Test {
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
    public void accountBlockAndUnblockTest(){
        //setup
        Account account=new Account("111","222","333");
        //act
        account.block("444");
        //assert
        Assert.assertTrue(account.getBlocked());
        //act
        account.unblock("555");
        //assert
        Assert.assertFalse(account.getBlocked());
    }
    @Test
    public void accountActiveAndDeactiveTest(){
        //setup
        Account account=new Account("111","222","333");
        //act
        account.deactive("444");
        //assert
        Assert.assertFalse(account.getActive());
        //act
        account.activate("555");
        //assert
        Assert.assertTrue(account.getActive());
    }
    @Test
    public void accountBalanceAndDepositTest(){
        //setup
        Account account=new Account("111","222","333");
        //assert zero Balance on created Account
        Assert.assertEquals("0",account.getBalance());
        //add money with deposit
        Assert.assertTrue(account.deposit("1000","555"));
        Assert.assertEquals("1000",account.getBalance());
        Assert.assertFalse(account.deposit("-1000","666"));
        account.deactive("777");
        Assert.assertFalse(account.deposit("1000","777"));
        account.activate("888");
        account.block("999");
        Assert.assertFalse(account.deposit("1000","1111"));
    }
    @Test
    public void accountBalanceAndWithdraw(){
        //setup
        Account account=new Account("111","222","333");
        //act add money to account for withdraw test
        account.deposit("2000","444");
        //assert
        Assert.assertTrue(account.withdraw("1000","555"));
        Assert.assertEquals("1000",account.getBalance());
        Assert.assertFalse(account.withdraw("3000","666"));
        account.block("777");
        Assert.assertFalse(account.withdraw("500","888"));
        account.unblock("999");
        account.deactive("911");
        Assert.assertFalse(account.withdraw("500","912"));
        account.activate("913");
        Assert.assertFalse(account.withdraw("-100","914"));
    }
}
