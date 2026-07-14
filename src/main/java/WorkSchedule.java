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

    private LocalDate validateWeekStartDate(LocalDate date) {
        Objects.requireNonNull(date, "Week start date cannot be null");
        if (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException("Week must start on a Monday");
        }
        return date;
    }

    private Map<LocalDate, List<Shift>> initializeEmptyShifts(LocalDate weekStart) {
        Map<LocalDate, List<Shift>> shifts = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            shifts.put(weekStart.plusDays(i), new ArrayList<>());
        }
        return shifts;
    }

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

    private static void assignShift(List<Employee> employees, List<WorkSchedule> schedules, Scanner scanner) {
        if (schedules.isEmpty()) {
            System.out.println("\n[ERROR] No schedules available. Please create a schedule first.");
            return;
        }
        try {
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

            System.out.print("\nEnter employee ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("[ERROR] Please enter a valid employee ID (number)");
                scanner.next();
            }
            int employeeId = scanner.nextInt();
            scanner.nextLine();

            Employee employee = findEmployee(employees, employeeId);
            if (employee == null) {
                System.out.println("\n[ERROR] Employee with ID " + employeeId + " not found");
                return;
            }

            System.out.print("\nEnter shift date (yyyy-mm-dd, must be between " +
                    weekStart + " and " + weekStart.plusDays(6) + "): ");
            LocalDate shiftDate = LocalDate.parse(scanner.nextLine().trim());

            if (shiftDate.isBefore(weekStart) || shiftDate.isAfter(weekStart.plusDays(6))) {
                System.out.println("\n[ERROR] Date must be within the selected week");
                return;
            }
            System.out.print("Enter START time (hh:mm): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine().trim());

            System.out.print("----Enter END time (hh:mm): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine().trim());

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

    private static WorkSchedule findSchedule(List<WorkSchedule> schedules, LocalDate weekStart) {
        return schedules.stream()
                .filter(s -> s != null && s.getWeekStartDate().equals(weekStart))
                .findFirst()
                .orElse(null);
    }

    private static Employee findEmployee(List<Employee> employees, int id) {
        return employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static void viewEmployees(List<Employee> employees) {
        System.out.println("\n=== EMPLOYEES ===");
        System.out.printf("%-5s %-20s %-15s\n", "ID", "Name", "Role");
        employees.forEach(e -> System.out.printf("%-5d %-20s %-15s\n",
                e.getId(), e.getName(), e.getRole()));
    }

    public void addShift(Shift shift) throws InputValidationException {
        Objects.requireNonNull(shift, "Shift cannot be null");
        LocalDate date = shift.getDate();
        if (!dailyShifts.containsKey(date)) {
            throw new InputValidationException("Date is not in the scheduled week", "date");
        }
        validateShift(shift);
        dailyShifts.get(date).add(shift);
    }

    private void validateShift(Shift shift) throws InputValidationException {
        LocalDate date = shift.getDate();
        boolean hasConflict = dailyShifts.get(date).stream()
                .anyMatch(existingShift -> existingShift.overlapsWith(shift));
        if (hasConflict) {
            throw new InputValidationException("Shift conflicts with existing schedule", "time");
        }
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

    public List<Shift> getShiftsForDate(LocalDate date) {
        return dailyShifts.getOrDefault(date, Collections.emptyList());
    }
}