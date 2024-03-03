package com.olexyn.abricore.store.runtime;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface DtoService<T> extends IService {

    DtoService<T> update(Set<T> dtos);

}
