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
public class Quantity implements Serializable, Comparable<Quantity> {
    private static final long serialVersionUID = 4878792675728891096L;

    /**
     * The quantity value
     */
    private BigDecimal quantity;

    private String quantityString = null;

    /**
     * The unit of this quantity, i.e. 's', 'MB'. Can be null when the quantity has no unit.
     */
    private final String unit;

    public Quantity(final BigDecimal quantity) {
        this(quantity, null);
    }

    public Quantity(String quantityString) {
        this(quantityString, null);
    }

    public Quantity(final BigDecimal quantity, final String unit) {
        Validate.notNull(quantity, "quantity can't be null");

        this.quantity = quantity;
        this.unit = unit;
    }

    public Quantity(String quantity, final String unit) {
        Validate.notNull(quantity, "quantity can't be null");
        this.quantityString = quantity;
        this.unit = unit;
    }

    /**
     * Copy constructor
     *
     * @param from the <code>Quantity</code> to copy.
     */
    public Quantity(Quantity from) {
        Validate.notNull(from, "quantity can't be null");

        this.quantity = from.getQuantity();
        this.unit = from.getUnit();
    }

    public static Quantity valueOf(final String quantity) {
        Validate.notNull(quantity, "quantity can't be null");

        return new Quantity(new BigDecimal(quantity));
    }

    public static Quantity valueOf(final String quantity, final String unit) {
        Validate.notNull(quantity, "quantity can't be null");
        if (!"UNLIMITED".equalsIgnoreCase(quantity)) {
            BigDecimal remaining = new BigDecimal(quantity);
            return new Quantity(remaining, unit);
        } else {
            Quantity unlimitedQuantity = new Quantity(quantity, unit);
            return unlimitedQuantity;
        }
    }

    public static Quantity valueOf(int quantity) {
        return new Quantity(new BigDecimal(quantity));
    }

    public static Quantity valueOf(int quantity, final String unit) {
        return new Quantity(new BigDecimal(quantity), unit);
    }

    public static Quantity valueOf(long quantity) {
        return new Quantity(new BigDecimal(quantity));
    }

    public static Quantity valueOf(long quantity, final String unit) {
        return new Quantity(new BigDecimal(quantity), unit);
    }

    public static Quantity valueOf(double quantity) {
        return new Quantity(new BigDecimal(quantity));
    }

    public static Quantity valueOf(double quantity, final String unit) {
        return new Quantity(new BigDecimal(quantity), unit);
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getQuantityString() {
        return quantityString;
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

        Quantity other = (Quantity) object;
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
    public int compareTo(Quantity other) {
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
    public Quantity add(Quantity augend) {
        Validate.notNull(augend, "argument can't be null");
        Validate.notNull(augend.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && augend.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(augend.getUnit()), "units can't be different");
        }

        return new Quantity(this.quantity.add(augend.getQuantity()), this.getUnit());
    }

    public Quantity subtract(Quantity quantityTosubstract) {
        Validate.notNull(quantityTosubstract, "argument can't be null");
        Validate.notNull(quantityTosubstract.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && quantityTosubstract.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(quantityTosubstract.getUnit()), "units can't be different");
        }

        Quantity newQuantity = new Quantity(this.quantity.subtract(quantityTosubstract.getQuantity()), this.getUnit());
        if (newQuantity.getQuantity().intValue() < 0) {
            newQuantity = new Quantity(BigDecimal.ZERO, this.getUnit());
        }
        return newQuantity;
    }

    public Quantity subtractToNegative(Quantity quantityTosubstract) {
        Validate.notNull(quantityTosubstract, "argument can't be null");
        Validate.notNull(quantityTosubstract.getQuantity(), "quantity can't be null");
        if (this.getUnit() != null && quantityTosubstract.getUnit() != null) {
            Validate.isTrue(this.getUnit().equals(quantityTosubstract.getUnit()), "units can't be different");
        }

        Quantity newQuantity = new Quantity(this.quantity.subtract(quantityTosubstract.getQuantity()), this.getUnit());
        return newQuantity;
    }
}
