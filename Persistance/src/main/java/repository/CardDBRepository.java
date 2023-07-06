package repository;

import exception.RepositoryException;
import model.Card;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;
import java.util.Properties;

public class CardDBRepository implements ICardDBRepository {


    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();
    Session session;

    public CardDBRepository(Properties properties) {
        logger.info("Initializing UserRepo with properties {}",properties);
        dbUtils = new JdbcUtils(properties);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Card add(Card entity) {

        Transaction transaction = session.beginTransaction();

        Long id = (Long) session.save(entity);

        entity.setId(id);

        transaction.commit();

        return entity;
    }

    @Override
    public void delete(Card entity) {

    }

    @Override
    public void update(Card entity, Long aLong) {

    }

    @Override
    public Card findById(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Iterable<Card> getAll() {

        Transaction transaction = session.beginTransaction();

        SQLQuery query = session.createSQLQuery("select * from cards");
        query.addEntity(Card.class);
        List<Card> cards = query.list();

        transaction.commit();

        return cards;
    }
}
