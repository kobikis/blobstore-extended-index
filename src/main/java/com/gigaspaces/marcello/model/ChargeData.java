package com.gigaspaces.marcello.model;

import com.gigaspaces.annotation.pojo.*;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ChargeData implements Serializable {
    private static final long serialVersionUID = 8054828245479249671L;

    private String id;

    private String customerAccountId;

    private Integer billSequence;
    private TimePeriod period;


    private List<ChargeRow> chargeRows;
    private List<ChargeRow> chargeGroupTotals;


    public ChargeData() {

    }

    public ChargeData(String customerAccountId, int billSequence, TimePeriod period) {
        this.customerAccountId = customerAccountId;
        this.billSequence = billSequence;
        this.period = period;
    }


    @SpaceId
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SpaceIndex
    @SpaceRouting
    public String getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(String customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    @SpaceIndex
    public Integer getBillSequence() {
        return billSequence;
    }

    public void setBillSequence(Integer billSequence) {
        this.billSequence = billSequence;
    }

    public List<ChargeRow> getChargeRows() {
        return chargeRows;
    }

    public void setChargeRows(List<ChargeRow> chargeRows) {
        this.chargeRows = chargeRows;
    }

    public List<ChargeRow> getChargeGroupTotals() {
        return chargeGroupTotals;
    }

    public void setChargeGroupTotals(List<ChargeRow> chargeGroupTotals) {
        this.chargeGroupTotals = chargeGroupTotals;
    }

    @SpaceIndexes( {@SpaceIndex(path = "start", type = SpaceIndexType.EXTENDED), @SpaceIndex(path = "end", type = SpaceIndexType.EXTENDED)})
    public TimePeriod getPeriod() {
        return period;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerAccountId, billSequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ChargeData other = (ChargeData) obj;
        return Objects.equal(this.customerAccountId, other.customerAccountId)
                && Objects.equal(this.billSequence, other.billSequence);
    }

    public void addRow(ChargeRow chargeRow) {
        if (chargeRows == null) {
            chargeRows = new ArrayList<>();
        }
        ChargeRow rowToAggregate = null;
        for (ChargeRow row : chargeRows) {
            if (row.shouldBeAggregatedWith(chargeRow, isAccountLevel())) {
                rowToAggregate = row;
            }
        }
        if (rowToAggregate != null) {
            rowToAggregate.aggregate(chargeRow);
        } else {
            chargeRows.add(chargeRow);
        }
    }




    @SpaceExclude
    protected abstract boolean isAccountLevel();

    @SpaceExclude
    public ChargeRow getRowByName(String name) {
        if (chargeRows == null) {
            return null;
        }
        for (ChargeRow chargeRow : chargeRows) {
            if (name.equalsIgnoreCase(chargeRow.getName())) {
                return chargeRow;
            }
        }
        return null;
    }




    public void setPeriod(TimePeriod period) {
        this.period = period;
    }


    @SpaceExclude
    public boolean isFirstBillSequence() {
        return getBillSequence() != null && getBillSequence().intValue() == 1;
    }


    public void createChargeGroupTotals() {
        chargeGroupTotals = new ArrayList<>();

        ChargeRow  group = new ChargeRow();

        for (ChargeRow chargeRow : chargeRows) {
            if (chargeRow.getGroupName() == null) {
                continue;
            }
            if (!chargeRow.getGroupName().equals(group.getGroupName())) {
                group = new ChargeRow();
                group.setGroupName(chargeRow.getGroupName());
                chargeGroupTotals.add(group);
            }
            if (chargeRow.getTotalAmount() != null) {
                if (group.getTotalAmount() == null) {
                    group.setTotalAmount(new AmountWithDoubles(chargeRow.getTotalAmount()));
                } else {
                    group.setTotalAmount(group.getTotalAmount().add(chargeRow.getTotalAmount()));
                }
            }
        }
    }

    public void sortRows() {
        if (chargeRows != null) {
            Collections.sort(getChargeRows(), new Comparator<ChargeRow>() {
                @Override
                public int compare(ChargeRow o1, ChargeRow o2) {
                    return ComparisonChain.start().compare(o1.getGroupName(), o2.getGroupName(), Ordering.natural().nullsLast()).compare(o1.getName(), o2.getName(), Ordering.natural().nullsLast()).result();
                }
            });
        }
    }

    public void addRows(List<ChargeRow> chargeRows) {
        if (chargeRows != null) {
            for (ChargeRow chargeRow : chargeRows) {
                addRow(chargeRow);
            }
        }
    }

    public abstract void postProcess();
}
