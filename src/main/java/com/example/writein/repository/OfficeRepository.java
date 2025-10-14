package com.example.writein.repository;

import com.example.writein.models.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface OfficeRepository extends JpaRepository<Office, Long> {

    List<Office> findByTitle(String title);
}
