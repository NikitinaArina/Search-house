package com.telegram.bot.search.house.entity;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url,
            title,
            location;
    private Long price,
            rooms,
            floor,
            year,
            square;
    @Enumerated(EnumType.STRING)
    private OwnerDto owner;
    @Enumerated(EnumType.STRING)
    private RenovationDto renovationType;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;
}
