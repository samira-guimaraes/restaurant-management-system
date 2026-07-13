import java.awt.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The PDFReportGenerator class provides functionality to generate various
 * reports in PDF format, including order reports, employee reports, and
 * booking reports. It includes methods for creating the reports, defining
 * table structures, and handling content rendering on the PDF documents.
 */
public class PDFReportGenerator {
    private static final String REPORTS_DIR = "reports/";
       static {
        new File(REPORTS_DIR).mkdirs();
    }
    private static void openPDF(String filePath) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // For Windows
                new ProcessBuilder("cmd", "/c", filePath).start();
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // For MacOS
                new ProcessBuilder("open", filePath).start();
            } else {
                // For Linux/Unix
                new ProcessBuilder("xdg-open", filePath).start();
            }
        } catch (IOException e) {
            System.out.println("Could not open file automatically.");
            System.out.println("Access manually at: " + new File(filePath).getAbsolutePath());
        }
    }

   public static void generateOrderReport(List<Order> orders, LocalDate startDate, LocalDate endDate) throws IOException {
       List<Order> filteredOrders = orders.stream()
               .filter(order -> !order.getCreatedAt().toLocalDate().isBefore(startDate))
               .filter(order -> !order.getCreatedAt().toLocalDate().isAfter(endDate))
               .collect(Collectors.toList());
       if (filteredOrders.isEmpty()) {
           throw new IllegalArgumentException("There are no orders registered in the selected period(" +
                   startDate + " a " + endDate + ")");
       }
       try (PDDocument document = new PDDocument()) {
           PDPage page = new PDPage(PDRectangle.A4);
           document.addPage(page);
           PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
           PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
           try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
               drawOrderPageContent(document, contentStream, fontBold, fontRegular, filteredOrders, startDate, endDate);
           }
           String fileName = REPORTS_DIR + "OrderReport_" + LocalDateTime.now().toString().replace(":", "-") + ".pdf";
           document.save(fileName);
           String filePath = new File(fileName).getAbsolutePath();
           System.out.println("Order report generated on: " + filePath);
           openPDF(filePath);
       }
   }


    private static void drawOrderPageContent(PDDocument document, PDPageContentStream contentStream,
                                             PDType1Font fontBold, PDType1Font fontRegular,
                                             List<Order> filteredOrders, LocalDate startDate, LocalDate endDate)
            throws IOException {
        // Header
        contentStream.setFont(fontBold, 16);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Order Report - TastyBit");
        contentStream.endText();
        contentStream.setFont(fontRegular, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 680);
        contentStream.showText("Period: " + startDate + " a " + endDate);
        contentStream.endText();
        // Order table
        float margin = 100;
        float yStart = 650;
        float rowHeight = 20;
        float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
        float colWidth = tableWidth / 6;
        // Table header
        drawTableHeader(contentStream, margin, yStart, colWidth, rowHeight, fontBold);
        // Table content
        float yPosition = yStart - rowHeight;
        for (Order order : filteredOrders) {
            if (yPosition < 100) {
                contentStream.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                document.addPage(newPage);
                try (PDPageContentStream newContentStream = new PDPageContentStream(document, newPage)) {
                    contentStream = newContentStream;
                    yPosition = 700;
                    drawTableHeader(newContentStream, margin, yPosition, colWidth, rowHeight, fontBold);
                    yPosition -= rowHeight;
                }
            }
            drawOrderRow(contentStream, margin, yPosition, colWidth, rowHeight, order, fontRegular);
            yPosition -= rowHeight;
        }
        // Footer
        contentStream.setFont(fontRegular, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition - 20);
        contentStream.showText("Total orders in the period: " + filteredOrders.size());
        contentStream.endText();
    }

    private static void drawTableHeader(PDPageContentStream contentStream, float x, float y,
                                        float colWidth, float rowHeight, PDType1Font font) throws IOException {
        contentStream.setFont(font, 12);
        // Define custom column widths (total table width remains the same)
        float[] columnWidths = {
                colWidth * 0.8f,   // ID (smaller)
                colWidth * 0.8f,   // Table
                colWidth * 1.2f,   // Employee (wider)
                colWidth * 1.0f,   // Status
                colWidth * 1.0f,   // Total Value
                colWidth * 1.2f    // Date/Time (wider)
        };

        // Draw header cells with new column labels and widths
        float currentX = x;
        drawCell(contentStream, currentX, y, columnWidths[0], rowHeight, "ID", font);
        currentX += columnWidths[0];
        drawCell(contentStream, currentX, y, columnWidths[1], rowHeight, "Table", font);
        currentX += columnWidths[1];
        drawCell(contentStream, currentX, y, columnWidths[2], rowHeight, "Employee", font);
        currentX += columnWidths[2];
        drawCell(contentStream, currentX, y, columnWidths[3], rowHeight, "Status", font);
        currentX += columnWidths[3];
        drawCell(contentStream, currentX, y, columnWidths[4], rowHeight, "Total Value", font);
        currentX += columnWidths[4];
        drawCell(contentStream, currentX, y, columnWidths[5], rowHeight, "Date/Time", font);
    }

  private static void drawOrderRow(PDPageContentStream contentStream, float x, float y,
                                   float colWidth, float rowHeight, Order order, PDType1Font font) throws IOException {
      contentStream.setFont(font, 10);

      // Use same column widths as in header
      float[] columnWidths = {
              colWidth * 0.8f,   // ID
              colWidth * 0.8f,   // Table
              colWidth * 1.2f,   // Employee
              colWidth * 1.0f,   // Status
              colWidth * 1.0f,   // Total Value
              colWidth * 1.2f    // Date/Time
      };

      float currentX = x;
      drawCell(contentStream, currentX, y, columnWidths[0], rowHeight, String.valueOf(order.getId()), font);
      currentX += columnWidths[0];
      drawCell(contentStream, currentX, y, columnWidths[1], rowHeight, String.valueOf(order.getTable().getNumber()), font);
      currentX += columnWidths[1];
      drawCell(contentStream, currentX, y, columnWidths[2], rowHeight, order.getEmployee().getName(), font);
      currentX += columnWidths[2];
      drawCell(contentStream, currentX, y, columnWidths[3], rowHeight, order.getStatus().toString(), font);
      currentX += columnWidths[3];
      drawCell(contentStream, currentX, y, columnWidths[4], rowHeight, String.format("R$ %.2f", order.calculateTotal()), font);
      currentX += columnWidths[4];
      // Format date/time to be more compact
      String formattedDateTime = order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
      drawCell(contentStream, currentX, y, columnWidths[5], rowHeight, formattedDateTime, font);
  }


    private static void drawShiftRow(PDPageContentStream contentStream, float x, float y,
                                     float width, float rowHeight, Shift shift, PDType1Font font) throws IOException {
        contentStream.setFont(font, 8);
        String shiftInfo = shift.getDate() + " - " + shift.getStartTime() + " às " + shift.getEndTime();
        drawCell(contentStream, x, y, width, rowHeight, shiftInfo, font);
    }

    private static void drawCell(PDPageContentStream contentStream, float x, float y,
                                 float width, float height, String text, PDType1Font font) throws IOException {
        contentStream.setLineWidth(0.5f);
        contentStream.addRect(x, y, width, height);
        contentStream.stroke();
        contentStream.beginText();
        contentStream.setFont(font, 10);
        contentStream.newLineAtOffset(x + 2, y + height / 2 - 4);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void generateBookingReport(List<Booking> bookings, LocalDate startDate, LocalDate endDate) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Pass ALL bookings (without filtering by date)
                drawBookingPageContent(document, contentStream, fontBold, fontRegular, bookings, startDate, endDate);
            }
            // Save and open the PDF
            String fileName = REPORTS_DIR + "BookingReport_" + LocalDateTime.now().toString().replace(":", "-") + ".pdf";
            document.save(fileName);
            String filePath = new File(fileName).getAbsolutePath();
            System.out.println("Booking report generated on: " + filePath);
            openPDF(filePath);
        }
    }

    private static void drawBookingPageContent(PDDocument document, PDPageContentStream contentStream,
                                               PDType1Font fontBold, PDType1Font fontRegular,
                                               List<Booking> bookings, LocalDate startDate, LocalDate endDate)
            throws IOException {
        // Header
        contentStream.setFont(fontBold, 16);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Booking Report - TastyBit");
        contentStream.endText();
        contentStream.setFont(fontRegular, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 730);
        contentStream.showText("Period: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " to " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        contentStream.endText();

        // Config table
        float margin = 50;
        float yStart = 700;
        float rowHeight = 20;
        float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
        float colId = tableWidth * 0.10f;       // ID
        float colCustomer = tableWidth * 0.20f; // Customer
        float colTable = tableWidth * 0.10f;    // Table
        float colDateTime = tableWidth * 0.30f; // Date/Time
        float colPeople = tableWidth * 0.10f;   // People
        float colConfirmed = tableWidth * 0.20f;// Confirmed

        // Table header - passing each width individually
        drawBookingTableHeader(contentStream, margin, yStart,
                colId, colCustomer, colTable, colDateTime, colPeople, colConfirmed,
                rowHeight, fontBold);

        // Table content
        float yPosition = yStart - rowHeight;
        for (Booking booking : bookings) {
            if (yPosition < 50) {
                contentStream.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                yPosition = 700;
                drawBookingTableHeader(contentStream, margin, yPosition,
                        colId, colCustomer, colTable, colDateTime, colPeople, colConfirmed,
                        rowHeight, fontBold);
                yPosition -= rowHeight;
            }

            drawBookingRow(contentStream, margin, yPosition,
                    colId, colCustomer, colTable, colDateTime, colPeople, colConfirmed,
                    rowHeight, booking, fontRegular);
            yPosition -= rowHeight;
        }

        // Footer
        contentStream.setFont(fontRegular, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition - 25);
        contentStream.showText("Total bookings: " + bookings.size() +
                " | Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        contentStream.endText();
    }

    private static void drawBookingTableHeader(PDPageContentStream contentStream,
                                               float x, float y,
                                               float colId, float colCustomer, float colTable,
                                               float colDateTime, float colPeople, float colConfirmed,
                                               float rowHeight, PDType1Font font) throws IOException {
        contentStream.setFont(font, 10);
        float currentX = x;
        drawCell(contentStream, currentX, y, colId, rowHeight, "ID", font);
        currentX += colId;
        drawCell(contentStream, currentX, y, colCustomer, rowHeight, "Customer", font);
        currentX += colCustomer;
        drawCell(contentStream, currentX, y, colTable, rowHeight, "Table", font);
        currentX += colTable;
        drawCell(contentStream, currentX, y, colDateTime, rowHeight, "Date/Time", font);
        currentX += colDateTime;
        drawCell(contentStream, currentX, y, colPeople, rowHeight, "People", font);
        currentX += colPeople;
        drawCell(contentStream, currentX, y, colConfirmed, rowHeight, "Confirmed", font);
    }

    private static void drawBookingRow(PDPageContentStream contentStream,
                                       float x, float y,
                                       float colId, float colCustomer, float colTable,
                                       float colDateTime, float colPeople, float colConfirmed,
                                       float rowHeight, Booking booking, PDType1Font font) throws IOException {
        contentStream.setFont(font, 9);

        float currentX = x;

        // ID
        drawCell(contentStream, currentX, y, colId, rowHeight, booking.getId(), font);
        currentX += colId;

        // Customer
        String customerName = booking.getCustomer().getName();
        if (customerName.length() > 15) {
            customerName = customerName.substring(0, 12) + "...";
        }
        drawCell(contentStream, currentX, y, colCustomer, rowHeight, customerName, font);
        currentX += colCustomer;
        // Table
        drawCell(contentStream, currentX, y, colTable, rowHeight,
                String.valueOf(booking.getTable().getNumber()), font);
        currentX += colTable;
        // Date/Time
        String dateTime = booking.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        drawCell(contentStream, currentX, y, colDateTime, rowHeight, dateTime, font);
        currentX += colDateTime;
        // People
        drawCell(contentStream, currentX, y, colPeople, rowHeight,
                String.valueOf(booking.getPartySize()), font);
        currentX += colPeople;
        // Confirmed
        drawCell(contentStream, currentX, y, colConfirmed, rowHeight,
                booking.isConfirmed() ? "Yes" : "No", font);
    }

    public static void generateInventoryReport() throws IOException {
        Inventory inventory = Inventory.getInstance();
        Map<String, InventoryItem> items = inventory.getItems();
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Inventory is empty - no items to generate report");
        }
        // Sort items alphabetically
        List<InventoryItem> sortedItems = items.values().stream()
                .sorted(Comparator.comparing(InventoryItem::getName))
                .collect(Collectors.toList());
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                drawInventoryPageContent(document, contentStream, fontBold, fontRegular, sortedItems);
            }

            // Save the document with timestamp in filename
            String fileName = REPORTS_DIR + "InventoryReport_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            document.save(fileName);

            // Get absolute path and show success message
            String filePath = new File(fileName).getAbsolutePath();
            System.out.println("Inventory report generated at: " + filePath);

            // Open the PDF automatically
            openPDF(filePath);
        }
    }

