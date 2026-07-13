/**
 * The InputValidationException class represents a custom exception
 * that is thrown when input validation fails. This class extends
 * the standard Exception class and provides additional context about
 * the specific field that caused the validation failure.
 *
 * This exception can be used to indicate input errors in various
 * parts of an application, allowing developers to handle cases
 * where user input does not meet the expected criteria.
 */

public class InputValidationException extends Exception {
   private final String field;

    public InputValidationException(String message, String field) {
     super(message);
    this.field = field;
   }

   public String getField() {
       return this.field;
    }
}
