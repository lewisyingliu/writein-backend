package com.example.writein.repository;

import com.example.writein.models.entities.CountingBoard;
import com.example.writein.models.entities.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface CountingBoardRepository extends JpaRepository<CountingBoard, Long> {

    List<CountingBoard> findByTitle(String title);

    List<CountingBoard> findByTitleContaining(String title);

    List<CountingBoard> findByElection(Election election);
}
