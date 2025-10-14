package com.example.writein.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.writein.models.entities.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class UserPDFGenerator {
    private static Logger logger = LoggerFactory.getLogger(UserPDFGenerator.class);

    public static ByteArrayInputStream userPDFReport(List<User> users) {
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.setMargins(65, 65, 95, 5);
            document.open();
            Font FONT_SIZE_14_BOLD = new Font(Font.HELVETICA, 14f, Font.BOLD);
            if (users != null && !users.isEmpty()) {
                PdfPTable table = new PdfPTable(2);
                table.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.setWidthPercentage(100f);
                PdfPCell cell;
                for (User user : users) {
                    cell = new PdfPCell(new Paragraph("User Name: " + user.getUsername() + "\n" + "Password: " + user.getUserCode(), FONT_SIZE_14_BOLD));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(136f);
                    table.addCell(cell);
                }
                if ((users.size() & 1) != 0) {
                    cell = new PdfPCell(new Paragraph(new Chunk("")));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setFixedHeight(136f);
                    table.addCell(cell);
                }
                document.add(table);
            } else {
                document.add(new Chunk(""));
            }
            document.close();
        } catch (DocumentException e) {
            logger.error(e.toString());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}