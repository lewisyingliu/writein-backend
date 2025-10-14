package com.example.writein.utils;

import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.WriteInRecord;
import com.example.writein.repository.ElectionRepository;
import com.example.writein.repository.WriteInRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WriteInCSVExportService {
    private static Logger logger = LoggerFactory.getLogger(WriteInCSVExportService.class);

    @Autowired
    WriteInRepository writeInRepository;
    @Autowired
    ElectionRepository electionRepository;

    public WriteInCSVExportService() {
    }

    public void writeWriteInsToCsv(Writer writer) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        List<Election> elections = electionRepository.findByDefaultTag(true);
        List<WriteInRecord> writeInRecords = writeInRepository.findByElection(elections.get(0));
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("First Name", "Middle Name", "Last Name", "Batch Number", "Officer", "Counting Board", "Election", "Voter Total", "Created Time", "Updated Time", "User Name");
            for (WriteInRecord c : writeInRecords) {
                csvPrinter.printRecord(c.getFirstName(), c.getMiddleName(), c.getLastName(), c.getBatchNumber(), c.getOffice().getTitle(), c.getCountingBoard().getTitle(),
                        c.getElection().getTitle(), String.valueOf(c.getRecordCount()), dateFormatter.format(c.getCreatedAt()), dateFormatter.format(c.getUpdatedAt()), c.getUser().getUsername());
            }
        } catch (IOException e) {
            logger.error("Error While writing CSV ", e);
        }
    }
}
