package com.example.writein.repository;

import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

@Query(value = "select u.* from users u inner join user_roles ur on u.id = ur.user_id inner join roles r on r.id = ur.role_id where r.name = 'ROLE_ADMIN'", nativeQuery = true)
List<User> findAdminUser();

@Query(value = "select u.id, u.username, u.email, u.password from users u inner join user_roles ur on u.id = ur.user_id inner join roles r on r.id = ur.role_id where r.name = 'ROLE_USER'", nativeQuery = true)
List<User> findWriteInUser();

    List<User> findByElection(Election election);

    List<User> findByUsernameContaining(String username);
}
