package com.gigaspaces.marcello.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;


import java.util.Collection;

@SpaceClass
public class SubscriberChargeData extends ChargeData {
    private static final long serialVersionUID = 4750813751156270540L;


    private TelephoneNumber telephoneNumber;

    public SubscriberChargeData() {

    }

    public SubscriberChargeData(String customerAccountId, int billSequence, TelephoneNumber telephoneNumber) {
        this(customerAccountId, billSequence, telephoneNumber, null);
    }

    public SubscriberChargeData(String customerAccountId, int billSequence, TelephoneNumber telephoneNumber, TimePeriod period) {
        super(customerAccountId, billSequence, period);
        this.telephoneNumber = telephoneNumber;
        setId(customerAccountId + "_" + billSequence + "_" + telephoneNumber.getCountryCode() + telephoneNumber.getLocalNumber());
    }


    @SpaceIndex
    public TelephoneNumber getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(TelephoneNumber telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCustomerAccountId(), getBillSequence(), telephoneNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final SubscriberChargeData other = (SubscriberChargeData) obj;
        return Objects.equal(this.telephoneNumber, other.telephoneNumber);
    }

    @Override
    protected boolean isAccountLevel() {
        return false;
    }

    public void postProcess() {
        updateDiscount();
        sortRows();
        createChargeGroupTotals();
    }

    private void updateDiscount() {
        Collection<ChargeRow> discountRows = Lists.newArrayList(Collections2.filter(getChargeRows(), new isDiscountRow()));

        for (ChargeRow discountRow : discountRows) {
            if ( discountRow.getDiscount().getDiscountAmount() == null ){
                getChargeRows().remove(discountRow);
            }
            for (ChargeRow chargeRow : getChargeRows()) {
                if (chargeRow.getFeatureCategory().equals(discountRow.getFeatureCategory())) {
                    chargeRow.updateWithDiscount(discountRow.getDiscount().getPercentage());
                }
            }
        }

    }

    private static class isDiscountRow implements Predicate<ChargeRow> {
        @Override
        public boolean apply(ChargeRow chargeRow) {
            return chargeRow.getDiscount() != null;
        }
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("ban", getCustomerAccountId())
                .add("bill_seq", getBillSequence())
                .add("telephoneNumber", telephoneNumber)
                .add("period", getPeriod())
                .toString();
    }
}
