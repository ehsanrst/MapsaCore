package com.mapsa.core.logger;

import com.mapsa.core.card.Card;
import com.mapsa.core.commits.card.CardCommit;
import com.mapsa.core.commits.card.CardCommitResponse;
import com.mapsa.core.commits.status.CardCommitStatus;
import com.mapsa.core.log.CardCommitLog;
import com.mapsa.core.log.CardCommitResponseLog;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public class CardCommitLogger {
    private SessionFactory sessionFactory;
    private Session session;
    private Configuration conf;
    private ServiceRegistry reg;

    public CardCommitLogger() {
        openSession();
    }

    public boolean saveCommitStatus(CardCommitStatus commitStatus) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            session.beginTransaction();
            session.saveOrUpdate(commitStatus);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CardCommitStatus> loadCommitStatus() {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.CardCommitStatus");
            List<CardCommitStatus> commitStatuses = query.list();
            return commitStatuses;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveCommitStatus(List<CardCommitStatus> commitStatusList) {
        try {
            for (CardCommitStatus commitStatus : commitStatusList) {
                saveCommitStatus(commitStatus);
            }
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CardCommitStatus loadCommitStatusbyId(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        CardCommitStatus commitStatus = null;
        try {
            Query query = session.createQuery("from com.mapsa.core.commits.status.CardCommitStatus where commitId= :p1");
            Query query1 = query.setParameter("p1", commitId);
            if (query.list().size() > 0) {
                return (CardCommitStatus) query1.list().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveCommit(CardCommitLog cardCommitLog) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            Query query = session.createQuery("from CardCommitLog where commitId= :commitId");
            query.setParameter("commitId", cardCommitLog.getCommitId());
            if (query.list().size() > 0) {
                return true;
            }
            session.beginTransaction();
            session.saveOrUpdate(cardCommitLog);
            session.getTransaction().commit();
            session.close();
            this.sessionFactory.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public CardCommitLog findCommitById(String commitId) {
        if (!this.session.isConnected()) {
            openSession();
        }
        try {
            CardCommitLog cardCommitLog;
            Query query = session.createQuery("from CardCommitLog where commitID= :commitId");
            query.setParameter("commitId", commitId);
            if (query.list().size() == 1) {
                return (CardCommitLog) query.list().get(0);

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CardCommit> findCommitsRecursively(String inputCommitId, int depth) {

        List<CardCommit> output = new ArrayList<CardCommit>();

        String commitIdToFind = inputCommitId;
        for (int i = 0; i < depth; i++) {
            CardCommitLog temp = findCommitById(commitIdToFind);
            if (temp != null) {
                output.add(temp.getCommit());
                commitIdToFind = temp.getPreviousCommitId();
            }
            if (commitIdToFind == null || temp == null) {
                break;
            }
        }
        return output;
    }

    public boolean saveCommitResponse(CardCommitResponseLog cardCommitResponseLog) {
        if (!session.isConnected()) {
            openSession();
        }
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery("from CardCommitResponseLog where commitId= :commitId");
            query.setParameter("commitId", cardCommitResponseLog.getCommitId());
            if (query.list().size() > 0) {
                return true;
            }
            session.saveOrUpdate(cardCommitResponseLog);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    public CardCommitResponseLog findCommitResponseByCommitId(String commitId) {
        if (!session.isConnected()) {
            openSession();
        }
        try {
            Query query = session.createQuery("from CardCommitResponseLog where commitId= :commitId");
            query.setParameter("commitId", commitId);
            CardCommitResponseLog cardCommitResponseLog = null;
            if (query.list().size() > 0) {
                cardCommitResponseLog = (CardCommitResponseLog) query.list().get(0);
            }
            return cardCommitResponseLog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    private void openSession() {
        this.conf = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Card.class).addAnnotatedClass(CardCommitLog.class).addAnnotatedClass(CardCommitResponseLog.class).addAnnotatedClass(CardCommitStatus.class);
        this.reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        this.sessionFactory = conf.buildSessionFactory(reg);
        this.session = sessionFactory.openSession();
    }
}
