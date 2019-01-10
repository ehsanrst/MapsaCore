package com.mapsa.core.user;

import com.mapsa.core.account.AccountStatus;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;

public class UserDAL {
    private Configuration conf;
    private ServiceRegistry reg;
    private SessionFactory sf;
    private Session session;
    private Transaction tx;

    public UserDAL() {
        openSession();
    }

    public boolean saveUser(User user) {
        try {
            if (!this.session.isConnected()) {
                openSession();
            }
            this.tx = session.beginTransaction();
            this.session.saveOrUpdate(user);
//            for (int i = 0; i < user.getAccounts().size(); i++) {
//                this.session.saveOrUpdate(user.getAccounts().get(i));
//            }
            this.tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
            sf.close();
        }
    }

    public boolean saveUsers(List<User> userList) {
        try {
            if (!this.session.isConnected()) {
                openSession();
            }
            for (User user : userList) {
                saveUser(user);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loadUserById(String userId) {
        User user = null;
        try {
            if (!this.session.isConnected()) {
                openSession();
            }
            Query query = session.createQuery("from User where id= :id");
            query.setParameter("id", userId);
            if (query.list().size() > 0) {
                return (User) query.list().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sf.close();
        }
    }

    public User loadUserByNationalId(String nationalId) {
        try {
            if (!this.session.isConnected()) {
                openSession();
            }
            Query query = session.createQuery("from User where nationalId= :nationalId");
            query.setParameter("nationalId", nationalId);
            if (query.list().size() > 0) {
                return (User) query.list().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sf.close();
        }
    }

    public List<User> loadUsers() {
        try {
            if (!this.session.isConnected()) {
                openSession();
            }
            Query query = session.createQuery("from User");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sf.close();
        }
    }

    private void openSession() {
        this.conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);
        this.reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        this.sf = conf.buildSessionFactory(reg);
        this.session = sf.openSession();
    }

}
