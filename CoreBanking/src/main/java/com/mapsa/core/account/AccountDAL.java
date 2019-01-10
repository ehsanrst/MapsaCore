package com.mapsa.core.account;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class AccountDAL {
    private SessionFactory sessionFactory;
    private Session session;

    public AccountDAL() {
        connector();
    }

    private void connector() {
        sessionFactory = new Configuration().addAnnotatedClass(com.mapsa.core.account.Account.class).configure("hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public boolean saveAccount(Account account) {
        try {
            if (!session.isConnected()) {
                connector();
            }
            session.beginTransaction();
            session.saveOrUpdate(account);
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            System.out.print("Error...");
            return false;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    public Account loadAccount(String accountId) {

        try {
            if (!session.isConnected()) {
                connector();
            }
            session.beginTransaction();
            Account account =(Account) session.get(com.mapsa.core.account.Account.class,accountId);
            session.getTransaction().commit();
            return account;

        } catch (HibernateException e) {
            e.printStackTrace();
            System.out.print("Error...");
            return null;
        } finally {
            session.close();
            sessionFactory.close();
        }

    }

    public List<Account> loadAccounts() {
        try {
            if (!session.isConnected()) {
                connector();
            }
            session.beginTransaction();
            List<Account> accountsList = new ArrayList<Account>();
            accountsList.addAll(session.createCriteria(com.mapsa.core.account.Account.class).list());
            session.getTransaction().commit();
            return accountsList;
        } catch (HibernateException e) {
            e.printStackTrace();
            System.out.print("Error...");
            return null;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }

    public boolean saveAccounts(List<Account> accounts) {

        try {
            for (Account allAccounts : accounts) {
                saveAccount(allAccounts);
            }
            return true;
        } catch (HibernateException he) {
            he.getStackTrace();
            return false;
        }
    }
}
