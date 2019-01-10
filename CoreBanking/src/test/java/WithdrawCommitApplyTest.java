import com.mapsa.core.AccountIMDImple;
import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.WithdrawCommit;
import com.mapsa.core.commits.account.WithdrawCommitResponse;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WithdrawCommitApplyTest {
    @Before
    public void runBeforeTestMethod() {
        SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class).buildSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();
        try {
            Query query = session.createQuery("delete from com.mapsa.core.commits.status.AccountCommitStatus");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.AccountCommitResponseLog");
            query.executeUpdate();
            query = session.createQuery("delete from com.mapsa.core.log.AccountCommitLog");
            query.executeUpdate();
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            sf.close();
        }
    }
    @Test
    public void WithdrawCommitApplyTest() {
        //withdrawTest
        Account account = new Account("1234", "2345", "3456");
        AccountIMDImple accountIMDImple = new AccountIMDImple();
        account.deposit("1000", "789");
        accountIMDImple.addAccount(account);
        WithdrawCommit withdrawCommit = new WithdrawCommit();
        withdrawCommit.setAccountId("1234");
        withdrawCommit.setAmount("500");
        withdrawCommit.setCUID("987");
        withdrawCommit.apply(accountIMDImple);
        Account returned_account = accountIMDImple.findAccountById("1234");
        assertEquals("500", returned_account.getBalance());
        AccountCommitStatus commitStatus =accountIMDImple.findCommitStatusById("1234");
        assertEquals("done", commitStatus.getStatus());
        WithdrawCommitResponse withdrawCommitResponse=(WithdrawCommitResponse)accountIMDImple.findCommitResponseById("987");
        assertTrue(withdrawCommitResponse.getDone());
    }
}
