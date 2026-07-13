import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a weekly work schedule, managing shifts for employees within a specified
 * week that starts on a Monday. This schedule is created by a manager and contains
 * daily shifts for each day of the week.
 */
public class WorkSchedule {
    private final UUID id;
    private final LocalDate weekStartDate;
    private final Map<LocalDate, List<Shift>> dailyShifts;
    private final Manager createdBy;


    // Getters
    public LocalDate getWeekStartDate() { return weekStartDate; }

    public WorkSchedule(LocalDate weekStartDate, Manager createdBy) {
        this.id = UUID.randomUUID();
        this.weekStartDate = validateWeekStartDate(weekStartDate);
        this.createdBy = Objects.requireNonNull(createdBy, "Manager cannot be null");
        this.dailyShifts = initializeEmptyShifts(weekStartDate);
    }

    /**
     * Validates that the given date is a valid week start date, ensuring it is not null and falls on a Monday.
     *
     * @param date the local date to be validated as the start of the week.
     *             Must not be null; otherwise, a NullPointerException will be thrown.
     *             If the date does not fall on a Monday, an IllegalArgumentException will be thrown.
     * @return the validated date if it meets the required conditions.
     * @throws NullPointerException if the provided date is null.
     * @throws IllegalArgumentException if the provided date is not a Monday.
     */
    private LocalDate validateWeekStartDate(LocalDate date) {
        Objects.requireNonNull(date, "Week start date cannot be null");
        if (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException("Week must start on a Monday");
        }
        return date;
    }

