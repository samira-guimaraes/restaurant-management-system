import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a booking for a customer at a specific table and time.
 * This class allows managing customer bookings, assigning tables,
 * and handling party size constraints. Bookings can also be updated
 * or confirmed as required.
 */
public class Booking {
    private final String id;
    private Customer customer;
    private Table table;
    private LocalDateTime dateTime;
    private int partySize;
    private boolean confirmed;
    private static int nextId = 1;
    private static final Scanner scanner = new Scanner(System.in);

    // Getters
    public String getId() {
        return id;
    }
    public Customer getCustomer() {
        return customer;
    }
    public Table getTable() {
        return table;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public int getPartySize() {
        return partySize;
    }

    // Setters
    public void setTable(Table table) {
        this.table = Objects.requireNonNull(table);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void confirm() throws IllegalStateException {
        if (this.confirmed) {
            throw new IllegalStateException("Booking already confirmed!");
        }
        this.confirmed = true;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = Objects.requireNonNull(dateTime);
    }
    public void setPartySize(int partySize) {
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        this.partySize = partySize;
    }
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Constructs a new Booking object with the specified details for the customer, table,
     * booking date and time, and party size.
     *
     * @param customer the customer associated with the booking; must not be null
     * @param table the table assigned for the booking; must not be null
     * @param dateTime the date and time of the booking; must not be null
     * @param partySize the number of people in the party for the booking; must be greater than 0
     * @throws NullPointerException if any of the {@code customer}, {@code table}, or {@code dateTime}
     *         parameters are null
     * @throws IllegalArgumentException if the {@code partySize} is less than or equal to 0
     */
    public Booking(Customer customer, Table table, LocalDateTime dateTime, int partySize) {
        this.id = String.valueOf(nextId++);
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.dateTime = Objects.requireNonNull(dateTime, "DateTime cannot be null");

        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        this.partySize = partySize;
        this.confirmed = false;
    }

    /**
     * Provides management functionality for handling restaurant bookings through a menu-based interface.
     * Allows users to create, view, confirm, and edit bookings or return to the main menu.
     * The method runs continuously in a loop until the user selects the option to go back to the main menu.
     *
     * @param tables the list of available {@code Table} objects that can be used for bookings
     * @param bookings the list of existing {@code Booking} objects to manage
     *                 (e.g., adding, viewing, confirming)
     * @param scanner the {@code Scanner} instance to obtain user input for menu navigation
     */
    public static void bookingManagement(List<Table> tables, List<Booking> bookings, Scanner scanner) {
        while (true) {
            System.out.println("\n=== BOOKING MANAGEMENT ===");
            System.out.println("1. Create Booking");
            System.out.println("2. View Bookings");
            System.out.println("3. Confirm Booking");
            System.out.println("4. Edit Booking");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> createBooking(tables, bookings);
                case 2 -> viewBookings(bookings);
                case 3 -> confirmBooking(bookings);
                case 4 -> editBooking(tables, bookings);
                case 5 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Creates a new booking for a customer, assigning a table and specifying the booking
     * details such as date, time, and party size. Validates the input to ensure the table
     * selected is available and has sufficient capacity. Checks for any booking conflicts
     * with the specified table and time.
     *
     * @param tables the list of available {@code Table} objects that customers can book
     * @param bookings the list of existing {@code Booking} objects used to validate conflicts
     */
    private static void createBooking(List<Table> tables, List<Booking> bookings) {
        System.out.println("\nCreate New Booking");
        showAvailableTables(tables);
        System.out.print("Select table number: ");
        int tableNumber = scanner.nextInt();
        scanner.nextLine();

        Table selectedTable = tables.stream()
                .filter(t -> t.getNumber() == tableNumber)
                .findFirst()
                .orElse(null);
        if (selectedTable == null) {
            System.out.println("Invalid table!");
            return;
        }
        System.out.print("Customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Customer phone: ");
        String customerPhone = scanner.nextLine();
        System.out.print("Number of people: ");
        int partySize = scanner.nextInt();
        scanner.nextLine();
        if (partySize > selectedTable.getCapacity()) {
            System.out.println("Party size exceeds table capacity!");
            return;
        }
        System.out.print("Booking date (yyyy-mm-dd): ");
        String dateStr = scanner.nextLine();
        System.out.print("Booking time (hh:mm): ");
        String timeStr = scanner.nextLine();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            if (hasBookingConflict(selectedTable, dateTime, bookings)) {
                System.out.println("Error: There's already a booking for this table at the selected time!");
                return;
            }
            Customer customer = new Customer(customerName, customerPhone, "");
            Booking newBooking = new Booking(customer, selectedTable, dateTime, partySize);
            bookings.add(newBooking);
            System.out.println("Booking created successfully! ID: " + newBooking.getId());
        } catch (Exception e) {
            System.out.println("Error creating booking: " + e.getMessage());
        }
    }

    /**
     * Allows editing of an existing booking by providing options to update the table, date/time,
     * and party size. Ensures the new details do not conflict with existing bookings and maintains
     * data validity constraints (e.g., table capacity, date/time format). The booking's confirmation
     * status is reset after editing.
     *
     * @param tables the list of available {@code Table} objects that can be assigned to bookings
     * @param bookings the list of existing {@code Booking} objects to be edited or checked for conflicts
     */
    private static void editBooking(List<Table> tables, List<Booking> bookings) {
        viewBookings(bookings);
        if (bookings.isEmpty()) {
            return;
        }
        System.out.print("\nEnter booking ID to edit: ");
        String bookingId = scanner.nextLine();
        Booking booking = null;
        for (Booking b : bookings) {
            if (b.getId().equals(bookingId)) {
                booking = b;
                break;
            }
        }
        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }
        System.out.println("\nEditing Booking ID: " + booking.getId());
        System.out.println("Note: Leave field blank to keep current value.");
        System.out.println("\nCurrent Table: " + booking.getTable().getNumber());
        showAvailableTables(tables);
        System.out.print("New Table Number (0 to keep current): ");
        int tableNumber = 0;
        try {
            tableNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid table number! Keeping current table.");
        }
        if (tableNumber > 0) {
            Table newTable = null;
            for (Table t : tables) {
                if (t.getNumber() == tableNumber) {
                    newTable = t;
                    break;
                }
            }
            if (newTable != null) {
                booking.setTable(newTable);
            } else {
                System.out.println("Invalid table! Keeping current table.");
            }
        }
        System.out.println("\nCurrent Date/Time: " + booking.getDateTime());
        System.out.print("New Date (yyyy-mm-dd or blank to keep): ");
        String dateStr = scanner.nextLine();
        System.out.print("New Time (hh:mm or blank to keep): ");
        String timeStr = scanner.nextLine();
        if (!dateStr.isBlank() || !timeStr.isBlank()) {
            try {
                LocalDate date = dateStr.isBlank() ? booking.getDateTime().toLocalDate() : LocalDate.parse(dateStr);
                LocalTime time = timeStr.isBlank() ? booking.getDateTime().toLocalTime() : LocalTime.parse(timeStr);
                LocalDateTime newDateTime = LocalDateTime.of(date, time);

                if (!newDateTime.equals(booking.getDateTime())) {
                    if (hasBookingConflict(booking.getTable(), newDateTime, bookings)) {
                        System.out.println("Error: There's already a booking for this table at the selected time!");
                        return;
                    }
                }
                booking.setDateTime(newDateTime);
            } catch (Exception e) {
                System.out.println("Invalid date/time format! Keeping current value.");
            }
        }
        System.out.println("\nCurrent Party Size: " + booking.getPartySize());
        System.out.print("New Party Size (0 to keep current): ");
        int partySize = 0;
        try {
            partySize = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number! Keeping current party size.");
        }
        if (partySize > 0) {
            if (partySize > booking.getTable().getCapacity()) {
                System.out.println("Party size exceeds table capacity! Keeping current value.");
            } else {
                booking.setPartySize(partySize);
            }
        }
        booking.setConfirmed(false);
        System.out.println("\nBooking updated successfully!");
    }

    /**
     * Checks if there is a booking conflict for a given table and date/time with the existing list of bookings.
     *
     * @param table the {@code Table} object to check for a potential conflict
     * @param dateTime the {@code LocalDateTime} of the desired booking to check for conflicts
     * @param bookings the list of existing {@code Booking} objects to check against
     * @return {@code true} if a booking conflict exists for the specified table and date/time, {@code false} otherwise
     */
    private static boolean hasBookingConflict(Table table, LocalDateTime dateTime, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.getTable().equals(table) && booking.getDateTime().equals(dateTime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Displays a list of available tables with their details, including table number
     * and seating capacity. If no tables are available, a message indicating this is shown.
     *
     * @param tables the list of {@code Table} objects to be checked and displayed; must not be null
     */
    private static void showAvailableTables(List<Table> tables) {
        if (tables.isEmpty()) {
            System.out.println("\nNo tables available!");
            return;
        }
        System.out.println("\n=== AVAILABLE TABLES ===");
        System.out.printf("%-10s %-10s\n", "Number", "Capacity");
        for (Table table : tables) {
            if (table.isAvailable()) {
                System.out.printf("%-10d %-10d\n", table.getNumber(), table.getCapacity());
            }
        }
    }


    /**
     * Displays a list of existing bookings with their details, including ID, customer name, table number,
     * booking date and time, party size, and confirmation status. If no bookings are available, a message
     * indicating no bookings are found will be displayed.
     *
     * @param bookings the list of {@code Booking} objects to be displayed; must not be null
     */
    private static void viewBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found!");
            return;
        }
        System.out.println("\n=== BOOKINGS ===");
        System.out.printf("%-10s %-15s %-10s %-20s %-10s %-10s\n",
                "ID", "Customer", "Table", "Date/Time", "Party", "Confirmed");
        bookings.forEach(booking ->
                System.out.printf("%-10s %-15s %-10d %-20s %-10d %-10s\n",
                        booking.getId(),
                        booking.getCustomer().getName(),
                        booking.getTable().getNumber(),
                        booking.getDateTime().toString().replace("T", " "),
                        booking.getPartySize(),
                        booking.isConfirmed() ? "Yes" : "No"));
    }

    /**
     * Confirms a booking from the provided list of bookings by selecting a booking based on its ID.
     * The method first displays all current bookings, then prompts the user for a booking ID. If the specified
     * booking is found, it attempts to mark the booking as confirmed, handling potential errors if the
     * booking is already confirmed.
     *
     * @param bookings the list of {@code Booking} objects from which a booking is selected and confirmed;
     *                 must not be null
     */
    private static void confirmBooking(List<Booking> bookings) {
        viewBookings(bookings);
        System.out.print("Enter booking ID to confirm: ");
        String bookingId = scanner.nextLine();
        Booking booking = null;
        for (Booking b : bookings) {
            if (b.getId().equals(bookingId)) {
                booking = b;
                break;
            }
        }
        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }
        try {
            booking.confirm();
            System.out.println("Booking confirmed successfully!");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     *
     * @param bookings
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Booking> filterBookingsByDate(List<Booking> bookings, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        List<Booking> filteredBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            LocalDate bookingDate = booking.getDateTime().toLocalDate();
            if (!bookingDate.isBefore(startDate) && !bookingDate.isAfter(endDate)) {
                filteredBookings.add(booking);
            }
        }
        return filteredBookings;
    }
}
