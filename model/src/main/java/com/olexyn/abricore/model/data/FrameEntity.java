package com.olexyn.abricore.model.data;

import com.olexyn.abricore.util.enums.FrameType;
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
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "T_FRAME", uniqueConstraints = @UniqueConstraint(columnNames = {"ID"}))
public class FrameEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = 8876770018937777412L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "ASSET", nullable = false, length = 30)
    private String asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "FRAME_TYPE", nullable = false, length = 30)
    private FrameType frameType;

    @Column(name = "START", length = 30)
    private Instant start;

    @Column(name = "END", length = 30)
    private Instant end;

    @Column(name = "DURATION", length = 30)
    private Duration duration;

}


