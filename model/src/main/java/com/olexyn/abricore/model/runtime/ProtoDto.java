package com.olexyn.abricore.model.runtime;





import lombok.Getter;

@Getter
public abstract class ProtoDto implements Dto<ProtoDto> {

    private final Long id = null;

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public ProtoDto mergeFrom(ProtoDto other) {
        return this;
    }

}
