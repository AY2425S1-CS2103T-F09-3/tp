package seedu.address.model.person;
import static java.util.Objects.requireNonNull;

/**
 *  Remark associated with a person
 */
public class Remark {
    public final String value;

    /**
     * Constructor
     * @param remark
     */
    public Remark(String remark) {
        requireNonNull(remark);
        this.value = remark;
    }

    public String getRemark() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Remark // instanceof handles nulls
                && value.equals(((Remark) other).value)); // state check
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
