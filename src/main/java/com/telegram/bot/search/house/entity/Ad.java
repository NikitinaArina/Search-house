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

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url,
            title,
            location;
    private Long price,
            floor,
            year,
            square;
    @Enumerated(EnumType.STRING)
    private RoomDto rooms;
    @Enumerated(EnumType.STRING)
    private OwnerDto owner;
    @Enumerated(EnumType.STRING)
    private RenovationDto renovationType;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;

    private String filename;

    @ManyToOne
    private User usr;
}
