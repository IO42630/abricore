package com.olexyn.abricore.model.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * SnapshotEntity is a data transfer object for SnapShotDto.
 *
 * @see com.olexyn.abricore.model.runtime.snapshots.SnapshotDto
 */
@Getter
@Setter
@Entity
@Table(name = "T_SNAPSHOT", uniqueConstraints = {
    @UniqueConstraint(columnNames = "ID"),
    @UniqueConstraint(columnNames = {"INSTANT", "ASSET"})
}, indexes = @Index(columnList = "INSTANT, ASSET", name = "IDX_INSTANT_ASSET", unique = true))
public class SnapshotEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = 2320270945713051887L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "INSTANT", nullable = false)
    private Instant instant;

    @Column(name = "ASSET", nullable = false, length = 30)
    private String asset;

    @Column(name = "TRADED_PRICE", length = 30)
    private String tradedPrice;

    @Column(name = "BID_PRICE", length = 30)
    private String bidPrice;

    @Column(name = "ASK_PRICE", length = 30)
    private String askPrice;

    @Column(name = "PRICE_RANGE", length = 30)
    private String range;

    @Column(name = "VOLUME", length = 30)
    private String volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        SnapshotEntity that = (SnapshotEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(instant, that.instant) &&
            Objects.equals(asset, that.asset) &&
            Objects.equals(tradedPrice, that.tradedPrice) &&
            Objects.equals(bidPrice, that.bidPrice) &&
            Objects.equals(askPrice, that.askPrice)
            && Objects.equals(range, that.range) &&
            Objects.equals(volume, that.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instant, asset, tradedPrice, bidPrice, askPrice, range, volume);
    }
}


