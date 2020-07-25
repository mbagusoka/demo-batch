package com.learn.demobatch.users.repository;

import com.learn.demobatch.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
