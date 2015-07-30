/***************************************************************************************************
 * Copyright 2012 TeliaSonera. All rights reserved.
 **************************************************************************************************/
package com.gigaspaces.marcello.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents an amount with a currency. Using the vat and amountExcludingVat field is optional.
 * 
 * This class is immutable.
 * 
 * @author <a href="mailto:stephan.kohler@netcom-gsm.no">Stephan Köhler</a>
 */
public class AmountWithDoubles implements Serializable, Comparable<AmountWithDoubles> {
    private static final long serialVersionUID = 6410939032828810832L;

    /**
     * The amount represented.
     */
    private Double amount;

    /**
     * The amount without the VAT.
     */
    private Double amountExcludingVat;

    /**
     * The amount of VAT.
     */
    private Double vat;

    /**
     * The currency of the amount.
     */
    private String currency;

    private  String amountString = null;

    public AmountWithDoubles() {

    }

    public AmountWithDoubles(final Double amount, final String currency) {
        this(amount, null, null, currency);
    }

    public AmountWithDoubles(final Double amount, final Double amountExcludingVat, final Double vat, final String currency) {
        Validate.isTrue(amount != null || amountExcludingVat != null, "amount or amountExcludingVat must be not null");
        Validate.notNull(currency, "currency can't be null");
        Validate.notEmpty(currency, "currency can't be empty");

        this.amount = amount;
        if (currency != null) {
            this.currency = currency.toUpperCase();
        } else {
            this.currency = null;
        }
        this.amountExcludingVat = amountExcludingVat;
        this.vat = vat;
    }

    /**
     * Copy constructor
     *
     * @param from the <code>Amount</code> to copy.
     */
    public AmountWithDoubles(AmountWithDoubles from) {
        Validate.notNull(from, "amount can't be null");

        this.amount = from.getAmount();
        this.amountExcludingVat = from.getAmountExcludingVat();
        this.vat = from.getVat();
        this.currency = from.getCurrency();
    }

    public AmountWithDoubles(String amountString) {
        this(amountString, null);
    }

    public AmountWithDoubles(String amount, final String currency) {
        Validate.notNull(amount, "amount can't be null");
        this.amountString = amount;
        this.currency = currency;
    }

    public static AmountWithDoubles valueOf(final String amountString, final String currency) {
        if(!"UNLIMITED".equalsIgnoreCase(amountString)){
            int decimalPlaces = 2;
            Double amount = new BigDecimal(amountString).setScale(decimalPlaces, BigDecimal.ROUND_DOWN).doubleValue();
            return new AmountWithDoubles(amount,currency);
        }
        else{
            AmountWithDoubles amount = new AmountWithDoubles(amountString,currency);
            return  amount;
        }
    }


    public static AmountWithDoubles valueOf(final String amount, final String amountExcludingVat, final String currency) {
        return AmountWithDoubles.valueOf(amount, amountExcludingVat, null, currency);
    }

    public static AmountWithDoubles valueOf(final String amount, final String amountExcludingVat, final String vat, final String currency) {
        return new AmountWithDoubles(
                amount == null ? null : new Double(amount),
                amountExcludingVat == null ? null : new Double(amountExcludingVat),
                vat == null ? null : new Double(vat),
                currency);
    }

    // int support
    public static AmountWithDoubles valueOf(int amount, final String currency) {
        return new AmountWithDoubles(new Double(amount), currency);
    }

