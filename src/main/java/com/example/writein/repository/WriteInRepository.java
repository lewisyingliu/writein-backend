package com.example.writein.repository;

import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.User;
import com.example.writein.models.entities.WriteInRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface WriteInRepository extends JpaRepository<WriteInRecord, Long> {

    @Query("select w from WriteInRecord w " +
            "join w.election e " +
            "join w.office o " +
            "join w.countingBoard c " +
            "where e.id = :electionId and (" +
            "lower(w.firstName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.middleName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.lastName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.batchNumber) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.creatorName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(o.title) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(c.title) like lower(concat('%', :searchTerm, '%')))"
    )
    Page<WriteInRecord> searchByElection(Long electionId, @Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("select w from WriteInRecord w " +
            "join w.election e " +
            "join w.user u " +
            "join w.office o " +
            "join w.countingBoard c " +
            "where e.id = :electionId and u.id = :userId and (" +
            "lower(w.firstName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.middleName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.lastName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.batchNumber) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(w.creatorName) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(o.title) like lower(concat('%', :searchTerm, '%')) or " +
            "lower(c.title) like lower(concat('%', :searchTerm, '%')))"
    )
    Page<WriteInRecord> searchByElectionAndUser(Long electionId, Long userId, @Param("searchTerm") String searchTerm, Pageable pageable);

    Page<WriteInRecord> findByElection(Election election, Pageable pageable);

    Page<WriteInRecord> findByElectionAndUser(Election election, User user, Pageable pageable);

    List<WriteInRecord> findByElection(Election election);
}
