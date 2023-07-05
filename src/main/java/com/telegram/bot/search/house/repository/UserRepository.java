package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByChatId(Long chatId);

    User findUserById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAllByIsActive(Boolean isActive);
}
