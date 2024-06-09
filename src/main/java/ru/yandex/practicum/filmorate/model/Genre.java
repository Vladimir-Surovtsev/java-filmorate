package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Genre {
    private int id;
    private String name;
}
