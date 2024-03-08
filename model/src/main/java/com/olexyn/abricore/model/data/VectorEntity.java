package com.olexyn.abricore.model.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.sql.rowset.serial.SerialClob;
import java.io.Serial;
import java.io.Serializable;


/**
 * The purpose of this entity is to store a viable vector.
 * <p>
 * The rank indicates an approximate ranking relative to other vectors.
 * E.g. if a vector is in the top 10 it might gain some points.
 * If a vector loses to some other vector it might have to give away some points.
 */
@Getter
@Setter
@Entity
@Table(name = "T_VECTOR", uniqueConstraints = {@UniqueConstraint(columnNames = {"ID"})})
public class VectorEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = -7041315843146454888L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "PROFIT_BY_DAY")
    private Long profitByDay;

    @Column(name = "PROFIT_BY_VOLUME")
    private Long profitByVolume;

    @Column(name = "RATING")
    private Long rating;

    @Column(name = "PARAM_MAP", length = 100000)
    private SerialClob paramMap;

    @Column(name = "SAMPLE_COUNT")
    private Long sampleCount;

    @Column(name = "AVG_DURATION")
    private Long avgDuration;

}


