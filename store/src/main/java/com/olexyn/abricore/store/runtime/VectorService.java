package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.dao.VectorDao;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class VectorService implements IService {

    private final VectorDao vectorDao;

    @Autowired
    public VectorService(VectorDao vectorDao) {
        this.vectorDao = vectorDao;
    }

    @Override
    @Synchronized
    public void save() {
        vectorDao.saveDtos(getVectors());
    }

    public void save(VectorDto vector) {
        vectorDao.saveDtos(Set.of(vector));
    }

    public void save(Collection<VectorDto> vectors) {
        vectorDao.saveDtos(Set.copyOf(vectors));
    }

    public void deleteWhereRatingSubZero() {
        var subZ = vectorDao.findDtos()
            .stream().filter(v -> v.getRating() < 0).toList();
        vectorDao.delete(subZ);
    }

    public void delete(VectorDto vector) {
       delete(List.of(vector));
    }

    public void delete(List<VectorDto> vectors) {
        vectorDao.delete(vectors);
    }


    public Set<VectorDto> getVectors() {
        return Set.copyOf(vectorDao.findDtos());
    }




}
