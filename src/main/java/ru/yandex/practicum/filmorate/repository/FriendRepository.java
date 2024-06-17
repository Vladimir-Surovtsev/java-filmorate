package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

public interface FriendRepository {
    List<Friend> findFriendsOfUser(long id);
}
