import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    private Table table;

    @BeforeEach
    void setUp() {
        table = new Table(1, 4);
    }
    @Test
    void testTableInitialization() {
        assertEquals(1, table.getNumber());
        assertEquals(4, table.getCapacity());
        assertTrue(table.isAvailable());
    }
    @Test
    void testSetAvailable() {
        table.setAvailable(false);
        assertFalse(table.isAvailable());

        table.setAvailable(true);
        assertTrue(table.isAvailable());
    }
    @Test
    void testAddTableSuccessfully() {
        List<Table> tables = new ArrayList<>();
        Scanner scanner = new Scanner("2\n4\n");  // Número da mesa: 2, Capacidade: 4

        Table.addTable(tables, scanner);
        assertEquals(1, tables.size());
        assertEquals(2, tables.get(0).getNumber());
        assertEquals(4, tables.get(0).getCapacity());
    }

    @Test
    void testAddTableDuplicateNumber() {
        List<Table> tables = new ArrayList<>();
        tables.add(new Table(3, 6));

        Scanner scanner = new Scanner("3\n6\n");  // Número da mesa duplicado
        Table.addTable(tables, scanner);
        assertEquals(1, tables.size());  // A segunda não deve ser adicionada
    }
}
