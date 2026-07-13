import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class ShiftTest {
    private Employee employee;
    @BeforeEach
    public void setup() {
        employee = new Employee(1, "Sara Duarte", "sara@example.com", "password", EmployeeRole.WAITER);
    }
    @Test
    public void constructor_ShouldCreateShift_WhenValidArguments() {
        Shift shift = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(9, 0), LocalTime.of(17, 0));
        assertEquals(employee, shift.getEmployee());
        assertEquals(LocalDate.of(2024, 5, 24), shift.getDate());
        assertEquals(LocalTime.of(9, 0), shift.getStartTime());
        assertEquals(LocalTime.of(17, 0), shift.getEndTime());
    }
    @Test
    public void constructor_ShouldThrowException_WhenStartTimeIsAfterEndTime() {
        assertThrows(IllegalArgumentException.class, () ->
                new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(18, 0), LocalTime.of(10, 0)));
    }
    @Test
    public void overlapsWith_ShouldReturnTrue_WhenShiftsOverlap() {
        Shift shift1 = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(9, 0), LocalTime.of(17, 0));
        Shift shift2 = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(16, 0), LocalTime.of(20, 0));
        assertTrue(shift1.overlapsWith(shift2));
    }
    @Test
    public void overlapsWith_ShouldReturnFalse_WhenShiftsDoNotOverlap() {
        Shift shift1 = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(9, 0), LocalTime.of(12, 0));
        Shift shift2 = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(13, 0), LocalTime.of(17, 0));

        assertFalse(shift1.overlapsWith(shift2));
    }

    @Test
    public void overlapsWith_ShouldReturnFalse_WhenDifferentDates() {
        Shift shift1 = new Shift(employee, LocalDate.of(2024, 5, 24), LocalTime.of(9, 0), LocalTime.of(17, 0));
        Shift shift2 = new Shift(employee, LocalDate.of(2024, 5, 25), LocalTime.of(9, 0), LocalTime.of(17, 0));
        assertFalse(shift1.overlapsWith(shift2));
    }
}
