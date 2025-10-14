package com.example.writein.repository;

import com.example.writein.models.entities.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    @Query("select c from Election c " +
            "where lower(c.title) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.code) like lower(concat('%', :searchTerm, '%'))"
    )
    List<Election> search(@Param("searchTerm") String searchTerm);
    List<Election> findByDefaultTag(boolean defaultTag);
    List<Election> findByTitleContaining(String title);
}