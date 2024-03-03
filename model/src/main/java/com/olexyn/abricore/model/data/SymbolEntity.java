package com.olexyn.abricore.model.data;

import com.olexyn.abricore.model.runtime.assets.AssetType;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import com.olexyn.abricore.util.enums.OptionStatus;
import lombok.Getter;
import lombok.Setter;

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

/**
 * SymbolEntity is a data transfer object for SymbolDto.
 *
 * @see com.olexyn.abricore.model.runtime.assets.OptionDto
 */
@Getter
@Setter
@Entity
@Table(
    name = "T_SYMBOL",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID"),
        @UniqueConstraint(columnNames = "NAME")
    }
)
public class SymbolEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = 6224544291505961290L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OptionStatus status;

    @Column(name = "EXPIRY")
    private Instant expiry;

    @Column(name = "RATIO", length = 30)
    private String ratio;

    @Column(name = "TW_SYMBOL", length = 30)
    private String twSymbol;

    @Column(name = "SQ_ISIN", length = 30)
    private String sqIsin;

    @Column(name = "STRIKE", length = 30)
    private String strike;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXCHANGE")
    private Exchange exchange;

    @Enumerated(EnumType.STRING)
    @Column(name = "ASSET_TYPE")
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPTION_TYPE")
    private OptionType optionType;

    @Column(name = "UNDERLYING", length = 30)
    private String underlying;

    @Column(name = "NAME", length = 30)
    private String name;



    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public OptionStatus getStatus() {
        return status;
    }

    public void setStatus(OptionStatus status) {
        this.status = status;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getTwSymbol() {
        return twSymbol;
    }

    public void setTwSymbol(String twSymbol) {
        this.twSymbol = twSymbol;
    }

    public String getSqIsin() {
        return sqIsin;
    }

    public void setSqIsin(String sqIsin) {
        this.sqIsin = sqIsin;
    }

    public String getStrike() {
        return strike;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


