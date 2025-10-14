package com.example.writein.controllers;

import com.example.writein.dto.UserInput;
import com.example.writein.models.entities.CountingBoard;
import com.example.writein.models.entities.Election;
import com.example.writein.payload.response.MessageResponse;
import com.example.writein.repository.CountingBoardRepository;
import com.example.writein.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.writein.utils.Constants.API_ENDPOINT;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(API_ENDPOINT)
public class CountingBoardController {
    private final CountingBoardRepository countingBoardRepository;
    private final ElectionRepository electionRepository;

    @GetMapping("/countingBoards")
    public ResponseEntity<List<CountingBoard>> getAllCountingBoards() {
        try {
            List<CountingBoard> countingBoards = countingBoardRepository.findAll();
            if (countingBoards.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(countingBoards, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/countingBoards/elections/{id}")
    public ResponseEntity<CountingBoard> createCountingBoard(@PathVariable("id") Long id, @RequestBody CountingBoard countingBoard) {
        try {
            Optional<Election> electionData = electionRepository.findById(id);
            if (electionData.isPresent()) {
                countingBoard.setElection(electionData.get());
                CountingBoard savedCountingBoard = countingBoardRepository.save(countingBoard);
                return new ResponseEntity<>(savedCountingBoard, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/countingBoards/{id}")
    public ResponseEntity<CountingBoard> updateCountingBoard(@PathVariable("id") Long id, @RequestBody CountingBoard countingBoard) {
        Optional<CountingBoard> countingBoardOptional = countingBoardRepository.findById(id);
        if (countingBoardOptional.isPresent()) {
            CountingBoard savedCountingBoard = countingBoardOptional.get();
            savedCountingBoard.setTitle(countingBoard.getTitle());
            savedCountingBoard.setDisplayOrder(countingBoard.getDisplayOrder());
            return new ResponseEntity<>(countingBoardRepository.save(savedCountingBoard), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/countingBoards/deleteBatch")
    public ResponseEntity<HttpStatus> deleteInBatch(@RequestBody List<Long> ids) {
        try {
            countingBoardRepository.deleteAllByIdInBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/countingBoard/{title}")
    public ResponseEntity<MessageResponse> getCountingBoardByTitle(@PathVariable String title) {
        List<CountingBoard> countingBoards = countingBoardRepository.findByTitle(title);
        if (!countingBoards.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }

    @PostMapping("/countingBoard")
    public ResponseEntity<MessageResponse> getCountingBoardByUserInput(@RequestBody UserInput userInput) {
        List<CountingBoard> countingBoards = countingBoardRepository.findByTitleContaining(userInput.getInitString());
        if (!countingBoards.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }

    @PostMapping("/countingBoards/createBatch/{id}")
    public ResponseEntity<Void> createBatchCountingBoards(@PathVariable("id") Long id, @RequestBody UserInput userInput) {
        try {
            Optional<Election> electionOptional = electionRepository.findById(id);
            int startNumber = userInput.getStartNumber();
            int endNumber = userInput.getEndNumber();
            String initString = userInput.getInitString();
            if (electionOptional.isPresent() && startNumber >= 1 && endNumber >= 2 && endNumber > startNumber) {
                for (int i = startNumber; i <= endNumber; i++) {
                    String title = initString + i;
                    CountingBoard newCountingBoard = new CountingBoard();
                    newCountingBoard.setElection(electionOptional.get());
                    newCountingBoard.setTitle(title);
                    newCountingBoard.setDisplayOrder(i);
                    countingBoardRepository.save(newCountingBoard);
                }
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