private static void drawInventoryPageContent(PDDocument document, PDPageContentStream contentStream,
                                             PDType1Font fontBold, PDType1Font fontRegular,
                                             List<InventoryItem> items) throws IOException {
    // Layout settings
    float margin = 50;
    float yStart = 750;
    float rowHeight = 20;
    float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;

    // Column widths
    float colName = tableWidth * 0.40f;
    float colQuantity = tableWidth * 0.15f;
    float colMinStock = tableWidth * 0.15f;
    float colStatus = tableWidth * 0.30f;

    // Header
    contentStream.setFont(fontBold, 16);
    contentStream.beginText();
    contentStream.newLineAtOffset(margin, yStart);
    contentStream.showText("Inventory Report - TastyBit");
    contentStream.endText();
    yStart -= 20;

    contentStream.setFont(fontRegular, 12);
    contentStream.beginText();
    contentStream.newLineAtOffset(margin, yStart);
    contentStream.showText("Generated on: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    contentStream.endText();
    yStart -= 30;

    // Table header
    drawInventoryTableHeader(contentStream, margin, yStart,
            colName, colQuantity, colMinStock, colStatus,
            rowHeight, fontBold);
    float yPosition = yStart - rowHeight;
    for (InventoryItem item : items) {
        if (yPosition < 100) { // New page if needed
            contentStream.close();
            PDPage newPage = new PDPage(PDRectangle.A4);
            document.addPage(newPage);
            contentStream = new PDPageContentStream(document, newPage);
            yPosition = 750;
            drawInventoryTableHeader(contentStream, margin, yPosition,
                    colName, colQuantity, colMinStock, colStatus,
                    rowHeight, fontBold);
            yPosition -= rowHeight;
        }

        // Determine stock status
        String status = item.getQuantity() < item.getMinStock() ?
                "LOW STOCK (Deficit: " + (item.getMinStock() - item.getQuantity()) + ")" :
                "OK";
        drawInventoryRow(contentStream, margin, yPosition,
                colName, colQuantity, colMinStock, colStatus,
                rowHeight, item, status, fontRegular);
        yPosition -= rowHeight;
    }

    // Footer with summary
    long lowStockCount = items.stream()
            .filter(item -> item.getQuantity() < item.getMinStock())
            .count();
    contentStream.setFont(fontRegular, 10);
    contentStream.beginText();
    contentStream.newLineAtOffset(margin, yPosition - 20);
    contentStream.showText("Total items: " + items.size() +
            " | Items with low stock: " + lowStockCount);
    contentStream.endText();
}

    private static void drawInventoryTableHeader(PDPageContentStream contentStream, float x, float y,
                                                 float colName, float colQuantity,
                                                 float colMinStock, float colStatus,
                                                 float rowHeight, PDType1Font font) throws IOException {
        contentStream.setFont(font, 12);
        float currentX = x;
        drawCell(contentStream, currentX, y, colName, rowHeight, "Item Name", font);
        currentX += colName;
        drawCell(contentStream, currentX, y, colQuantity, rowHeight, "Quantity", font);
        currentX += colQuantity;
        drawCell(contentStream, currentX, y, colMinStock, rowHeight, "Min Stock", font);
        currentX += colMinStock;
        drawCell(contentStream, currentX, y, colStatus, rowHeight, "Status", font);
    }

    private static void drawInventoryRow(PDPageContentStream contentStream, float x, float y,
                                         float colName, float colQuantity,
                                         float colMinStock, float colStatus,
                                         float rowHeight, InventoryItem item, String status,
                                         PDType1Font font) throws IOException {
        contentStream.setFont(font, 10);
        float currentX = x;
        // Name
        drawCell(contentStream, currentX, y, colName, rowHeight, item.getName(), font);
        currentX += colName;
        // Quantity
        drawCell(contentStream, currentX, y, colQuantity, rowHeight,
                String.format("%.2f", item.getQuantity()), font);
        currentX += colQuantity;
        // Min Stock
        drawCell(contentStream, currentX, y, colMinStock, rowHeight,
                String.format("%.2f", item.getMinStock()), font);
        currentX += colMinStock;
        // Status (red if low stock)
        if (status.startsWith("LOW")) {
            contentStream.setNonStrokingColor(Color.RED);
        }
        drawCell(contentStream, currentX, y, colStatus, rowHeight, status, font);
        contentStream.setNonStrokingColor(Color.BLACK); // Reset color
    }

}
