package com.olexyn.abricore.model.runtime;

import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SqDetail {

    private String isin;
    private Exchange exchange;
    private Currency currency;
}
