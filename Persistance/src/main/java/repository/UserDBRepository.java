package repository;
import exception.RepositoryException;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserDBRepository implements IUserRepository{

    private static final Logger logger= LogManager.getLogger(UserDBRepository.class.getName());
    private Session session;

    @Autowired
    public UserDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public User add(User entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        logger.traceExit();
        return entity;
    }

    @Override
    public void delete(User entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public void update(User entity) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public User findById(Long id) throws RepositoryException {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        User entity = session.get(User.class, id);
        transaction.commit();
        logger.traceExit();
        return entity;
    }

    @Override
    public Iterable<User> getAll() {
        logger.traceEntry();
        Query query = session.createQuery("from User");
        List<User> entities = query.list();
        logger.traceExit();
        return entities;
    }

    @Override
    public User findUserByUsername(String username) throws RepositoryException {
        logger.traceEntry();
        Query query = session.createQuery("from User where username = :u");
        query.setParameter("u", username);
        User user = (User) query.uniqueResult();
        if (user == null) {
            logger.error("No user found!");
            throw new RepositoryException("No user found!");
        }
        logger.traceExit();
        return user;
    }
}
