package org.example.filmopoisk_client.repository;

import org.example.filmopoisk_client.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
