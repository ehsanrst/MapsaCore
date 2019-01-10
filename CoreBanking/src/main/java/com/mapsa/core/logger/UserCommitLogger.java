package com.mapsa.core.logger;

import com.mapsa.core.commits.user.UserCommit;
import com.mapsa.core.commits.status.UserCommitStatus;
import com.mapsa.core.commits.status.CommitStatus;
import com.mapsa.core.log.UserCommitLog;
import com.mapsa.core.log.UserCommitResponseLog;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserCommitLogger {
    private Configuration conf;
    private ServiceRegistry reg;
    private SessionFactory sf;
    private Session session;

    public UserCommitLogger() {
        openSession();
    }

    public boolean saveCommit(UserCommitLog userCommitLog) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            session.beginTransaction();
            session.save(userCommitLog);
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

    public boolean saveListCommitStatus(List<UserCommitStatus> commitstatusList) {
        try {
            for (UserCommitStatus commitst : commitstatusList) {
                saveCommitStatus(commitst);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserCommitStatus> loadAllCommitStatus() {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.UserCommitStatus");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserCommitStatus findCommitStatus(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        UserCommitStatus userCommitStatus = null;
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.CommitStatus where commitID= :commitID");
            query.setParameter("commitID", commitId);
            if (query.list().size() > 0) {
                return (UserCommitStatus) query.list().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserCommitLog findCommitById(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        UserCommitLog userCommitLog = null;
        try {
            Query query = session.createQuery("from UserCommitLog where commitId= :commitId");
            query.setParameter("commitId", commitId);
            if (query.list().size() > 0) {
                return (UserCommitLog) query.list().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;


    }

    public List<UserCommit> findCommitsRecursively(String commitId, int depth) {

        List<UserCommit> output = new ArrayList<UserCommit>();

        String commitIdToFind = commitId;
        for (int i = 0; i < depth; i++) {
            UserCommitLog temp = findCommitById(commitIdToFind);
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

    public boolean saveCommitResponse(UserCommitResponseLog userCommitResponseLog) {
        if (!session.isConnected()) {
            openSession();
        }
        Transaction transaction = session.beginTransaction();
        try {
            session.save(userCommitResponseLog);
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

    public UserCommitResponseLog findCommitResponseByCommitId(String commitId) {
        if (!session.isConnected()) {
            openSession();
        }
        String hql = "FROM com.mapsa.core.log.UserCommitResponseLog A WHERE A.commitId=:I";
        Query query = session.createQuery(hql);
        query.setParameter("I", commitId);
        UserCommitResponseLog userCommitResponseLog = null;
        if (query.list().size() > 0)
            userCommitResponseLog = (UserCommitResponseLog) query.list().get(0);
        session.close();
        sf.close();
        return userCommitResponseLog;

    }

    private void openSession() {
        this.conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserCommitLog.class).addAnnotatedClass(UserCommitResponseLog.class).addAnnotatedClass(UserCommitStatus.class);
        this.reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        this.sf = conf.buildSessionFactory(reg);
        this.session = sf.openSession();
    }
}
