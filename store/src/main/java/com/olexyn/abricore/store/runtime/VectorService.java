package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.dao.VectorDao;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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
        vectorDao.saveDtos(getVectors());
    }

    public void save(VectorDto vector) {
        VECTORS.add(vector);
        vectorDao.saveDtos(Set.of(vector));
    }

    public void save(List<VectorDto> vectors) {
        VECTORS.addAll(vectors);
        vectorDao.saveDtos(Set.copyOf(vectors));
    }

//    public void add(VectorDto vector) {
//        VECTORS.add(vector);
//    }

    public void delete(VectorDto vector) {
        VECTORS.remove(vector);
        vectorDao.delete(List.of(vector));
    }

    public void delete(List<VectorDto> vectors) {
        vectors.forEach(VECTORS::remove);
        vectorDao.delete(vectors);
    }

    public void addAll(Set<VectorDto> vectors) {
        VECTORS.addAll(vectors);
    }

    public Set<VectorDto> getVectors() {
        return VECTORS;
    }


}
