package com.telegram.bot.search.house.entity;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.enums.RoomDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SearchCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "room", joinColumns = @JoinColumn(name = "id"))
    @ElementCollection(targetClass = RoomDto.class, fetch = FetchType.EAGER)
    private List<RoomDto> rooms;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "owner", joinColumns = @JoinColumn(name = "id"))
    @ElementCollection(targetClass = OwnerDto.class, fetch = FetchType.EAGER)
    private List<OwnerDto> owner;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "renovation", joinColumns = @JoinColumn(name = "id"))
    @ElementCollection(targetClass = RenovationDto.class, fetch = FetchType.EAGER)
    private List<RenovationDto> renovation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "floor_id", referencedColumnName = "id")
    private Floor floor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "year_id", referencedColumnName = "id")
    private Year year;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "search_criteria_id")
    private List<Location> location;
    private String city;
    private String name;

    private Boolean isChildren;

    private Boolean isAnimal;
    private Boolean isActive = false;

    private LocalDateTime lastSearch;
}
