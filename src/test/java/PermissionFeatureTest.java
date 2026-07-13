import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class PermissionFeatureTest {

    @Test
    void allEnumConstants_ShouldBeAccessible() {
        assertNotNull(PermissionFeature.MANAGE_SCHEDULE);
        assertNotNull(PermissionFeature.CREATE_ORDER);
        assertNotNull(PermissionFeature.EDIT_ORDER);
        assertNotNull(PermissionFeature.UPDATE_INVENTORY);
        assertNotNull(PermissionFeature.CHANGE_ORDER_STATUS);
        assertNotNull(PermissionFeature.MANAGE_EMPLOYEES);
        assertNotNull(PermissionFeature.MANAGE_MENU);
        assertNotNull(PermissionFeature.VIEW_INVENTORY);
        assertNotNull(PermissionFeature.VIEW_MENU);
        assertNotNull(PermissionFeature.MANAGE_BOOKINGS);
        assertNotNull(PermissionFeature.MANAGE_TABLES);
    }
    @Test
    void enumValues_ShouldContainAllDefinedConstants() {
        PermissionFeature[] values = PermissionFeature.values();
        assertEquals(11, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.MANAGE_SCHEDULE));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.CREATE_ORDER));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.EDIT_ORDER));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.UPDATE_INVENTORY));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.CHANGE_ORDER_STATUS));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.MANAGE_EMPLOYEES));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.MANAGE_MENU));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.VIEW_INVENTORY));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.VIEW_MENU));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.MANAGE_BOOKINGS));
        assertTrue(java.util.Arrays.asList(values).contains(PermissionFeature.MANAGE_TABLES));
    }
    @Test
    void valueOf_ShouldReturnCorrectEnumConstant() {
        assertEquals(PermissionFeature.MANAGE_SCHEDULE, PermissionFeature.valueOf("MANAGE_SCHEDULE"));
        assertEquals(PermissionFeature.CREATE_ORDER, PermissionFeature.valueOf("CREATE_ORDER"));
        assertEquals(PermissionFeature.EDIT_ORDER, PermissionFeature.valueOf("EDIT_ORDER"));
    }
    @Test
    void valueOf_ShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            PermissionFeature.valueOf("INVALID_PERMISSION");
        });
    }
}