    /**
     * Initializes a map representing an empty work schedule for a week, starting from the given date.
     * The map contains seven entries, one for each day of the week, with each date mapped to an empty list of shifts.
     *
     * @param weekStart the starting date of the week for which the shifts are to be initialized.
     *                  This date is expected to be a valid week start, typically a Monday.
     *                  Cannot be null; otherwise, a NullPointerException may occur.
     * @return a map where each key is a date within the specified week and the value is an empty list of shifts for that date.
     */
    private Map<LocalDate, List<Shift>> initializeEmptyShifts(LocalDate weekStart) {
        Map<LocalDate, List<Shift>> shifts = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            shifts.put(weekStart.plusDays(i), new ArrayList<>());
        }
        return shifts;
    }

    /**
     * Manages the scheduling process for employees, including creating work schedules, assigning shifts,
     * and viewing employee and weekly schedules. This method is accessible only to users with
     * the 'MANAGE_SCHEDULE' permission.
     *
     * @param currentUser the employee currently using the system, whose permissions are verified
     *                    before allowing access to the scheduling features. Cannot be null.
     * @param employees   the list of all employees available for scheduling and assignment operations.
     *                    Cannot be null but may be empty.
     * @param schedules   the list of current work schedules that can be managed or viewed.
     *                    Cannot be null but may be empty.
     * @param scanner     the scanner object used to capture user input for menu navigation and data entry.
     *                    Cannot be null.
     */
    public static void scheduleManagement(Employee currentUser, List<Employee> employees,
                                          List<WorkSchedule> schedules, Scanner scanner) {
        if (!currentUser.hasPermission(PermissionFeature.MANAGE_SCHEDULE)) {
            System.out.println("Access denied! Only managers can manage schedules.");
            return;
        }
        while (true) {
            System.out.println("\n=== SCHEDULE MANAGEMENT ===");
            System.out.println("1. Create Work Schedule");
            System.out.println("2. Assign Shift");
            System.out.println("3. View Employee Schedule");
            System.out.println("4. View Weekly Schedule");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> createWorkSchedule(currentUser, schedules, scanner);
                case 2 -> assignShift(employees, schedules, scanner);
                case 3 -> viewEmployeeSchedule(employees, schedules, scanner);
                case 4 -> viewWeeklySchedule(schedules, scanner);
                case 5 -> { return; }
                default -> System.out.println("Invalid option! Try again");
            }
        }
    }

    /**
     * Creates a new work schedule for a specific week, starting from the date provided by the user.
     * The method is accessible only to users who are managers. The created schedule is added to the
     * list of existing schedules.
     *
     * @param currentUser the employee currently logged in and attempting to create a work schedule.
     *                    It must be an instance of Manager; otherwise, an IllegalStateException
     *                    will be thrown. Cannot be null.
     * @param schedules   the list of existing work schedules. The newly created schedule will be
     *                    added to this list. Cannot be null but may be empty.
     * @param scanner     the scanner object used to capture user input, including the week start
     *                    date. Cannot be null.
     */
    private static void createWorkSchedule(Employee currentUser, List<WorkSchedule> schedules, Scanner scanner) {
        System.out.println("\nCreate Work Schedule");
        System.out.print("Week start date (yyyy-mm-dd, must be a Monday): ");
        String dateStr = scanner.nextLine();
        try {
            LocalDate weekStart = LocalDate.parse(dateStr);

            if (!(currentUser instanceof Manager)) {
                throw new IllegalStateException("Only managers can create schedules");
            }
            WorkSchedule schedule = new WorkSchedule(weekStart, (Manager)currentUser);
            schedules.add(schedule);
            System.out.println("Schedule created for week starting " + weekStart);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Assigns a shift to an employee for a specific date and time within a given work schedule.
     * This method interacts with the user via a {@code Scanner} to gather input such as
     * employee ID, shift date, and start/end times, and updates the specified schedule accordingly.
     * Error handling is implemented to ensure proper input and provide feedback for invalid entries.
     *
     * @param employees the list of employees available for shift assignment. Cannot be null.
     * @param schedules the list of work schedules available for assigning shifts. Cannot be null but may be empty.
     *                   If empty, the method will notify the user and return without assigning a shift.
     * @param scanner   the scanner object used to read user inputs such as schedule date,
     *                  employee ID, shift date, and times. Cannot be null.
     */
    private static void assignShift(List<Employee> employees, List<WorkSchedule> schedules, Scanner scanner) {
        if (schedules.isEmpty()) {
            System.out.println("\n[ERROR] No schedules available. Please create a schedule first.");
            return;
        }
        try {
            // Show available shift
            System.out.println("\nAvailable Schedules:");
            schedules.forEach(s -> System.out.println("- Week starting: " + s.getWeekStartDate()));

            System.out.print("\nEnter week start date (yyyy-mm-dd): ");
            LocalDate weekStart = LocalDate.parse(scanner.nextLine().trim());
            WorkSchedule schedule = findSchedule(schedules, weekStart);
            if (schedule == null) {
                System.out.println("\n[ERROR] No schedule found for week starting " + weekStart);
                return;
            }
            System.out.println("\nAvailable Employees:");
            viewEmployees(employees);

            // Request Employee ID
            System.out.print("\nEnter employee ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("[ERROR] Please enter a valid employee ID (number)");
                scanner.next(); // descarta entrada inválida
            }
            int employeeId = scanner.nextInt();
            scanner.nextLine(); // consume newline

            Employee employee = findEmployee(employees, employeeId);
            if (employee == null) {
                System.out.println("\n[ERROR] Employee with ID " + employeeId + " not found");
                return;
            }

            // Request shift date
            System.out.print("\nEnter shift date (yyyy-mm-dd, must be between " +
                    weekStart + " and " + weekStart.plusDays(6) + "): ");
            LocalDate shiftDate = LocalDate.parse(scanner.nextLine().trim());

            // Check if the date is within the week of the shift
            if (shiftDate.isBefore(weekStart) || shiftDate.isAfter(weekStart.plusDays(6))) {
                System.out.println("\n[ERROR] Date must be within the selected week");
                return;
            }
            // Request start time
            System.out.print("Enter START time (hh:mm): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine().trim());

            // Request end time
            System.out.print("----Enter END time (hh:mm): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine().trim());

            // Create and add shift
            Shift shift = new Shift(employee, shiftDate, startTime, endTime);
            schedule.addShift(shift);
            System.out.println("\n[SUCCESS] Shift assigned successfully for " +
                    employee.getName() + " on " + shiftDate);

        } catch (DateTimeParseException e) {
            System.out.println("\n[ERROR] Invalid date/time format. Please use yyyy-mm-dd for dates and hh:mm for times");
        } catch (InputValidationException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Displays the schedule for a specified employee within a given date range. The method allows
     * the user to select an employee and view their assigned shifts based on the provided schedules.
     * Errors in input or data retrieval, such as invalid dates or a non-existent employee ID, are
     * handled gracefully with corresponding notifications.
     *
     * @param employees the list of employees from which a specific employee can be selected.
     *                  Cannot be null but may be empty. If empty, no employee can be selected.
     * @param schedules the list of work schedules containing shift assignments. Cannot be null
     *                  but may be empty. If empty, no schedules will be available to view.
     * @param scanner   the scanner object used to capture user input for specifying the employee ID
     *                  and date range. Cannot be null.
     */
    private static void viewEmployeeSchedule(List<Employee> employees, List<WorkSchedule> schedules, Scanner scanner) {
        viewEmployees(employees);
        System.out.print("Enter employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();
        Employee employee = employees.stream()
                .filter(e -> e.getId() == employeeId)
                .findFirst()
                .orElse(null);
        if (employee == null) {
            System.out.println("Employee not found!");
            return;
        }
        System.out.print("Enter start date (yyyy-mm-dd): ");
        String startStr = scanner.nextLine();
        System.out.print("Enter end date (yyyy-mm-dd): ");
        String endStr = scanner.nextLine();
        try {
            LocalDate startDate = LocalDate.parse(startStr);
            LocalDate endDate = LocalDate.parse(endStr);
            System.out.println("\n=== SCHEDULE FOR " + employee.getName().toUpperCase() + " ===");
            System.out.printf("%-15s %-15s %-15s %-15s\n", "Date", "Day", "Start", "End");

            List<Shift> employeeShifts = schedules.stream()
                    .flatMap(s -> s.getShiftsForEmployee(employee, startDate, endDate).stream())
                    .sorted(Comparator.comparing(Shift::getDate))
                    .toList();
            for (Shift shift : employeeShifts) {
                System.out.printf("%-15s %-15s %-15s %-15s\n",
                        shift.getDate(),
                        shift.getDate().getDayOfWeek(),
                        shift.getStartTime(),
                        shift.getEndTime());
            }
        } catch (Exception e) {
            System.out.println("Error viewing schedule: " + e.getMessage());
        }
    }

    /**
     * Displays the weekly schedule for a selected week. This method prompts the user to choose a
     * week starting date from the available schedules and then displays the detailed schedule for
     * that week, including employee shifts for each day.
     *
     * @param schedules the list of work schedules containing shift details for various weeks.
     *                  Cannot be null, but may be empty. If empty, the method notifies the user
     *                  and terminates without displaying any schedule details.
     * @param scanner   the scanner object used to capture user input, including the selection of a
     *                  week start date. Cannot be null.
     */
    private static void viewWeeklySchedule(List<WorkSchedule> schedules, Scanner scanner) {
        if (schedules.isEmpty()) {
            System.out.println("No schedules available.");
            return;
        }
        System.out.println("\nAvailable Schedules:");
        schedules.forEach(s -> System.out.println("Week starting: " + s.getWeekStartDate()));
        System.out.print("Select week start date (yyyy-mm-dd): ");
        LocalDate weekStart = LocalDate.parse(scanner.nextLine());

        WorkSchedule schedule = findSchedule(schedules, weekStart);
        if (schedule == null) {
            System.out.println("Schedule not found!");
            return;
        }
        System.out.println("\n=== WEEKLY SCHEDULE (" + weekStart + " to " + weekStart.plusDays(6) + ") ===");

        for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {
            System.out.println("\n" + date.getDayOfWeek() + " (" + date + ")");
            System.out.printf("%-20s %-15s %-15s\n", "Employee", "Start", "End");
            List<Shift> shifts = schedule.getShiftsForDate(date);
            if (shifts.isEmpty()) {
                System.out.println("No shifts scheduled");
            } else {
                shifts.stream()
                        .sorted(Comparator.comparing(Shift::getStartTime))
                        .forEach(shift -> System.out.printf("%-20s %-15s %-15s\n",
                                shift.getEmployee().getName(),
                                shift.getStartTime(),
                                shift.getEndTime()));
            }
        }
    }

    // Auxiliary methods
    /**
     * Finds and returns a work schedule from the provided list that matches the given week start date.
     * If no schedule is found with the specified week start date, the method returns null.
     *
     * @param schedules the list of work schedules to search through. Cannot be null but may be empty.
     *                  If empty, the method will return null.
     * @param weekStart the start date of the week to match against the schedules. Must not be null.
     * @return the work schedule that matches the specified week start date, or null if no match is found.
     */
    private static WorkSchedule findSchedule(List<WorkSchedule> schedules, LocalDate weekStart) {
        return schedules.stream()
                .filter(s -> s != null && s.getWeekStartDate().equals(weekStart))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds and returns an employee from the provided list that matches the specified ID.
     * If no employee is found with the given ID, the method returns null.
     *
     * @param employees the list of employees to search through. Cannot be null but may be empty.
     *                  If empty, the method will return null.
     * @param id the unique identifier of the employee being searched for.
     * @return the employee object with the matching ID, or null if no match is found.
     */
    private static Employee findEmployee(List<Employee> employees, int id) {
        return employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Displays a list of employees along with their IDs, names, and roles in a formatted table.
     *
     * @param employees the list of employees to be displayed.
     *                  Cannot be null but may be empty. If empty, no employees will be displayed.
     */
    private static void viewEmployees(List<Employee> employees) {
        System.out.println("\n=== EMPLOYEES ===");
        System.out.printf("%-5s %-20s %-15s\n", "ID", "Name", "Role");
        employees.forEach(e -> System.out.printf("%-5d %-20s %-15s\n",
                e.getId(), e.getName(), e.getRole()));
    }

    /**
     * Adds a shift to the work schedule for the specified date.
     * The shift must be validated and the date must fall within the scheduled week.
     *
     * @param shift the shift to be added. Must not be null. Throws InputValidationException
     *              if the shift is invalid or if the date is not within the scheduled week.
     * @throws InputValidationException if the shift is invalid or the date is not in the scheduled week.
     */
    // Shift manipulation methods
    public void addShift(Shift shift) throws InputValidationException {
        Objects.requireNonNull(shift, "Shift cannot be null");
        LocalDate date = shift.getDate();
        if (!dailyShifts.containsKey(date)) {
            System.out.printf("Date is not in the scheduled week", "date");
        }
        validateShift(shift);
        dailyShifts.get(date).add(shift);
    }

    private void validateShift(Shift shift) throws InputValidationException {
        LocalDate date = shift.getDate();
        // Check for schedule conflicts
        boolean hasConflict = dailyShifts.get(date).stream()
                .anyMatch(existingShift -> existingShift.overlapsWith(shift));
        if (hasConflict) {
            throw new InputValidationException("Shift conflicts with existing schedule", "time");
        }
        // Check maximum duration
        if (Duration.between(shift.getStartTime(), shift.getEndTime()).toHours() > 12) {
            throw new InputValidationException("Shift cannot exceed 12 hours", "duration");
        }
    }

    public List<Shift> getShiftsForEmployee(Employee employee, LocalDate startDate, LocalDate endDate) {
        return dailyShifts.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                .flatMap(entry -> entry.getValue().stream())
                .filter(shift -> shift.getEmployee().equals(employee))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of shifts scheduled for the specified date.
     *
     * @param date the date for which to retrieve the shifts
     * @return a list of shifts scheduled for the specified date, or an empty list if no shifts are found
     */
    public List<Shift> getShiftsForDate(LocalDate date) {
        return dailyShifts.getOrDefault(date, Collections.emptyList());
    }
}