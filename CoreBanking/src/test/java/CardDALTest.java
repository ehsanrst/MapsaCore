import com.mapsa.core.account.Account;
import com.mapsa.core.card.Card;
import com.mapsa.core.card.CardDAL;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CardDALTest {
    CardDAL cardDAL = new CardDAL();
    SessionFactory sessionFactory;
    Session session;

    @Before
    public void runBeforeTestMethod() {
        sessionFactory = new Configuration().addAnnotatedClass(Card.class).configure("hibernate.cfg.xml").buildSessionFactory();session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from com.mapsa.core.card.Card");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }


    @Test
    public void saveCard() {
        //setup
        Card card1 = new Card("1", "20", "1111", "220", "2000");
        Card card3 = new Card("3", "21", "4444", "221", "2001");
        Card card2 = new Card("1", "20", "1111", "220", "2000");
        Card output = null;

        //test save
        boolean val1 = cardDAL.saveCard(card1);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

        //test add new element
        val1 = cardDAL.saveCard(card3);
        assertTrue(val1);
        output = cardDAL.getCardById("3");
        assertEquals("3", output.getCardId());
        assertEquals("21", output.getCvv2());
        assertEquals("4444", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("221", output.getAccountId());
        assertEquals("2001", output.getLastCommitId());
        assertEquals(2, cardDAL.loadCards().size());

        //test duplicate id
        val1 = cardDAL.saveCard(card2);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());
        assertEquals(2, cardDAL.loadCards().size());

        //test update element
        //deactive cardId1
        card1.deactive("2002");
        val1 = cardDAL.saveCard(card1);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(false, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2002", output.getLastCommitId());

        // change password cardId2
        card3.upDateCardPass("4444", "5555", "2003");
        val1 = cardDAL.saveCard(card3);
        assertTrue(val1);
        output = cardDAL.getCardById("3");
        assertEquals("3", output.getCardId());
        assertEquals("21", output.getCvv2());
        assertEquals("5555", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("221", output.getAccountId());
        assertEquals("2003", output.getLastCommitId());
    }

    @Test
    public void saveCards() {
        List<Card> cardList1 = new ArrayList<Card>();
        List<Card> cardList2 = new ArrayList<Card>();
        List<Card> cardList3 = new ArrayList<Card>();
        //setup

        cardList1.add(new Card("1", "20", "1111", "220", "2000"));
        cardList1.add(new Card("2", "21", "2222", "221", "2001"));

        cardList2.add(new Card("1", "20", "1111", "220", "2000"));
        cardList2.add(new Card("2", "21", "2222", "221", "2001"));

        Card card5 = new Card("1", "20", "1111", "220", "2000");
        cardList3.add(card5);
        Card card6 = new Card("2", "21", "2222", "221", "2001");
        cardList3.add(card6);
        Card card7 = new Card("3", "22", "3333", "222", "2002");
        cardList3.add(card7);
        Card card8 = new Card("4", "23", "7777", "223", "2003");
        cardList3.add(card8);
        Card output = null;

        //test save list
        boolean val1 = cardDAL.saveCards(cardList1);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

        output = cardDAL.getCardById("2");
        assertEquals("2", output.getCardId());
        assertEquals("21", output.getCvv2());
        assertEquals("2222", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("221", output.getAccountId());
        assertEquals("2001", output.getLastCommitId());

        //test duplicate list
        val1 = cardDAL.saveCards(cardList2);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

        output = cardDAL.getCardById("2");
        assertEquals("2", output.getCardId());
        assertEquals("21", output.getCvv2());
        assertEquals("2222", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("221", output.getAccountId());
        assertEquals("2001", output.getLastCommitId());

        assertEquals(2, cardDAL.loadCards().size());

        //test update all list
        val1 = cardDAL.saveCards(cardList3);
        assertTrue(val1);
        output = cardDAL.getCardById("1");
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

        output = cardDAL.getCardById("2");
        assertEquals("2", output.getCardId());
        assertEquals("21", output.getCvv2());
        assertEquals("2222", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("221", output.getAccountId());
        assertEquals("2001", output.getLastCommitId());

        output = cardDAL.getCardById("3");
        assertEquals("3", output.getCardId());
        assertEquals("22", output.getCvv2());
        assertEquals("3333", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("222", output.getAccountId());
        assertEquals("2002", output.getLastCommitId());

        output = cardDAL.getCardById("4");
        assertEquals("4", output.getCardId());
        assertEquals("23", output.getCvv2());
        assertEquals("7777", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("223", output.getAccountId());
        assertEquals("2003", output.getLastCommitId());

        assertEquals(4, cardDAL.loadCards().size());

        //test update element of list
        //deactive cardId3
        card7.deactive("2004");
        cardList3.set(2, card7);
        //change password cardId4
        card8.upDateCardPass("7777", "8888", "2005");
        cardList3.set(3, card8);
        val1 = cardDAL.saveCards(cardList3);
        assertTrue(val1);
        output = cardDAL.getCardById("3");
        assertEquals("3", output.getCardId());
        assertEquals("22", output.getCvv2());
        assertEquals("3333", output.getPassword());
        assertEquals(false, output.isActive());
        assertEquals("222", output.getAccountId());
        assertEquals("2004", output.getLastCommitId());

        output = cardDAL.getCardById("4");
        assertEquals("4", output.getCardId());
        assertEquals("23", output.getCvv2());
        assertEquals("8888", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("223", output.getAccountId());
        assertEquals("2005", output.getLastCommitId());

        assertEquals(4, cardDAL.loadCards().size());

    }

    @Test
    public void loadCards() {
        //setup
        Card card1 = new Card("1", "20", "1111", "220", "2000");
        Card card2 = new Card("2", "21", "2222", "221", "2001");
        boolean val1 = cardDAL.saveCard(card1);
        assertTrue(val1);
        val1 = cardDAL.saveCard(card2);
        assertTrue(val1);

        //test loadCards
        List<Card> listcards = cardDAL.loadCards();
        assertEquals(2, cardDAL.loadCards().size());
        assertEquals("1", listcards.get(0).getCardId());
        assertEquals("20", listcards.get(0).getCvv2());
        assertEquals("1111", listcards.get(0).getPassword());
        assertEquals(true, listcards.get(0).isActive());
        assertEquals("220", listcards.get(0).getAccountId());
        assertEquals("2000", listcards.get(0).getLastCommitId());

        assertEquals("2", listcards.get(1).getCardId());
        assertEquals("21", listcards.get(1).getCvv2());
        assertEquals("2222", listcards.get(1).getPassword());
        assertEquals(true, listcards.get(1).isActive());
        assertEquals("221", listcards.get(1).getAccountId());
        assertEquals("2001", listcards.get(1).getLastCommitId());

        assertEquals(2, cardDAL.loadCards().size());
        assertEquals(2, listcards.size());
    }

    @Test
    public void getCardById() {
        //setup
        Card card1 = new Card("1", "20", "1111", "220", "2000");
        Card card2 = new Card("2", "21", "2222", "221", "2001");
        boolean val1 = cardDAL.saveCard(card1);
        assertTrue(val1);
        val1 = cardDAL.saveCard(card2);
        assertTrue(val1);
        Card output = null;

        //test loadCardById with wrong CardNumber
        output = cardDAL.getCardById("3");
        assertTrue(val1);
        assertEquals(null, output);

        //test loadCardById with correct CardNumber
        output = cardDAL.getCardById("1");
        assertTrue(val1);
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

    }

    @Test
    public void getCardByAccountId() {
        //setup
        Card card1 = new Card("1", "20", "1111", "220", "2000");
        Card card2 = new Card("2", "21", "2222", "221", "2001");
        boolean val1 = cardDAL.saveCard(card1);
        assertTrue(val1);
        val1 = cardDAL.saveCard(card2);
        assertTrue(val1);
        Card output = null;

        //test loadCardByAccountId with wrong AccountId
        output = cardDAL.getCardByAccountId("227");
        assertTrue(val1);
        assertEquals(null, output);

        //test loadCardAccountId with correct AccountId
        output = cardDAL.getCardByAccountId("220");
        assertTrue(val1);
        assertEquals("1", output.getCardId());
        assertEquals("20", output.getCvv2());
        assertEquals("1111", output.getPassword());
        assertEquals(true, output.isActive());
        assertEquals("220", output.getAccountId());
        assertEquals("2000", output.getLastCommitId());

    }
}