package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @ReleaseDateConstraint
    private LocalDate releaseDate;

    @NotNull
    private long duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @NotNull
    private Mpa mpa;

    @Builder.Default
    private Set<Genre> genres = new LinkedHashSet<>();

    @JsonProperty("duration")
    @Positive
    public long getDuration() {
        return duration;
    }

    public void addLike(long id) {
        likes.add(id);
    }

    public void deleteLike(long id) {
        likes.remove(id);
    }

    public int getLikesCount() {
        return likes.size();
    }

    public List<Genre> getGenres() {
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }
}