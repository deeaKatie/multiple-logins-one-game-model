package repository;

import exception.RepositoryException;

public interface IRepository <E,ID>{
    E add(E entity);
    void delete(E entity);
    void update(E entity, ID id);
    E findById(ID id) throws RepositoryException;
    Iterable<E> getAll();
}
