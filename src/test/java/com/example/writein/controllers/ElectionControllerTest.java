package com.example.writein.controllers;

import com.example.writein.models.EElection;
import com.example.writein.models.entities.Election;
import com.example.writein.repository.ElectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElectionControllerTest {

    @Mock
    private ElectionRepository electionRepository;

    @InjectMocks
    private ElectionController electionController;

    @Test
    void getAllElections_whenEmpty_returnsNoContent() {
        when(electionRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Election>> response = electionController.getAllElections();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAllElections_whenExists_returnsOk() {
        Election election = new Election();
        election.setTitle("Test Election");
        when(electionRepository.findAll()).thenReturn(List.of(election));

        ResponseEntity<List<Election>> response = electionController.getAllElections();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getElectionById_whenExists_returnsOk() {
        Election election = new Election();
        election.setTitle("Test");
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));

        ResponseEntity<Election> response = electionController.getElectionlById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getElectionById_whenNotExists_returnsNotFound() {
        when(electionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Election> response = electionController.getElectionlById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void findByDefaultTag_whenExists_returnsOk() {
        Election election = new Election();
        election.setDefaultTag(true);
        when(electionRepository.findByDefaultTag(true)).thenReturn(List.of(election));

        ResponseEntity<Election> response = electionController.findByDefaultTag();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void findByDefaultTag_whenEmpty_returnsNoContent() {
        when(electionRepository.findByDefaultTag(true)).thenReturn(Collections.emptyList());

        ResponseEntity<Election> response = electionController.findByDefaultTag();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void createElection_whenFirstElection_setsDefaultTag() {
        Election election = new Election();
        election.setTitle("First");
        election.setCode("E001");
        election.setElectionDate(LocalDate.now());

        when(electionRepository.findAll()).thenReturn(Collections.emptyList());
        when(electionRepository.save(any(Election.class))).thenReturn(election);

        ResponseEntity<Election> response = electionController.createElection(election);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(election.isDefaultTag());
    }

    @Test
    void createElection_withDefaultTag_clearsOthers() {
        Election existing = new Election();
        existing.setDefaultTag(true);

        Election newElection = new Election();
        newElection.setTitle("New");
        newElection.setCode("E002");
        newElection.setElectionDate(LocalDate.now());
        newElection.setDefaultTag(true);

        when(electionRepository.findAll()).thenReturn(List.of(existing));
        when(electionRepository.save(any(Election.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Election> response = electionController.createElection(newElection);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertFalse(existing.isDefaultTag());
    }

    @Test
    void updateElection_whenExists_returnsOk() {
        Election existing = new Election();
        existing.setCode("E001");
        existing.setTitle("Old");
        existing.setElectionDate(LocalDate.now());
        existing.setStatus(EElection.PrePublished);

        Election updated = new Election();
        updated.setCode("E001-U");
        updated.setTitle("Updated");
        updated.setDefaultTag(false);
        updated.setElectionDate(LocalDate.now().plusDays(1));
        updated.setStatus(EElection.Published);

        when(electionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(electionRepository.findAll()).thenReturn(Collections.emptyList());
        when(electionRepository.save(any(Election.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Election> response = electionController.updateElection(1L, updated);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateElection_whenNotExists_returnsNotFound() {
        when(electionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Election> response = electionController.updateElection(1L, new Election());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteElection_returnsNoContent() {
        doNothing().when(electionRepository).deleteById(1L);

        ResponseEntity<Map<String, Boolean>> response = electionController.deleteElection(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteByBatch_returnsNoContent() {
        doNothing().when(electionRepository).deleteAllByIdInBatch(any());
        when(electionRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<HttpStatus> response = electionController.deleteByBatch(List.of(1L, 2L));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteByBatch_setsDefaultTagWhenNoneRemaining() {
        Election remaining = new Election();
        remaining.setDefaultTag(false);

        doNothing().when(electionRepository).deleteAllByIdInBatch(any());
        when(electionRepository.findAll()).thenReturn(List.of(remaining));
        when(electionRepository.save(any(Election.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<HttpStatus> response = electionController.deleteByBatch(List.of(1L));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(remaining.isDefaultTag());
    }
}
