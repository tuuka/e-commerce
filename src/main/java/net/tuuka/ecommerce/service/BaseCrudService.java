package net.tuuka.ecommerce.service;

import java.util.List;

interface BaseCrudService <T, ID> {

    List<T> findAll();

    T findById(ID id);

    T save(T entity);

    T update(T entity);

    T deleteById(ID id);

}
