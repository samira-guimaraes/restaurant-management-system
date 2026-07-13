import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    private Booking booking;
    private Customer customer;
    private Table table;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        // Creating a customer, a table, and a time for the tests
        customer = new Customer("John Doe", "123456789", "john@example.com");
        table = new Table(1, 4);
        dateTime = LocalDateTime.of(2025, 5, 24, 18, 0);

        // Creating the booking for the customer
        booking = new Booking(customer, table, dateTime, 3);
    }

    @Test
    void testBookingInitialization() {
        // Tests if the booking was correctly created
        assertNotNull(booking.getId(), "The booking ID should not be null.");
        assertEquals(customer, booking.getCustomer(), "The customer in the booking is incorrect.");
        assertEquals(table, booking.getTable(), "The table in the booking is incorrect.");
        assertEquals(dateTime, booking.getDateTime(), "The time in the booking is incorrect.");
        assertEquals(3, booking.getPartySize(), "The party size in the booking is incorrect.");
        assertFalse(booking.isConfirmed(), "The booking should not be confirmed.");
    }

    @Test
    void testConfirmBooking() {
        // Tests the booking confirmation
        booking.confirm();
        assertTrue(booking.isConfirmed(), "The booking should be confirmed.");
    }

    @Test
    void testDoubleConfirmationThrowsException() {
        // Tests that confirming the booking twice throws an exception
        booking.confirm();
        assertThrows(IllegalStateException.class, booking::confirm, "It should throw an exception when trying to confirm again.");
    }

    @Test
    void testSetPartySizeValid() {
        // Tests if the party size can be changed to a valid value
        booking.setPartySize(2);
        assertEquals(2, booking.getPartySize(), "The party size should be 2.");
    }

    @Test
    void testSetPartySizeInvalid() {
        // Tests that setting an invalid party size throws an exception
        assertThrows(IllegalArgumentException.class, () -> booking.setPartySize(0), "It should throw an exception for invalid party size.");
    }

    @Test
    void testSetTableNotNull() {
        // Tests if the table can be changed to a new table
        Table newTable = new Table(2, 6);
        booking.setTable(newTable);
        assertEquals(newTable, booking.getTable(), "The table in the booking was not changed correctly.");
    }

    @Test
    void testSetDateTime() {
        // Tests if the date and time of the booking can be changed
        LocalDateTime newDateTime = LocalDateTime.of(2025, 6, 1, 19, 0);
        booking.setDateTime(newDateTime);
        assertEquals(newDateTime, booking.getDateTime(), "The date and time of the booking were not updated correctly.");
    }

    @Test
    void testFilterBookingsByDate() {
        // Tests if the filtering of bookings by date is working correctly
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking);

        // Creating another booking to test the filter
        Booking anotherBooking = new Booking(customer, table,
                LocalDateTime.of(2025, 5, 25, 19, 0), 2);
        allBookings.add(anotherBooking);

        // Filtering bookings on May 24, 2025
        List<Booking> result = Booking.filterBookingsByDate(allBookings,
                LocalDate.of(2025, 5, 24),
                LocalDate.of(2025, 5, 24));

        assertEquals(1, result.size(), "It should return 1 booking for May 24.");
        assertEquals(booking, result.get(0), "The returned booking is not the expected one.");
    }

    @Test
    void testFilterBookingsByDateInvalidRange() {
        // Tests that an exception is thrown when trying to filter with an invalid date range
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking);

        assertThrows(IllegalArgumentException.class, () ->
                        Booking.filterBookingsByDate(allBookings,
                                LocalDate.of(2025, 5, 25),
                                LocalDate.of(2025, 5, 24)),
                "It should throw an exception when the end date is before the start date.");
    }
}
