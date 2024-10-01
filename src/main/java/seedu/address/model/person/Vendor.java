package seedu.address.model.person;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a Vendor in the address book.
 * A Vendor is a specific type of Person with an additional company field.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Vendor extends Person {

    // Identity field specific to vendor
    private final Company company;

    /**
     * Constructs a {@code Vendor} with the specified details.
     * Every field must be present and not null.
     *
     * @param name Name of the vendor.
     * @param company Company associated with the vendor.
     * @param phone Phone number of the vendor.
     * @param email Email address of the vendor.
     * @param address Residential or business address of the vendor.
     * @param tags Tags associated with the vendor.
     */
    public Vendor(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Company company) {
        super(name, phone, email, address, tags);
        this.company = company;
    }

    /**
     * Returns the company associated with this vendor.
     *
     * @return The company of the vendor.
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Returns true if both persons have the same name and are both vendors.
     * They are different entries if they are of different roles.
     *
     * @param otherPerson The person to compare with.
     * @return True if both are the same person and both are vendors.
     */
    @Override
    public boolean isSamePerson(Person otherPerson) {
        return super.isSamePerson(otherPerson)
                && (otherPerson instanceof Vendor);
    }

    /**
     * Returns true if this vendor is equal to another vendor.
     * Equality is defined by the combination of all identity fields and the company field.
     *
     * @param other The object to compare with.
     * @return True if both vendors have the same fields and company.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Vendor)) {
            return false;
        }

        Vendor otherVendor = (Vendor) other;
        return super.equals(other)
                && company.equals(otherVendor.company);
    }

    /**
     * Returns the hash code for this vendor.
     * The hash code is computed using all identity fields and the company field.
     *
     * @return The hash code of the vendor.
     */
    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(getName(), getPhone(), getEmail(), getAddress(), getTags(), getCompany());
    }

    /**
     * Returns the string representation of this vendor, including all person fields and the company field.
     *
     * @return A string representation of the vendor.
     */
    @Override
    public String toString() {
        return super.toStringBuilder()
                .add("company", company)
                .toString();
    }
}