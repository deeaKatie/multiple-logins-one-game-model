package repository;

import exception.RepositoryException;
import model.Game;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserDBRepository implements IUserRepository{

    private static final Logger logger= LogManager.getLogger();
    private Session session;


    public UserDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public User add(User user) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(user);
        user.setId(id);
        transaction.commit();
        return user;
    }

    @Override
    public void delete(User user) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.delete(user);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public void update(User user, Long id) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        user.setId(id);
        session.update(user);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public User findById(Long idToFind) throws RepositoryException {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        User entity = session.get(User.class, idToFind);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<User> getAll() {
        SQLQuery query = session.createSQLQuery("select * from user");
        query.addEntity(User.class);
        List<User> users = query.list();
        return users;
    }

    private User parseUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("cod_u");
        String username = rs.getString("username");
        String password = rs.getString("password");
        User user = new User(username, password);
        user.setId(id);
        System.out.println(user);
        return user;
    }

    @Override
    public User findUserByUsername(String username) throws RepositoryException {
        //todo implement the boy
        return null;
    }
}
