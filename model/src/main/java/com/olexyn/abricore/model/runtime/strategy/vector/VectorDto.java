package com.olexyn.abricore.model.runtime.strategy.vector;

import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.util.exception.DataCorruptionException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.num.NumSerialize.toStr;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;

/**
 * VectorDto is the Abricore version of a Vector.
 * It wraps a Map of String/BoundParam.
 * The keys can be arbitrary, but two Vectors of same type are expected to have the same KeySet.
 * BoundParam fullfill two functions:
 * - they know the boundaries
 * - the hold the value
 * This simplifies setting values, as we can always check / adjust the value to be withing the boundary.
 */
@Getter
public class VectorDto implements Serializable, Dto<VectorDto> {

    @Serial
    private static final long serialVersionUID = -8047079411636107509L;

    @Setter
    private Long id;

    @Setter
    private Long profitByDay = 0L;

    @Setter
    private Long profitByVolume = 0L;

    @Setter
    private Long rating = 0L;

    @Setter
    private Long sampleCount = 1L;

    @Setter
    private Long avgDuration = 1L;

    private final Map<VectorKey, BoundParam> paramMap = new EnumMap<>(VectorKey.class);

    public BoundParam getBoundParam(VectorKey key) {
        return paramMap.get(key);
    }


    private BoundParam getForceBoundParam(VectorKey key) {
        return paramMap.get(key);
    }

    public void normalizeToLowerBound(VectorKey key) {
        getForceBoundParam(key).normalizeToLowerBound();
    }

    public long getValue(VectorKey key) {
        return paramMap.get(key).getValue();
    }

    /**
     * Put a BoundParam into the Vector.
     * If the BoundParam has no value, the value will be randomly generated.
     */
    public void cloneParam(VectorKey key, BoundParam param) {
        paramMap.put(key, param.copy());
    }

    public void set(VectorKey key, long value) {
        paramMap.get(key).setValue(value);
    }

    public VectorDto generateRng() {
        VectorDto copy = clone();
        for (var entry : copy.paramMap.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue().generateRng());
        }
        return copy;
    }

    public VectorDto combine(VectorDto other) {
        if (!this.paramMap.keySet().equals(other.paramMap.keySet())) {
            throw new DataCorruptionException();
        }
        VectorDto result = clone();
        int vectorSize = result.paramMap.size();
        double pickAmount = Math.random() * vectorSize;
        for (int pick = 0; pick < pickAmount; pick++) {
            long pointer = 0;
            long selected = Math.round(Math.random() * vectorSize);
            for (var key : result.paramMap.keySet()) {
                if (pointer == selected) {
                    result.cloneParam(key, other.getBoundParam(key));
                }
                pointer++;
            }
        }
        return result;
    }


    /**
     * Each digit in each BoundParam will be randomized with probability of impulse.
     */
    public VectorDto mutate(long impulse) {
        VectorDto mutant = clone().clearParams();
        getParamMap().forEach(
            (key, value) -> mutant.getParamMap().put(key, value.mutate(impulse))
        );
        return mutant;
    }

    @Override
    public VectorDto clone() {
        var clone = new VectorDto();
        this.getParamMap().forEach(clone::cloneParam);
        return clone;
    }

    private VectorDto clearParams() {
        paramMap.clear();
        return this;
    }

    @Override
    public String toString() {
        return paramMap.values().stream()
            .map(boundParam -> prettyStr(boundParam.getValue(), 2) + ' ')
            .collect(Collectors.joining());
    }

    public String paramString() {
        return paramMap.values().stream()
            .map(boundParam -> toStr(boundParam.getValue()))
            .collect(Collectors.joining());
    }

    @Override
    public boolean isComplete() {
        throw new NotImplementedException();
    }

    @Override
    public VectorDto mergeFrom(VectorDto other) {
        throw new NotImplementedException();
    }


    @Override
    public int hashCode() {
        return Objects.hash(paramString());
    }

}
