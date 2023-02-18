package com.telegram.bot.search.house.entity;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SearchCriteria {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "search_criteria_id", referencedColumnName = "id")
    private List<Room> rooms = new ArrayList<>();

    private Long price;

    @Enumerated(EnumType.STRING)
    private OwnerDto owner;

    @Enumerated(EnumType.STRING)
    private RenovationDto renovation;

    private String location;

    private Boolean isChildren;

    private Boolean isAnimal;
}
