/***************************************************************************************************
 * Copyright 2012 TeliaSonera. All rights reserved.
 **************************************************************************************************/
package com.gigaspaces.marcello.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a quantity with an optional unit.
 *
 * This class is immutable.
 *
 * @author <a href="mailto:stephan.kohler@netcom-gsm.no">Stephan Köhler</a>
 */
public class QuantityWithString implements Serializable, Comparable<QuantityWithString> {
    private static final long serialVersionUID = 4878792675728891096L;

    /**
     * The quantity value
     */
    private String quantity;


    /**
     * The unit of this quantity, i.e. 's', 'MB'. Can be null when the quantity has no unit.
     */
    private final String unit;

    public QuantityWithString(final String quantity) {
        this(quantity, null);
    }

    public QuantityWithString(final String quantity, final String unit) {
        Validate.notNull(quantity, "quantity can't be null");

        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Copy constructor
     *
     * @param from the <code>Quantity</code> to copy.
     */
    public QuantityWithString(QuantityWithString from) {
        Validate.notNull(from, "quantity can't be null");

        this.quantity = from.getQuantity();
        this.unit = from.getUnit();
    }

    public static QuantityWithString valueOf(int quantity) {
        return new QuantityWithString(Integer.toString(quantity));
    }

    public static QuantityWithString valueOf(int quantity, final String unit) {
        return new QuantityWithString(Integer.toString(quantity), unit);
    }

    public static QuantityWithString valueOf(long quantity) {
        return new QuantityWithString(Long.toString(quantity));
    }

    public static QuantityWithString valueOf(long quantity, final String unit) {
        return new QuantityWithString(Long.toString(quantity), unit);
    }

    public static QuantityWithString valueOf(String quantity) {
        return new QuantityWithString(quantity);
    }

    public static QuantityWithString valueOf(String quantity, final String unit) {
        return new QuantityWithString(quantity, unit);
    }

    public String getQuantity() {
        return quantity;
    }


    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object.getClass() != getClass()) {
            return false;
        }

        QuantityWithString other = (QuantityWithString) object;
        return new EqualsBuilder()
            .append(this.quantity, other.getQuantity())
            .append(this.unit, other.getUnit())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(getQuantity())
            .append(getUnit())
            .toHashCode();
    }

    @Override
    public String toString() {
        return this.quantity + (this.unit == null ? "" : " " + this.unit);
    }

    @Override
    public int compareTo(QuantityWithString other) {
        return new CompareToBuilder()
            .append(getQuantity(), other.getQuantity())
            .append(getUnit(), other.getUnit())
            .toComparison();
    }

    /**
     * Returns a Quantity whose value is (this + augend)
     *
     * @param augend the quantity to add to this quantity.
     * @throws IllegalArgumentException when the quantity is null, or the units are not equal.
     */
    public QuantityWithString add(QuantityWithString augend) {
        Validate.notNull(augend, "argument can't be null");
        Validate.notNull(augend.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && augend.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(augend.getUnit()), "units can't be different");
        }

        return new QuantityWithString(new BigDecimal(this.quantity).add(new BigDecimal(augend.getQuantity())).toPlainString(), this.getUnit());
    }

    public QuantityWithString subtract(QuantityWithString quantityTosubstract) {
        Validate.notNull(quantityTosubstract, "argument can't be null");
        Validate.notNull(quantityTosubstract.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && quantityTosubstract.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(quantityTosubstract.getUnit()), "units can't be different");
        }

        BigDecimal subtracted = new BigDecimal(quantity).subtract(new BigDecimal(quantityTosubstract.getQuantity()));
        if (subtracted.intValue() < 0) {
            return new QuantityWithString("0", this.getUnit());
        }
        return new QuantityWithString(subtracted.toPlainString(), getUnit());
    }

    public QuantityWithString subtractToNegative(QuantityWithString quantityTosubstract) {
        Validate.notNull(quantityTosubstract, "argument can't be null");
        Validate.notNull(quantityTosubstract.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && quantityTosubstract.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(quantityTosubstract.getUnit()), "units can't be different");
        }

        BigDecimal subtracted = new BigDecimal(quantity).subtract(new BigDecimal(quantityTosubstract.getQuantity()));
        return new QuantityWithString(subtracted.toPlainString(), getUnit());
    }

    public static QuantityWithString valueOf(Quantity quantity) {
        if (quantity == null) {
            return null;
        }
        return new QuantityWithString(quantity.getQuantity() == null ? null : quantity.getQuantity().toPlainString(), quantity.getUnit());
    }
}
