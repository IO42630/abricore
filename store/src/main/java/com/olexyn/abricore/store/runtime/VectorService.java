package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.dao.VectorDao;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class VectorService implements IService {

    private static final Set<VectorDto> VECTORS = new HashSet<>();
    private final VectorDao vectorDao;

    @Autowired
    public VectorService(VectorDao vectorDao) {
        this.vectorDao = vectorDao;

        VECTORS.addAll(vectorDao.findDtos());

    }

    @Override
    @Synchronized
    public void save() {
        vectorDao.deleteAll();
        vectorDao.saveDtos(getVectors());
    }

    public void add(VectorDto vector) {
        VECTORS.add(vector);
    }

    public void remove(VectorDto vector) {
        VECTORS.remove(vector);
    }

    public void addAll(Set<VectorDto> vectors) {
        VECTORS.addAll(vectors);
    }

    public Set<VectorDto> getVectors() {
        return VECTORS;
    }


}
