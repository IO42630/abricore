package com.olexyn.abricore.store.runtime;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface IService {

    void save() throws IOException;

}
