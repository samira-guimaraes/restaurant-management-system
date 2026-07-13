import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a work shift assigned to an employee, including the date, start time, and end time.
 * Instances of this class are immutable and must be created with valid parameters.
 */

public class Shift {
    private final Employee employee;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    /**
     * Constructs a new Shift object representing a work shift assigned to an employee.
     *
     * @param employee the employee assigned to this shift; must not be null
     * @param date the date of the shift; must not be null
     * @param startTime the start time of the shift; must not be null and should precede the end time
     * @param endTime the end time of the shift; must not be null and should follow the start time
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if the start time is after the end time
     */
    public Shift(Employee employee, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.employee = Objects.requireNonNull(employee, "Employee cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    /**
     * Determines if this shift overlaps with another shift.
     * Two shifts overlap if they occur on the same date and their time ranges intersect.
     *
     * @param other the other Shift to check for overlap; must not be null
     * @return true if the shifts overlap, false otherwise
     */
    public boolean overlapsWith(Shift other) {
        if (!this.date.equals(other.getDate())) {
            return false;
        }
        return this.startTime.isBefore(other.getEndTime()) &&
                this.endTime.isAfter(other.getStartTime());
    }
}