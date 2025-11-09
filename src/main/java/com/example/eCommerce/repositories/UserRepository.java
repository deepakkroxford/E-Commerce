package com.example.eCommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.eCommerce.model.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
