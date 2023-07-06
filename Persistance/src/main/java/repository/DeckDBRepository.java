package repository;

import exception.RepositoryException;
import model.Deck;
import model.Game;
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

public class DeckDBRepository implements IDeckDBRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();
    Session session;

    public DeckDBRepository(Properties properties) {
        logger.info("Initializing DeckDBRepository with properties {}",properties);
        dbUtils = new JdbcUtils(properties);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Deck add(Deck entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(Deck entity) {

    }

    @Override
    public void update(Deck entity, Long aLong) {

    }

    @Override
    public Deck findById(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Iterable<Deck> getAll() {
        SQLQuery query = session.createSQLQuery("select * from deck");
        query.addEntity(Deck.class);
        List<Deck> entities = query.list();
        return entities;
    }
}
