package com.example.eCommerce.repositories;

import com.example.eCommerce.model.AppRole;
import com.example.eCommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAppRole(AppRole appRole);
}