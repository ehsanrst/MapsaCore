package com.mapsa.core.logger;

import com.mapsa.core.account.Account;
import com.mapsa.core.commits.account.AccountCommit;
import com.mapsa.core.commits.status.AccountCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.AccountCommitLog;
import com.mapsa.core.log.AccountCommitResponseLog;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public class AccountCommitLogger {
    private Configuration conf;
    private ServiceRegistry reg;
    private SessionFactory sf;
    private Session session;

    public AccountCommitLogger() {
        openSession();
    }

    public boolean saveCommit(AccountCommitLog accountCommitLog) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            session.beginTransaction();
            session.save(accountCommitLog);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            session.close();
            sf.close();
        }
    }

    public boolean saveCommitStatus(CommitStatus commitStatus) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(commitStatus);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
            sf.close();
        }
    }

    public boolean saveListCommitStatus(List<AccountCommitStatus> commitstatusList) {
        try {
            for (AccountCommitStatus commitst : commitstatusList) {
                saveCommitStatus(commitst);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AccountCommitStatus> loadAllCommitStatus() {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.AccountCommitStatus");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountCommitStatus findCommitStatus(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        AccountCommitStatus accountCommitStatus = null;
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.CommitStatus where commitID= :commitID");
            query.setParameter("commitID", commitId);
            if (query.list().size() > 0) {
                return (AccountCommitStatus) query.list().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountCommitLog findCommitById(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        AccountCommitLog accountCommitLog = null;
        if(commitId==null){
            return null;
        }
        try {
            Query query = session.createQuery("from AccountCommitLog where commitId= :commitId");
            query.setParameter("commitId", commitId);
            if (query.list().size() > 0) {
                return (AccountCommitLog) query.list().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;


    }

    public List<AccountCommit> findCommitsRecursively(String commitId, int depth) {

        List<AccountCommit> output = new ArrayList<AccountCommit>();

        String commitIdToFind = commitId;
        for (int i = 0; i < depth; i++) {
            AccountCommitLog temp = findCommitById(commitIdToFind);
            if (temp != null) {
                output.add((temp.getCommit()));
                commitIdToFind = temp.getPreviousCommitId();
            }
            if (commitIdToFind == null || temp == null) {
                break;
            }
        }
        return output;
    }

    public boolean saveCommitResponse(AccountCommitResponseLog accountCommitResponseLog) {
        if (!session.isConnected()) {
            openSession();
        }
        Transaction transaction = session.beginTransaction();
        try {
            session.save(accountCommitResponseLog);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            return false;
        } finally {
            session.close();
            sf.close();
        }


    }

    public AccountCommitResponseLog findCommitResponseByCommitId(String commitId) {
        if (!session.isConnected()) {
            openSession();
        }
        String hql = "FROM com.mapsa.core.log.AccountCommitResponseLog A WHERE A.commitId=:I";
        Query query = session.createQuery(hql);
        query.setParameter("I", commitId);
        AccountCommitResponseLog accountCommitResponseLog = null;
        if (query.list().size() > 0)
            accountCommitResponseLog = (AccountCommitResponseLog) query.list().get(0);
        session.close();
        sf.close();
        return accountCommitResponseLog;

    }

    private void openSession() {
        this.conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(AccountCommitLog.class).addAnnotatedClass(AccountCommitResponseLog.class).addAnnotatedClass(AccountCommitStatus.class);
        this.reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        this.sf = conf.buildSessionFactory(reg);
        this.session = sf.openSession();
    }
}
