package com.example.writein.controllers;

import com.example.writein.models.entities.Election;
import com.example.writein.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.example.writein.utils.Constants.API_ENDPOINT;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(API_ENDPOINT)
public class ElectionController {

    private final ElectionRepository electionRepository;

    @GetMapping("/elections")
    public ResponseEntity<List<Election>> getAllElections() {
        try {
            List<Election> elections = electionRepository.findAll();
            if (elections.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(elections, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<Election> getElectionlById(@PathVariable("id") long id) {
        Optional<Election> electionData = electionRepository.findById(id);
        return electionData.map(election -> new ResponseEntity<>(election, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/elections/defaultTag")
    public ResponseEntity<Election> findByDefaultTag() {
        try {
            List<Election> elections = electionRepository.findByDefaultTag(true);
            if (elections.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(elections.get(0), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/elections")
    public ResponseEntity<Election> createElection(@RequestBody Election election) {
        try {
            List<Election> otherElections = electionRepository.findAll();
            if (otherElections.isEmpty()) {
                election.setDefaultTag(true);
            } else {
                if (election.isDefaultTag()) {
                    otherElections.forEach(e -> {
                        e.setDefaultTag(false);
                        electionRepository.save(e);
                    });
                }
            }
            Election savedElection = electionRepository.save(election);
            return new ResponseEntity<>(savedElection, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<Election> updateElection(@PathVariable("id") Long id, @RequestBody Election election) {
        Optional<Election> electionOptional = electionRepository.findById(id);
        if (electionOptional.isPresent()) {
            Election savedElection = electionOptional.get();
            savedElection.setCode(election.getCode());
            savedElection.setTitle(election.getTitle());
            savedElection.setDefaultTag(election.isDefaultTag());
            savedElection.setElectionDate(election.getElectionDate());
            savedElection.setStatus(election.getStatus());
            List<Election> otherElections = electionRepository.findAll().stream().filter(e -> !Objects.equals(e.getId(), id)).toList();
            if (savedElection.isDefaultTag() && !otherElections.isEmpty()) {
                otherElections.forEach(e -> {
                    e.setDefaultTag(false);
                    electionRepository.save(e);
                });
            }
            return new ResponseEntity<>(electionRepository.save(savedElection), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteElection(@PathVariable("id") Long id) {
        try {
            electionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/elections/deleteBatch")
    public ResponseEntity<HttpStatus> deleteByBatch(@RequestBody List<Long> ids) {
        try {
            electionRepository.deleteAllByIdInBatch(ids);
            List<Election> elections = electionRepository.findAll();
            if (!elections.isEmpty()) {
                List<Election> inUseElections = elections.stream().filter(Election::isDefaultTag).toList();
                if (inUseElections.isEmpty()) {
                    Election firstElection = elections.get(0);
                    firstElection.setDefaultTag(true);
                    electionRepository.save(firstElection);
                }
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
