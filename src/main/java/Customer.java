import java.util.Objects;
import java.util.UUID;

/**
 * Represents a customer with a unique identifier, name, phone number, and email address.
 * The Customer class provides methods to retrieve and modify certain attributes
 * while ensuring immutability for others.
 */
public class Customer {
    private final String id = UUID.randomUUID().toString();
    private String name;
    private String phone;
    private final String email;

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }
    public void setPhone(String phone) {
        this.phone = Objects.requireNonNull(phone);
    }
    public String getName() {
        return this.name;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getEmail() {
        return this.email;
    }
}
