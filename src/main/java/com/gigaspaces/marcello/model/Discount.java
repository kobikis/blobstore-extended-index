package com.gigaspaces.marcello.model;

import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by werner on 30/10/14.
 */
public class Discount implements Serializable {
    private static final long serialVersionUID = 7996631756953522187L;

    private BigDecimal percentage;

    private Amount discountAmount;

    public Discount(BigDecimal percentage) {
        Validate.isTrue(percentage != null, "percentage must be not null");

        this.percentage = percentage;
    }

    public Discount(BigDecimal percentage, Amount discountAmount) {
        Validate.isTrue(percentage != null || discountAmount != null, "percentage or discountAmount must be not null");

        Validate.isTrue(discountAmount.getAmount() != null || discountAmount.getAmountExcludingVat() != null, "amount or amountExcludingVat on discountAmount must be not null");
        Validate.notNull(discountAmount.getCurrency(), "currency on discountAmount can't be null");
        Validate.notEmpty(discountAmount.getCurrency(), "currency on discountAmount can't be empty");

        this.percentage = percentage;
        this.discountAmount = discountAmount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Discount discount = (Discount) object;

        if (!discountAmount.equals(discount.discountAmount)) return false;
        if (!percentage.equals(discount.percentage)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = percentage != null ? percentage.hashCode() : 0;
        result = 31 * result + (discountAmount != null ? discountAmount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (this.percentage != null) {
            result.append(this.percentage);
        }

        if (this.discountAmount != null) {
            result.append(" (");
            result.append(this.discountAmount);
            result.append(")");
        }

        return result.toString();
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Amount getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Amount discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Discount(Discount from) {
        Validate.notNull(from, "discount can't be null");

        this.percentage = from.getPercentage();
        if (from.getDiscountAmount() != null) {
            this.discountAmount = new Amount(from.getDiscountAmount().getAmount(), from.getDiscountAmount().getAmountExcludingVat(), from.getDiscountAmount().getVat(), from.getDiscountAmount().getCurrency());
        }
    }

    public Discount addDiscountAmount(Amount discountAmount) {
        return new Discount(null, this.discountAmount.add(discountAmount));
    }

    public static Discount createForAmount(Amount amount, BigDecimal percentage) {
        if (amount == null || percentage == null) {
            return null;
        }

        BigDecimal discountValue = amount.getAmount().multiply((percentage.divide(BigDecimal.valueOf(100))));
        discountValue = discountValue.setScale(2, RoundingMode.HALF_DOWN);

        BigDecimal discountVatValue = null;
        if (amount.getVat() != null) {
            discountVatValue = amount.getVat().multiply((percentage.divide(BigDecimal.valueOf(100))));
            discountVatValue = discountVatValue.setScale(2, RoundingMode.HALF_DOWN);
        }
        BigDecimal discountExcludingVatValue = null;
        if (amount.getAmountExcludingVat() != null) {
            discountExcludingVatValue = amount.getAmountExcludingVat().multiply((percentage.divide(BigDecimal.valueOf(100))));
            discountExcludingVatValue = discountExcludingVatValue.setScale(2, RoundingMode.HALF_DOWN);
        }

        Amount discountAmount = new Amount(discountValue, discountExcludingVatValue, discountVatValue, amount.getCurrency());
        return new Discount(percentage, discountAmount);
    }
}
