/**
 * The PermissionFeature enum defines a list of specific permissions
 * that can be assigned to different roles or employees within the system.
 * These permissions are used to control access to various features and functionalities.
 *
 * Each constant in this enum represents a distinct capability that a user
 * with the associated permission is allowed to perform.
 *
 * Examples of functionalities include managing orders, updating inventory,
 * viewing schedules, and handling bookings.
 *
 * This enum can be utilized to enforce role-based access control (RBAC)
 * by associating specific permissions with user roles.
 */
public enum PermissionFeature {
    MANAGE_SCHEDULE,
    CREATE_ORDER,
    EDIT_ORDER,
    UPDATE_INVENTORY,
    CHANGE_ORDER_STATUS,
    MANAGE_EMPLOYEES,
    //VIEW_SCHEDULE,
    MANAGE_MENU, VIEW_INVENTORY, VIEW_MENU, MANAGE_BOOKINGS, MANAGE_TABLES;


    private PermissionFeature() {
    }
}
