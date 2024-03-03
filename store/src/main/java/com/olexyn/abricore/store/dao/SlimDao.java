package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.AbstractEntity;
import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.util.log.LogU;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;


/**
 * <b>Contract:</b><br>
 * - Dao returns/accepts only Dto. <br>
 * - Dto leave the Dao <b>not</b> fully initialized. <br>
 */
public abstract class SlimDao<E extends AbstractEntity, D extends Dto> {

    private static final int PAGE_SIZE = 10000;

    protected Mapper mapper;

    protected static final DateTimeFormatter SQL_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
        .withZone(ZoneId.systemDefault());

    protected SlimDao(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Used to access child's repo from generic parent logic.
     */
    protected abstract JpaRepository<E, Long> getRepo();

    @Transactional(readOnly = true)
    public @Nullable E find(Long id) {
        return getRepo().findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<E> findAll() {
        return getRepo().findAll();
    }

    public void cosume(Consumer<E> consumer) {
        int pageNumber = 0;
        Page<E> page;
        do {
            page = getRepo().findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            page.getContent().forEach(consumer);
            pageNumber++;
        } while (pageNumber < page.getTotalPages());
    }

    public E save(E entity) {
        return getRepo().save(entity);
    }

    protected void saveAll(List<E> entities) {
        var newE = entities.stream().filter(e -> e.getId() == null).count();
        var oldE = entities.size() - newE;
        LogU.save("EXISTING: %-10d NEW:    %-10d %-10s", oldE, newE, classPrint(entities));
        getRepo().saveAll(entities);
    }

    protected String classPrint(Collection<?> collection) {
        return collection.stream().map(x -> x.getClass().getSimpleName()).findFirst().orElse("?");
    }

    public void saveDtos(Set<D> dtos) {
        var saves = dtos.stream()
            .map(this::toEntity)
            .toList();
        saveAll(saves);
    }

    protected abstract D toDto(E entity);

    protected abstract E toEntity(D dto);


    public void delete(List<D> dtos) {
        var deletes = dtos.stream().map(this::toEntity).toList();
        getRepo().deleteAll(deletes);
    }

    public void deleteAll() {
        getRepo().deleteAll();
    }

}
