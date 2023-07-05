package repository;

import exception.RepositoryException;
import model.Game;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public UserDBRepository(Properties properties) {
        logger.info("Initializing UserRepo with properties {}",properties);
        dbUtils = new JdbcUtils(properties);
    }

//    public void smth () {
//        Game game = new Game();
//
//        User u1 = new User();
//        u1.setId(1L);
//        u1.setUsername("ana");
//        u1.setPassword("ana");
//
//        User u2 = new User();
//        u1.setId(2L);
//        u1.setUsername("bob");
//        u1.setPassword("bob");
//
//        game.addPlayer(u1);
//        game.addPlayer(u2);
//
//        Map<User, String> cards = new ConcurrentHashMap<>();
//        cards.put(u1, "89AK");
//        cards.put(u2, "67JQ");
//
//        game.setPlayerCards(cards);
//
//        ArrayList<User> winner = new ArrayList<>();
//        winner.add(u1);
//        game.setWinners(winner);
//
//        ArrayList<String> winnerscards = new ArrayList<>();
//        winnerscards.add("89AK67JQ");
//        game.setWinnersCards(winnerscards);
//
//
//        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                .configure() // configures settings from hibernate.cfg.xmlmes
//                .build();
//
//        SessionFactory sessionFactory;
//        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//
//
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = null;
//            try {
//                transaction = session.beginTransaction();
//                //session.save(game);
//                List games = session.createQuery("from Game").list();
//                System.out.println("GAMESS");
//                for (Iterator it = games.iterator(); it.hasNext();) {
//                    Game g = (Game) it.next();
//                    System.out.println(g);
//                }
//                transaction.commit();
//            } catch (RuntimeException ex) {
//                System.err.println("Eroare la inserare "+ex);
//                if (transaction != null)
//                    transaction.rollback();
//            }
//        }
//    }
    @Override
    public User add(User user) {
//        logger.traceEntry();
//        Connection connection = dbUtils.getConnection();
//        try(PreparedStatement ps = connection.prepareStatement("insert into users (username,password) values (?,?)")){
//            ps.setString(1,user.getUsername());
//            ps.setString(2,user.getPassword());
//            int result = ps.executeUpdate();
//            logger.trace("Saved {} instances",result);
//        }catch (SQLException ex){
//            logger.error(ex);
//        }
//        logger.traceExit();
        return null;
    }

    @Override
    public void delete(User user) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement ps = connection.prepareStatement("delete from users where cod_u=?")){
            ps.setLong(1,user.getId());
            int result = ps.executeUpdate();
            logger.trace("Saved {} instances",result);
        }catch (SQLException ex){
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(User user, Long id) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement ps = connection.prepareStatement("update users set username=?, password=? where cod_u=?")){
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setLong(3,id);
            int result = ps.executeUpdate();
            logger.trace("Saved {} instances",result);
        }catch (SQLException ex){
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public User findById(Long idToFind) throws RepositoryException {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement ps = connection.prepareStatement("select * from users where cod_u=?")){
            ps.setLong(1,idToFind);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    return parseUser(rs);
                }
            }
        }catch (SQLException ex){
            logger.trace(ex);
        }
        throw new RepositoryException("Nonexistent User");
    }

    @Override
    public Iterable<User> getAll() {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement("select * from users")){
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    System.out.println(parseUser(rs));
                    users.add(parseUser(rs));
                }
            }
        }catch (SQLException ex){
            logger.trace(ex);
        }
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

        System.out.println("----------- GET ALL");
        getAll();
        System.out.println("------------------------Connection not ok\n");
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        System.out.println("Connection ok\n");
        try(PreparedStatement ps = connection.prepareStatement("select * from users where username=?")){
            System.out.println("in try cathc!\n");
            System.out.println("User ane,: " + username);
            ps.setString(1,username);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    logger.traceExit();
                    return parseUser(rs);
                }
            }
        }catch (SQLException ex){
            logger.trace(ex);
        }
        logger.traceExit();
        throw new RepositoryException("Nonexistent User");
    }
}
