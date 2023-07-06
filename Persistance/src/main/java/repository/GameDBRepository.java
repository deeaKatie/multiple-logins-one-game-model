package repository;

import exception.RepositoryException;
import model.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Properties;

public class GameDBRepository implements IGameDBRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();
    Session session;

    public GameDBRepository(Properties properties) {
        logger.info("Initializing GameRepo with properties {}",properties);
        dbUtils = new JdbcUtils(properties);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Game add(Game entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(Game entity) {

    }

    @Override
    public void update(Game entity, Long aLong) {

    }

    @Override
    public Game findById(Long gameId) throws RepositoryException {
        Transaction transaction = session.beginTransaction();
        Game entity = session.get(Game.class, gameId);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<Game> getAll() {
        return null;
    }
}
