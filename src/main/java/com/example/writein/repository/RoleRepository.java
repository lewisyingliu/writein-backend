package com.example.writein.repository;

import java.util.Optional;

import com.example.writein.models.ERole;
import com.example.writein.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
