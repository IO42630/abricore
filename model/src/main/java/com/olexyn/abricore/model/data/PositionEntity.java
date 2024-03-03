package com.olexyn.abricore.model.data;

import com.olexyn.abricore.util.enums.PositionStatus;
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

@Getter
@Setter
@Entity
@Table(name = "T_POSITION", uniqueConstraints = {@UniqueConstraint(columnNames = "ID")})
public class PositionEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = -7304799133134336508L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "ASSET", nullable = false, length = 30)
    private String asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private PositionStatus status;

    @Column(name = "AMOUNT", nullable = false, length = 30)
    private String amount;

    @Column(name = "PRICE", nullable = false, length = 30)
    private String price;

    public void setId(@Nullable Long id) {
        if (id == null) { return; }
        this.id = id;
    }

    public void setAsset(@Nullable String asset) {
        if (asset == null) { return; }
        this.asset = asset;
    }


    public void setStatus(@Nullable PositionStatus status) {
        if (status == null) { return; }
        this.status = status;
    }

    public void setAmount(@Nullable String amount) {
        if (amount == null) { return; }
        this.amount = amount;
    }

    public void setPrice(@Nullable String price) {
        if (price == null) { return; }
        this.price = price;
    }

}


