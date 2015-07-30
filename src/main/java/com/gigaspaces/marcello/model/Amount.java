/***************************************************************************************************
 * Copyright 2012 TeliaSonera. All rights reserved.
 **************************************************************************************************/
package com.gigaspaces.marcello.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Represents an amount with a currency. Using the vat and amountExcludingVat field is optional.
 * 
 * This class is immutable.
 * 
 * @author <a href="mailto:stephan.kohler@netcom-gsm.no">Stephan Köhler</a>
 */
public class Amount implements Serializable, Comparable<Amount> {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The amount represented.
     */
    private  BigDecimal amount;

    /**
     * The amount without the VAT.
     */
    private  BigDecimal amountExcludingVat;

    /**
     * The amount of VAT.
     */
    private  BigDecimal vat;
    
    /**
     * The currency of the amount.
     */
    private final String currency;

    private  String amountString = null;


    public Amount(final BigDecimal amount, final String currency) {
        this(amount, null, null, currency);
    }

    public Amount(final BigDecimal amount, final BigDecimal amountExcludingVat, final BigDecimal vat, final String currency) {
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
    public Amount(Amount from) {
        Validate.notNull(from, "amount can't be null");

        this.amount = from.getAmount();
        this.amountExcludingVat = from.getAmountExcludingVat();
        this.vat = from.getVat();
        this.currency = from.getCurrency();
    }

    public Amount(String amountString) {
        this(amountString, null);
    }

    public Amount(String amount, final String currency) {
        Validate.notNull(amount, "amount can't be null");
        this.amountString = amount;
        this.currency = currency;
    }

    public static Amount valueOf(final String amountString, final String currency) {
        if(!"UNLIMITED".equalsIgnoreCase(amountString)){
            int decimalPlaces = 2;
            BigDecimal amount = new BigDecimal(amountString);
            amount = amount.setScale(decimalPlaces, BigDecimal.ROUND_DOWN);
            return new Amount(amount,currency);
        }
        else{
            Amount amount = new Amount(amountString,currency);
            return  amount;
        }
    }


    public static Amount valueOf(final String amount, final String amountExcludingVat, final String currency) {
        return Amount.valueOf(amount, amountExcludingVat, null, currency);
    }

    public static Amount valueOf(final String amount, final String amountExcludingVat, final String vat, final String currency) {
        return new Amount(
                amount == null ? null : new BigDecimal(amount),
                amountExcludingVat == null ? null : new BigDecimal(amountExcludingVat),
                vat == null ? null : new BigDecimal(vat),
                currency);
    }

    // int support
    public static Amount valueOf(int amount, final String currency) {
        return new Amount(new BigDecimal(amount), currency);
    }

    public static Amount valueOf(int amount, int amountExcludingVat, final String currency) {
        return Amount.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static Amount valueOf(int amount, int amountExcludingVat, int vat, final String currency) {
        return new Amount(new BigDecimal(amount), new BigDecimal(amountExcludingVat), new BigDecimal(vat), currency);
    }

    // long support
    public static Amount valueOf(long amount, final String currency) {
        return new Amount(new BigDecimal(amount), currency);
    }

    public static Amount valueOf(long amount, long amountExcludingVat, final String currency) {
        return Amount.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static Amount valueOf(long amount, long amountExcludingVat, long vat, final String currency) {
        return new Amount(new BigDecimal(amount), new BigDecimal(amountExcludingVat), new BigDecimal(vat), currency);
    }

    // double support
    public static Amount valueOf(double amount, final String currency) {
        return new Amount(new BigDecimal(amount), currency);
    }

    public static Amount valueOf(double amount, double amountExcludingVat, final String currency) {
        return Amount.valueOf(amount, amountExcludingVat, 0, currency);
    }

    public static Amount valueOf(double amount, double amountExcludingVat, double vat, final String currency) {
        return new Amount(new BigDecimal(amount), new BigDecimal(amountExcludingVat), new BigDecimal(vat), currency);
    }

    public static Amount changeSign(Amount amount) {
        return new Amount(amount.getAmount().multiply(new BigDecimal("-1")), amount.getAmountExcludingVat().multiply(new BigDecimal("-1")), amount.getVat().multiply(new BigDecimal("-1")), amount.getCurrency());
    }

    /**
     * Returns an amount with the same amount and currency as the given amount, 
     * but without amountExcludingVat and vat fields set.
     * 
     * @param amount the amount to use
     * @return an amount with the same amount and currency as the given amount, 
     * but without amountExcludingVat and vat fields set.
     */
    public static Amount removeVatFields(Amount amount) {
        Validate.notNull(amount, "amount can't be null");
        
        return new Amount(amount.getAmount(), amount.getCurrency());
    }
    
    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getAmountExcludingVat() {
        return amountExcludingVat;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getVat() {
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
        
        Amount other = (Amount) object;
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
    public int compareTo(Amount other) {
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
    public Amount add(Amount augend) {
        Validate.notNull(augend, "argument can't be null");
        Validate.notNull(augend.getAmount(), "amount can't be null");
        Validate.notNull(augend.getCurrency(), "currency can't be null");
        Validate.notEmpty(augend.getCurrency(), "currency can't be empty");
        Validate.isTrue(this.getCurrency().equals(augend.getCurrency()), "currencies can't be different");
        
        BigDecimal newAmount = this.amount.add(augend.getAmount());
        
        BigDecimal currentAmountExcludingVat = this.amountExcludingVat != null
                ? this.amountExcludingVat
                : BigDecimal.ZERO;

        BigDecimal currentVat = this.vat != null
                ? this.vat
                : BigDecimal.ZERO;

        BigDecimal newAmountExcludingVat = augend.getAmountExcludingVat() != null
                ? currentAmountExcludingVat.add(augend.getAmountExcludingVat())
                : currentAmountExcludingVat;

        BigDecimal newVat = augend.getVat() != null
                ? currentVat.add(augend.getVat())
                : currentVat;

        if (newAmountExcludingVat.equals(BigDecimal.ZERO)) {
            newAmountExcludingVat = null;
        }

        if (newVat.equals(BigDecimal.ZERO)) {
            newVat = null;
        }

        return new Amount(newAmount, newAmountExcludingVat, newVat, this.getCurrency());
    }
    
    /**
     * Returns an Amount whose value is (this - substrahend)
     * 
     * @param subtrahend the amount to subtract from this amount.
     * @throws IllegalArgumentException when the amount or currency is null, or the currencies are not equal.
     */
    public Amount subtract(Amount subtrahend) {
        Validate.notNull(subtrahend, "argument can't be null");
        Validate.notNull(subtrahend.getAmount(), "amount can't be null");
        Validate.notNull(subtrahend.getCurrency(), "currency can't be null");
        Validate.notEmpty(subtrahend.getCurrency(), "currency can't be empty");
        Validate.isTrue(this.getCurrency().equals(subtrahend.getCurrency()), "currencies can't be different");
        
        BigDecimal newAmount = this.amount.subtract(subtrahend.getAmount());
        
        BigDecimal currentAmountExcludingVat = this.amountExcludingVat != null
                ? this.amountExcludingVat
                : BigDecimal.ZERO;

        BigDecimal currentVat = this.vat != null
                ? this.vat
                : BigDecimal.ZERO;

        BigDecimal newAmountExcludingVat = subtrahend.getAmountExcludingVat() != null
                ? currentAmountExcludingVat.subtract(subtrahend.getAmountExcludingVat())
                : currentAmountExcludingVat;

        BigDecimal newVat = subtrahend.getVat() != null
                ? currentVat.subtract(subtrahend.getVat())
                : currentVat;

        if (newAmountExcludingVat.equals(BigDecimal.ZERO)) {
            newAmountExcludingVat = null;
        }

        if (newVat.equals(BigDecimal.ZERO)) {
            newVat = null;
        }
        
        return new Amount(newAmount, newAmountExcludingVat, newVat, this.getCurrency());
    }
}
