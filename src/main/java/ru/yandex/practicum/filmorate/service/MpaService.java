package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.JdbsMpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final JdbsMpaRepository repository;

    public List<Mpa> findAll() {
        return repository.findAll();
    }

    public Mpa findById(int id) {
        return repository.findById(id);
    }
}
