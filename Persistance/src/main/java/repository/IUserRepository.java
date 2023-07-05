package repository;

import exception.RepositoryException;
import model.User;

public interface IUserRepository extends IRepository<User,Long> {
    User findUserByUsername(String username) throws RepositoryException;
}
