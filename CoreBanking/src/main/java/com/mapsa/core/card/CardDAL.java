package com.mapsa.core.card;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;
public class CardDAL {
    private Configuration conf;
    private ServiceRegistry reg;
    private SessionFactory sf;
    private Session session;
    private Transaction tx;

    public CardDAL() {
        openSession();
    }
    
    private void openSession() {
        this.conf = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Card.class);
        this.reg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        this.sf = conf.buildSessionFactory(reg);
        this.session = sf.openSession();
    }

    public Boolean saveCard(Card card) {
        try {
            if (!session.isConnected()) {
                openSession();

            }
            session.beginTransaction();
            session.saveOrUpdate(card);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
            sf.close();
        }
    }

    public boolean saveCards(List<Card> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                saveCard(list.get(i));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Card> loadCards() {
        try {
            if (!session.isConnected()) {
                openSession();
            }
            session.beginTransaction();
            Query query = session.createQuery("from Card");
            List<Card> cards = query.list();
            session.getTransaction().commit();
            return cards;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sf.close();
        }
    }

    public Card getCardById(String cardId) {
        try {
            if (!session.isConnected()) {
                openSession();
            }
            session.beginTransaction();
            Card card = (Card) session.get(Card.class, cardId);
            session.getTransaction().commit();
            return card;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
            sf.close();
        }
    }

    public Card getCardByAccountId(String accountId) {
        if (!session.isConnected()) {
            openSession();
        }

        try {
            session.beginTransaction();
            Query query = session.createQuery("from com.mapsa.core.card.Card where accountId= :p1");
            query.setParameter("p1", accountId);
            session.getTransaction().commit();
            if (query.list().size()>0){
                return (Card) query.list().get(0);
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
}


