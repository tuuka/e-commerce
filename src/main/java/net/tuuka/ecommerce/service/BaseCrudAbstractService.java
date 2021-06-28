package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Transactional
public abstract class BaseCrudAbstractService<T extends BaseEntity, ID, R extends JpaRepository<T, ID>>
        implements BaseCrudService<T, ID> {

    protected final R repository;

    public BaseCrudAbstractService(R repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T findById(ID id) {
        Objects.requireNonNull(id, "ID can not be null");
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Entity with id = "
                        + id + " not found"));
    }

    @Override
    public T save(T entity) {
        requireNotNullAndNullId(entity);
        return repository.save(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        requireNotNullAndNotNullId(entity);
        findById((ID) entity.getId());
        return repository.save(entity);
    }

    @Override
    public T deleteById(ID id) {
        T entity = findById(id);
        repository.deleteById(id);
        return entity;
    }

    protected void requireNotNullAndNullId(T entity) {
        requireNonNull(entity);
        if (entity.getId() != null)
            throw new IllegalStateException(String.format("%s.id have to be null, but %s given",
                    entity.getClass().getSimpleName(), entity.getId()));
    }

    protected void requireNotNullAndNotNullId(T entity) {
        requireNonNull(entity);
        if (entity.getId() == null)
            throw new IllegalStateException(String.format("%s.id have not to be null",
                    entity.getClass().getSimpleName()));
    }

    protected void requireNonNull(Object object) {
        Objects.requireNonNull(object, String.format("%s can't be null",
                object.getClass().getSimpleName()));
    }
}
