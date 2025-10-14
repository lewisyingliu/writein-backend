package com.example.writein.controllers;

import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.Office;
import com.example.writein.payload.response.MessageResponse;
import com.example.writein.repository.ElectionRepository;
import com.example.writein.repository.OfficeRepository;
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
public class OfficeController {
    private final OfficeRepository officeRepository;
    private final ElectionRepository electionRepository;

    @GetMapping("/offices")
    public ResponseEntity<List<Office>> getAllOffices() {
        try {
            List<Office> offices = officeRepository.findAll();
            if (offices.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(offices, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/offices/elections/{id}")
    public ResponseEntity<Office> createOffice(@PathVariable("id") Long id, @RequestBody Office office) {
        try {
            Optional<Election> electionData = electionRepository.findById(id);
            if (electionData.isPresent()) {
                office.setElection(electionData.get());
                Office savedOffice = officeRepository.save(office);
                return new ResponseEntity<>(savedOffice, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/offices/{id}")
    public ResponseEntity<Office> updateOffice(@PathVariable("id") Long id, @RequestBody Office office) {
        Optional<Office> officeOptional = officeRepository.findById(id);
        if (officeOptional.isPresent()) {
            Office savedOffice = officeOptional.get();
            savedOffice.setTitle(office.getTitle());
            savedOffice.setDisplayOrder(office.getDisplayOrder());
            savedOffice.setCountingBoards(office.getCountingBoards());
            return new ResponseEntity<>(officeRepository.save(savedOffice), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/offices/deleteBatch")
    public ResponseEntity<HttpStatus> deleteInBatch(@RequestBody List<Long> ids) {
        try {
            officeRepository.deleteAllByIdInBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/office/{title}")
    public ResponseEntity<MessageResponse> getOfficeByTitle(@PathVariable String title) {
        List<Office> offices = officeRepository.findByTitle(title);
        if (!offices.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }
}