    public static AmountWithDoubles valueOf(int amount, int amountExcludingVat, final String currency) {
        return AmountWithDoubles.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static AmountWithDoubles valueOf(int amount, int amountExcludingVat, int vat, final String currency) {
        return new AmountWithDoubles(new Double(amount), new Double(amountExcludingVat), new Double(vat), currency);
    }

    // long support
    public static AmountWithDoubles valueOf(long amount, final String currency) {
        return new AmountWithDoubles(new Double(amount), currency);
    }

    public static AmountWithDoubles valueOf(long amount, long amountExcludingVat, final String currency) {
        return AmountWithDoubles.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static AmountWithDoubles valueOf(long amount, long amountExcludingVat, long vat, final String currency) {
        return new AmountWithDoubles(new Double(amount), new Double(amountExcludingVat), new Double(vat), currency);
    }

    // double support
    public static AmountWithDoubles valueOf(double amount, final String currency) {
        return new AmountWithDoubles(amount, currency);
    }

    public static AmountWithDoubles valueOf(double amount, double amountExcludingVat, final String currency) {
        return AmountWithDoubles.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static AmountWithDoubles valueOf(double amount, double amountExcludingVat, double vat, final String currency) {
        return new AmountWithDoubles(amount, amountExcludingVat, vat, currency);
    }

    public static AmountWithDoubles changeSign(AmountWithDoubles amount) {
        return new AmountWithDoubles(amount.getAmount() * -1, amount.getAmountExcludingVat() *-1, amount.getVat() *-1, amount.getCurrency());
    }

    /**
     * Returns an amount with the same amount and currency as the given amount, 
     * but without amountExcludingVat and vat fields set.
     * 
     * @param amount the amount to use
     * @return an amount with the same amount and currency as the given amount, 
     * but without amountExcludingVat and vat fields set.
     */
    public static AmountWithDoubles removeVatFields(AmountWithDoubles amount) {
        Validate.notNull(amount, "amount can't be null");
        
        return new AmountWithDoubles(amount.getAmount(), amount.getCurrency());
    }
    
    public Double getAmount() {
        return amount;
    }

    public Double getAmountExcludingVat() {
        return amountExcludingVat;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getVat() {
        return vat;
    }

    public String getAmountString() {
        return amountString;
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
        
        AmountWithDoubles other = (AmountWithDoubles) object;
        return new EqualsBuilder()
                .append(this.amount, other.getAmount())
                .append(this.amountExcludingVat, other.getAmountExcludingVat())
                .append(this.currency, other.getCurrency())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAmount())
                .append(getAmountExcludingVat())
                .append(getCurrency())
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        if (this.amount != null) {
            result.append(this.amount);
        }
        
        if (this.amountExcludingVat != null) {
            result.append(" (");
            result.append(this.amountExcludingVat);
        }
        
        if (this.vat != null) {
            result.append(" + ");
            result.append(this.vat);
            result.append(")");
        }
        
        if (StringUtils.isNotBlank(this.currency)) {
            result.append(" ");
            result.append(this.currency);
        }
        
        return result.toString();
    }

    @Override
    public int compareTo(AmountWithDoubles other) {
        return new CompareToBuilder()
                .append(getAmount(), other.getAmount())
                .append(getCurrency(), other.getCurrency())
                .toComparison();
    }
    
    /**
     * Returns an Amount whose value is (this + augend)
     * 
     * @param augend the amount to add to this amount.
     * @throws IllegalArgumentException when the amount or currency is null, or the currencies are not equal.
     */
    public AmountWithDoubles add(AmountWithDoubles augend) {
        Validate.notNull(augend, "argument can't be null");
        Validate.notNull(augend.getAmount(), "amount can't be null");
        Validate.notNull(augend.getCurrency(), "currency can't be null");
        Validate.notEmpty(augend.getCurrency(), "currency can't be empty");
        Validate.isTrue(this.getCurrency().equals(augend.getCurrency()), "currencies can't be different");
        
        Double newAmount = this.amount + augend.getAmount();
        
        Double currentAmountExcludingVat = this.amountExcludingVat != null
                ? this.amountExcludingVat
                : 0;

        Double currentVat = this.vat != null
                ? this.vat
                : 0;

        Double newAmountExcludingVat = augend.getAmountExcludingVat() != null
                ? currentAmountExcludingVat + augend.getAmountExcludingVat()
                : currentAmountExcludingVat;

        Double newVat = augend.getVat() != null
                ? currentVat + augend.getVat()
                : currentVat;

        if (newAmountExcludingVat.equals(0)) {
            newAmountExcludingVat = null;
        }

        if (newVat.equals(0)) {
            newVat = null;
        }

        return new AmountWithDoubles(newAmount, newAmountExcludingVat, newVat, this.getCurrency());
    }
    
    /**
     * Returns an Amount whose value is (this - substrahend)
     * 
     * @param subtrahend the amount to subtract from this amount.
     * @throws IllegalArgumentException when the amount or currency is null, or the currencies are not equal.
     */
    public AmountWithDoubles subtract(AmountWithDoubles subtrahend) {
        Validate.notNull(subtrahend, "argument can't be null");
        Validate.notNull(subtrahend.getAmount(), "amount can't be null");
        Validate.notNull(subtrahend.getCurrency(), "currency can't be null");
        Validate.notEmpty(subtrahend.getCurrency(), "currency can't be empty");
        Validate.isTrue(this.getCurrency().equals(subtrahend.getCurrency()), "currencies can't be different");
        
        Double newAmount = this.amount -subtrahend.getAmount();
        
        Double currentAmountExcludingVat = this.amountExcludingVat != null
                ? this.amountExcludingVat
                : 0;

        Double currentVat = this.vat != null
                ? this.vat
                : 0;

        Double newAmountExcludingVat = subtrahend.getAmountExcludingVat() != null
                ? currentAmountExcludingVat -subtrahend.getAmountExcludingVat()
                : currentAmountExcludingVat;

        Double newVat = subtrahend.getVat() != null
                ? currentVat -subtrahend.getVat()
                : currentVat;

        if (newAmountExcludingVat.equals(0)) {
            newAmountExcludingVat = null;
        }

        if (newVat.equals(0)) {
            newVat = null;
        }
        
        return new AmountWithDoubles(newAmount, newAmountExcludingVat, newVat, this.getCurrency());
    }

    public static AmountWithDoubles valueOf(Amount amount) {
        return valueOf(amount.getAmount(), amount.getAmountExcludingVat(), amount.getVat(), amount.getCurrency());
    }

    public static AmountWithDoubles valueOf(BigDecimal amount, BigDecimal amountExcludingVat, BigDecimal vat, String currency) {
        return new AmountWithDoubles(
                amount == null ? null : amount.doubleValue(),
                amountExcludingVat == null ? null : amountExcludingVat.doubleValue(),
                vat == null ? null : vat.doubleValue(),
                currency);

    }
}
