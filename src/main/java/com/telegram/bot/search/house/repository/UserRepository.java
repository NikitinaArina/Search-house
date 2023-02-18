package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
}
