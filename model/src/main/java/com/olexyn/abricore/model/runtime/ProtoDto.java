package com.olexyn.abricore.model.runtime;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public abstract class ProtoDto implements Dto<ProtoDto> {

    @Serial
    private static final long serialVersionUID = -9143909897035119771L;

    private Long id = null;

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public ProtoDto mergeFrom(ProtoDto other) {
        return this;
    }

}
