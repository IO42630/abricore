package com.olexyn.abricore.store.dao;

import org.springframework.transaction.annotation.Transactional;

public interface StringKeyDao<E, D> {

    @Transactional(readOnly = true)
    D find(String keyString);

}
