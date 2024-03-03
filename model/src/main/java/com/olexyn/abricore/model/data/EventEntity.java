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
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "T_EVENT", uniqueConstraints = @UniqueConstraint(columnNames = {"EVENT_KEY"}))
public class EventEntity implements Serializable, AbstractEntity {

    @Serial
    private static final long serialVersionUID = 8876770018937771232L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "EVENT_KEY", nullable = false, length = 255)
    private String eventKey;

    @Column(name = "VALUE", nullable = false, length = 255)
    private String value;

}


