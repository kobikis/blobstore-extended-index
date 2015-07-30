package com.gigaspaces.marcello.model;

import com.google.common.base.Objects;

import java.io.*;
import java.math.BigDecimal;

public class ChargeRow implements Serializable {
    private static final long serialVersionUID = 8690976519192547147L;

    private static final BigDecimal ZERO_SCALE_TWO = BigDecimal.ZERO.setScale(2);

    private QuantityWithString duration;
    private QuantityWithString quantity;
    private AmountWithDoubles totalAmount;
    private DiscountWithDouble discount;
    private String name;
    private String featureCategory;
    private TimePeriod period;
    private String discountCode;
    private String groupName;

    public ChargeRow() {

    }

    public ChargeRow(String groupName, String name, AmountWithDoubles totalAmount) {
        this.groupName = groupName;
        this.name = name;
        this.totalAmount = totalAmount;
    }

    public QuantityWithString getDuration() {
        return duration;
    }

    public void setDuration(QuantityWithString duration) {
        this.duration = duration;
    }

    public QuantityWithString getQuantity() {
        return quantity;
    }

    public void setQuantity(QuantityWithString quantity) {
        this.quantity = quantity;
    }

    public AmountWithDoubles getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(AmountWithDoubles totalAmount) {
        this.totalAmount = totalAmount;
    }

    public DiscountWithDouble getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountWithDouble discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeatureCategory() {
        return featureCategory;
    }

    public void setFeatureCategory(String featureCategory) {
        this.featureCategory = featureCategory;
    }

    public TimePeriod getPeriod() {
        return period;
    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public void updateWithDiscount(String percentage) {
        if (totalAmount != null && "NO-DISC".equals(discountCode)) {
            setDiscount(DiscountWithDouble.createForAmount(totalAmount, percentage));
            setTotalAmount(totalAmount.subtract(discount.getDiscountAmount()));
        }
    }

    public void aggregate(ChargeRow other) {
        if (other.getDuration() != null) {
            if (this.duration == null) {
                this.duration = new QuantityWithString(other.getDuration());
            } else {
                this.duration = this.duration.add(other.getDuration());
            }
        }

        if (other.getQuantity() != null) {
            if (this.quantity == null) {
                this.quantity = new QuantityWithString(other.getQuantity());
            } else {
                this.quantity = this.quantity.add(other.getQuantity());
            }
        }

        if (other.getTotalAmount() != null) {
            if (this.totalAmount == null) {
                this.totalAmount = new AmountWithDoubles(other.getTotalAmount());
            } else {
                this.totalAmount = this.totalAmount.add(other.getTotalAmount());
            }
        }

        if (other.getDiscount() != null) {
            if (this.discount == null) {
                this.discount = new DiscountWithDouble(other.getDiscount());
            } else if (other.getDiscount().getDiscountAmount() != null) {
                if (discount.getPercentage() != null && discount.getPercentage().equals(other.getDiscount().getPercentage())) {
                    if (discount.getDiscountAmount() == null) {
                        this.discount = new DiscountWithDouble(discount.getPercentage(), other.getDiscount().getDiscountAmount());
                    } else {
                        this.discount = new DiscountWithDouble(discount.getPercentage(), discount.getDiscountAmount().add(other.getDiscount().getDiscountAmount()));
                    }
                } else {
                    this.discount = new DiscountWithDouble(null, discount.getDiscountAmount().add(other.getDiscount().getDiscountAmount()));
                }
            }
        }

        if (other.getPeriod() != null)

        {
            if (this.period == null) {
                this.period = new TimePeriod(other.getPeriod());
            } else {
                this.period = this.period.union(other.getPeriod());
            }
        }

    }

    /*
     public boolean shouldBeAggregatedWith(ChargeRow chargeRow, boolean accountLevel) {
         return chargeRow.getName().equals(name)
                 && chargeRow.getFeatureCategory().equals(featureCategory)
                 && chargeRow.getDiscount() == null
                 && discount == null;

     }
      */
    public boolean shouldBeAggregatedWith(ChargeRow chargeRow, boolean accountLevel) {
        return Objects.equal(chargeRow.getName(), name)
                && Objects.equal(chargeRow.getFeatureCategory(), featureCategory)
                && (accountLevel || (
                chargeRow.getDiscount() == null && discount == null
        )
        );

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("name", name)
                .add("groupName", groupName)
                .add("totalAmount", totalAmount)
                .add("quantity", quantity)
                .add("duration", duration)
                .toString();
    }

    public ChargeRow copy() {
        ChargeRow copy = new ChargeRow();

        copy.duration = duration != null ? new QuantityWithString(duration) : null;
        copy.quantity = quantity != null ? new QuantityWithString(quantity) : null;
        copy.totalAmount = totalAmount != null ? new AmountWithDoubles(totalAmount) : null;
        copy.discount = discount != null ? new DiscountWithDouble(discount) : null;
        copy.name = name;
        copy.featureCategory = featureCategory;
        copy.period = period != null ? new TimePeriod(period) : null;
        copy.discountCode = discountCode;
        copy.groupName = groupName;
        return copy;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updateDiscountAmount(AmountWithDoubles totalAmount) {
        if (totalAmount == null || ZERO_SCALE_TWO.equals(totalAmount.getAmount())) {
            return;
        }
        if (this.discount == null) {
            this.discount = new DiscountWithDouble(null, totalAmount);
        } else {
            this.discount = this.discount.addDiscountAmount(totalAmount);
        }
        if (getTotalAmount() == null) {
            setTotalAmount(AmountWithDoubles.changeSign(totalAmount));
        } else {
            setTotalAmount(getTotalAmount().subtract(totalAmount));
        }
    }
}
