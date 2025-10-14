package com.example.writein.controllers;

import com.example.writein.models.entities.CountingBoard;
import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.WriteInRecord;
import com.example.writein.repository.CountingBoardRepository;
import com.example.writein.repository.ElectionRepository;
import com.example.writein.repository.WriteInRepository;
import com.example.writein.utils.WriteInCSVExportService;
import com.example.writein.utils.WriteInReportPDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.writein.controllers.WriteInController.getMapResponseEntity;
import static com.example.writein.utils.Constants.API_ENDPOINT;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(API_ENDPOINT)
public class RecordController {
    private final WriteInRepository writeInRepository;
    private final ElectionRepository electionRepository;
    private final CountingBoardRepository countingBoardRepository;
    private final WriteInCSVExportService writeInCSVExportService;

    @GetMapping("/records/elections/{electionId}")
    public ResponseEntity<Map<String, Object>> getRecordsByElection(@PathVariable("electionId") Long electionId,
                                                                    @RequestParam(required = false) String filter,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        try {
            Optional<Election> electionOptional = electionRepository.findById(electionId);
            Election election = electionOptional.orElse(null);
            List<Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] splitedSort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(splitedSort[1]), splitedSort[0]));
                }
            } else {
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
            Page<WriteInRecord> pageWriteInRecords;
            if (filter == null) {
                pageWriteInRecords = writeInRepository.findByElection(election, pagingSort);
            } else {
                pageWriteInRecords = writeInRepository.searchByElection(election.getId(), filter, pagingSort);
            }
            return getMapResponseEntity(pageWriteInRecords);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/records/{id}")
    public ResponseEntity<WriteInRecord> updateWriteIn(@PathVariable("id") Long id, @RequestBody WriteInRecord writeInRecord) {
        Optional<WriteInRecord> writeInRecordOptional = writeInRepository.findById(id);
        if (writeInRecordOptional.isPresent()) {
            WriteInRecord savedWriteInRecord = writeInRecordOptional.get();
            savedWriteInRecord.setOffice(writeInRecord.getOffice());
            savedWriteInRecord.setCountingBoard(writeInRecord.getCountingBoard());
            savedWriteInRecord.setBatchNumber(writeInRecord.getBatchNumber());
            savedWriteInRecord.setFirstName(writeInRecord.getFirstName());
            savedWriteInRecord.setMiddleName(writeInRecord.getMiddleName());
            savedWriteInRecord.setLastName(writeInRecord.getLastName());
            return new ResponseEntity<>(writeInRepository.save(savedWriteInRecord), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/records/deleteBatch")
    public ResponseEntity<HttpStatus> deleteInBatch(@RequestBody List<Long> ids) {
        try {
            writeInRepository.deleteAllByIdInBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/records/printPdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> printPdf(@PathVariable("id") Long id) throws IOException {
        try {
            Optional<Election> electionOptional = electionRepository.findById(id);
            if (electionOptional.isPresent()) {
                List<WriteInRecord> writeInRecords = writeInRepository.findByElection(electionOptional.get());
                List<CountingBoard> countingBoards = countingBoardRepository.findByElection(electionOptional.get());
                ByteArrayInputStream bis = WriteInReportPDFGenerator.writeInPDFReport(writeInRecords, countingBoards);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=writeInReport.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/records/exportExcel")
    public void exportToCSV(HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"writeins.csv\"");
        writeInCSVExportService.writeWriteInsToCsv(servletResponse.getWriter());
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
}
