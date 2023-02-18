package com.telegram.bot.search.house.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Floor {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "'from'")
    private Integer from;

    @Column(name = "'to'")
    private Integer to;

    @OneToOne
    @JoinColumn(name = "search_criteria_id", referencedColumnName = "id", nullable = false)
    private SearchCriteria searchCriteria;
}
