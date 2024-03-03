package com.olexyn.abricore.model.runtime;

import java.io.Serializable;

public interface Dto<D> extends Serializable {

    Long getId();

    boolean isComplete();

    D mergeFrom(D other);

}
