package com.olexyn.abricore.model.runtime.strategy;

import com.olexyn.abricore.model.runtime.ProtoDto;
import com.olexyn.abricore.model.runtime.TradeDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class CycleDto extends ProtoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 5536482907195908087L;

    private Instant from = null;
    private Instant to = null;
    private final List<TradeDto> trades = new ArrayList<>();
    private long fitness;

}
