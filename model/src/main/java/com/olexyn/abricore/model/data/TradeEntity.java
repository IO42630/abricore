package com.olexyn.abricore.model.data;

import com.olexyn.abricore.util.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "T_TRADE", uniqueConstraints = @UniqueConstraint(columnNames = {
    "ID", "BUY_ID", "SELL_ID"
}))
public class TradeEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = 8876770018937777492L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "ASSET", nullable = false, length = 30)
    private String asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private TradeStatus status;

    @Column(name = "BUY_INSTANT", length = 30)
    private Instant buyInstant;

    @Column(name = "SELL_INSTANT", length = 30)
    private Instant sellInstant;

    @Column(name = "AMOUNT", length = 30)
    private String amount;

    @Column(name = "BUY_PRICE", length = 30)
    private String buyPrice;

    @Column(name = "SELL_PRICE", length = 30)
    private String sellPrice;

    @Column(name = "BUY_ID", length = 30)
    private String buyId;

    @Column(name = "SELL_ID", length = 30)
    private String sellId;

    @Column(name = "BUY_FEE", length = 30)
    private String buyFee;

    @Column(name = "SELL_FEE", length = 30)
    private String sellFee;

    @Column(name = "UUID", nullable = false)
    private UUID uuid;


    public void setBuyFee(@Nullable String buyFee) {
        if (buyFee == null) { return; }
        this.buyFee = buyFee;
    }

    public void setSellFee(@Nullable String sellFee) {
        if (sellFee == null) { return; }
        this.sellFee = sellFee;
    }

}


