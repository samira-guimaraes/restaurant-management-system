import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WorkScheduleTest {
    private Manager manager;
    private LocalDate monday;
    private WorkSchedule schedule;
    @BeforeEach
    void setUp() {
        manager = new Manager(1, "Alice", "alice@example.com", "password123");
        monday = LocalDate.of(2025, 5, 19); // A Monday
        schedule = new WorkSchedule(monday, manager);
    }
    @Test
    void constructor_ShouldThrowException_WhenDateIsNotMonday() {
        LocalDate notMonday = LocalDate.of(2025, 5, 20); // Tuesday
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new WorkSchedule(notMonday, manager);
        });
        assertEquals("Week must start on a Monday", exception.getMessage());
    }
    @Test
    void constructor_ShouldThrowException_WhenManagerIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new WorkSchedule(monday, null);
        });
    }
    @Test
    void addShift_ShouldAddSuccessfully_WhenShiftIsValid() throws InputValidationException {
        Employee employee = new Employee(2, "Samantha", "samantha@example.com", "Samantha123pass", EmployeeRole.CHEF);
        LocalDate date = monday.plusDays(2); // Wednesday
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(16, 0);
        Shift shift = new Shift(employee, date, start, end);
        schedule.addShift(shift);
        List<Shift> shifts = schedule.getShiftsForDate(date);
        assertEquals(1, shifts.size());
        assertEquals(shift, shifts.get(0));
    }
    @Test
    void addShift_ShouldThrowException_WhenDateNotInScheduleWeek() {
        Employee employee = new Employee(3, "Luana", "luana@example.com", "luana123", EmployeeRole.CHEF);
        LocalDate invalidDate = monday.minusDays(1); // Sunday before week start
        Shift shift = new Shift(employee, invalidDate, LocalTime.of(9, 0), LocalTime.of(17, 0));
        Exception exception = assertThrows(InputValidationException.class, () -> schedule.addShift(shift));
        assertEquals("Date is not in the scheduled week", exception.getMessage());
    }
    @Test
    void addShift_ShouldThrowException_WhenShiftOverlaps() throws InputValidationException {
        Employee employee = new Employee(4, "Romulo", "romulo@example.com", "Romulo1234", EmployeeRole.CHEF);
        LocalDate date = monday.plusDays(1);
        Shift shift1 = new Shift(employee, date, LocalTime.of(8, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(employee, date, LocalTime.of(11, 0), LocalTime.of(14, 0)); // Overlaps

        schedule.addShift(shift1);
        Exception exception = assertThrows(InputValidationException.class, () -> schedule.addShift(shift2));
        assertEquals("Shift conflicts with existing schedule", exception.getMessage());
    }
    @Test
    void addShift_ShouldThrowException_WhenShiftExceeds12Hours() {
        Employee employee = new Employee(5, "Cammy", "cammy@example.com", "Cammy456", EmployeeRole.CHEF);
        LocalDate date = monday.plusDays(4);
        Shift longShift = new Shift(employee, date, LocalTime.of(7, 0), LocalTime.of(21, 30)); // >12h

        Exception exception = assertThrows(InputValidationException.class, () -> schedule.addShift(longShift));
        assertEquals("Shift cannot exceed 12 hours", exception.getMessage());
    }
    @Test
    void getShiftsForEmployee_ShouldReturnOnlyRelevantShifts() throws InputValidationException {
        Employee employee1 = new Employee(6, "Bernardo", "bernardo@example.com", "Bernardopass6", EmployeeRole.WAITER);
        Employee employee2 = new Employee(7, "Enrico", "enrico@example.com", "Enricopass7", EmployeeRole.WAITER);
        LocalDate wednesday = monday.plusDays(2);
        schedule.addShift(new Shift(employee1, wednesday, LocalTime.of(9, 0), LocalTime.of(12, 0)));
        schedule.addShift(new Shift(employee2, wednesday, LocalTime.of(14, 0), LocalTime.of(18, 0)));
        List<Shift> result = schedule.getShiftsForEmployee(employee1, monday, monday.plusDays(6));
        assertEquals(1, result.size());
        assertEquals(employee1, result.get(0).getEmployee());
    }
}
