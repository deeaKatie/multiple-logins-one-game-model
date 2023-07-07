package repository;

import exception.RepositoryException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class GameDBRepository implements IGameDBRepository {

    private static final Logger logger= LogManager.getLogger();
    private Session session;

    @Autowired
    public GameDBRepository() {

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
        SQLQuery query = session.createSQLQuery("select * from game");
        query.addEntity(Game.class);
        List<Game> games = query.list();

        System.out.println("GAMES");
        for (var g : games) {
            System.out.println(g);
        }
        System.out.println("END");

        return games;
    }
}
