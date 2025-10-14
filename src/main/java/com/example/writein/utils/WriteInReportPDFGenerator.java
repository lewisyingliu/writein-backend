package com.example.writein.utils;

import com.example.writein.models.entities.CountingBoard;
import com.example.writein.models.entities.Office;
import com.example.writein.models.entities.User;
import com.example.writein.models.entities.WriteInRecord;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class WriteInReportPDFGenerator {
    private static Logger logger = LoggerFactory.getLogger(WriteInReportPDFGenerator.class);

    public static ByteArrayInputStream writeInPDFReport(List<WriteInRecord> writeInRecords, List<CountingBoard> countingBoards) throws IOException {
        countingBoards.sort(Comparator.comparing(CountingBoard::getDisplayOrder));
        Map<Office, List<WriteInRecord>> groupByOffice = writeInRecords.stream().collect(groupingBy(WriteInRecord::getOffice));
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.setMargins(36, 36, 36, 36);
            document.open();
            if (!writeInRecords.isEmpty()) {
                for (CountingBoard countingBoard : countingBoards) {
                    document.add(addCountingBoard(countingBoard));
                    document.add(Chunk.NEWLINE);
                    List<Office> officeList = countingBoard.getOffices();
                    officeList.sort(Comparator.comparing(Office::getDisplayOrder));
                    for (Office office : officeList) {
                        document.add(Chunk.NEWLINE);
                        document.add(addOffice(office));
                        document.add(addHeader());
                        if (groupByOffice.containsKey(office)) {
                            Map<String, List<WriteInRecord>> groupByName = groupByOffice.get(office).stream()
                                    .filter(e -> (e.getCountingBoard().getId()).equals(countingBoard.getId()))
                                    .collect(groupingBy(n -> "First Name:" + n.getFirstName() + "Middle Name:" + n.getMiddleName() + "Last Name:" + n.getLastName()));
                            for (Map.Entry<String, List<WriteInRecord>> entry : groupByName.entrySet()) {
                                document.add(addVoters(entry.getValue()));
                            }
                        }
                    }
                    document.newPage();
                }
            } else {
                document.add(new Chunk(""));
            }
            document.close();
        } catch (DocumentException e) {
            logger.error(e.toString());
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static Phrase addCountingBoard(CountingBoard countingBoard) {
        Font FONT_SIZE_20_BOLD = new Font(Font.HELVETICA, 20f, Font.BOLD);
        Paragraph countingBoardParagraph = new Paragraph();
        countingBoardParagraph.setFont(FONT_SIZE_20_BOLD);
        countingBoardParagraph.setAlignment(Element.ALIGN_CENTER);
        countingBoardParagraph.add("COUNTING BOARD: " + countingBoard.getTitle().toUpperCase());
        return countingBoardParagraph;
    }

    private static Phrase addOffice(Office office) {
        Font FONT_SIZE_18_BOLD = new Font(Font.HELVETICA, 18f, Font.BOLD);
        Paragraph officeTitleParagraph = new Paragraph();
        officeTitleParagraph.setFont(FONT_SIZE_18_BOLD);
        officeTitleParagraph.setAlignment(Element.ALIGN_LEFT);
        officeTitleParagraph.add(office.getTitle().toUpperCase());
        return officeTitleParagraph;
    }

    private static PdfPTable addHeader() {
        PdfPTable table = new PdfPTable(new float[]{2, 2, 2, 1});
        table.setSpacingBefore(5);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(100f);
        table.addCell(new PdfPCell(new Phrase("First Name")));
        table.addCell(new PdfPCell(new Phrase("Middle Name")));
        table.addCell(new PdfPCell(new Phrase("Last Name")));
        table.addCell(new PdfPCell(new Phrase("Total Votes")));
        return table;
    }

    private static PdfPTable addVoters(List<WriteInRecord> writeInRecords) {
        WriteInRecord firstRecord = writeInRecords.get(0);
        PdfPTable table = new PdfPTable(new float[]{2, 2, 2, 1});
        table.setSpacingBefore(5);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(100f);
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(firstRecord.getFirstName()));
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(firstRecord.getMiddleName()));
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(firstRecord.getLastName()));
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(writeInRecords.size())));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        return table;
    }
}