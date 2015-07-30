package com.gigaspaces.marcello.model;

import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by werner on 30/10/14.
 */
public class DiscountWithDouble implements Serializable {
    private static final long serialVersionUID = 7996631756953522187L;

    private String percentage;

    private AmountWithDoubles discountAmount;

    public DiscountWithDouble(String percentage) {
        Validate.isTrue(percentage != null, "percentage must be not null");

        this.percentage = percentage;
    }

    public DiscountWithDouble(String percentage, AmountWithDoubles discountAmount) {
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

        DiscountWithDouble discount = (DiscountWithDouble) object;

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

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public AmountWithDoubles getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(AmountWithDoubles discountAmount) {
        this.discountAmount = discountAmount;
    }

    public DiscountWithDouble(DiscountWithDouble from) {
        Validate.notNull(from, "discount can't be null");

        this.percentage = from.getPercentage();
        if (from.getDiscountAmount() != null) {
            this.discountAmount = new AmountWithDoubles(from.getDiscountAmount().getAmount(), from.getDiscountAmount().getAmountExcludingVat(), from.getDiscountAmount().getVat(), from.getDiscountAmount().getCurrency());
        }
    }

    public DiscountWithDouble addDiscountAmount(AmountWithDoubles discountAmount) {
        return new DiscountWithDouble(null, this.discountAmount.add(discountAmount));
    }

    public static DiscountWithDouble createForAmount(AmountWithDoubles amount, String percentage) {
        if (amount == null || percentage == null) {
            return null;
        }


        BigDecimal percentaqeDecimal = new BigDecimal(percentage);
        BigDecimal discountValue = new BigDecimal(amount.getAmount()).multiply((percentaqeDecimal.divide(BigDecimal.valueOf(100))));
        discountValue = discountValue.setScale(2, RoundingMode.HALF_DOWN);

        BigDecimal discountVatValue = null;
        if (amount.getVat() != null) {
            discountVatValue = new BigDecimal(amount.getVat()).multiply((percentaqeDecimal.divide(BigDecimal.valueOf(100))));
            discountVatValue = discountVatValue.setScale(2, RoundingMode.HALF_DOWN);
        }
        BigDecimal discountExcludingVatValue = null;
        if (amount.getAmountExcludingVat() != null) {
            discountExcludingVatValue = new BigDecimal(amount.getAmountExcludingVat()).multiply((percentaqeDecimal.divide(BigDecimal.valueOf(100))));
            discountExcludingVatValue = discountExcludingVatValue.setScale(2, RoundingMode.HALF_DOWN);
        }

        AmountWithDoubles discountAmount = AmountWithDoubles.valueOf(discountValue, discountExcludingVatValue, discountVatValue, amount.getCurrency());
        return new DiscountWithDouble(percentage, discountAmount);
    }
}
