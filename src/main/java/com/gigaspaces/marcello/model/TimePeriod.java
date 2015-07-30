/***************************************************************************************************
 * Copyright 2012 TeliaSonera. All rights reserved.
 **************************************************************************************************/
package com.gigaspaces.marcello.model;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents a period of time, including date and time.
 * 
 * This class is immutable.
 * 
 * @author <a href="mailto:stephan.kohler@netcom-gsm.no">Stephan Köhler</a>
 */
public class TimePeriod implements Serializable, Comparable<TimePeriod> {
     private static final long serialVersionUID = 127637207779691667L;


    /**
     * The start of the period.
     */
    private Date start;

    /**
     * The end of the period.
     */
    private Date end;

    public TimePeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Copy constructor
     * 
     * @param from the <code>TimePeriod</code> to copy.
     */
    public TimePeriod(TimePeriod from) {
        Validate.notNull(from, "TimePeriod can't be null");
        
        this.start = from.getStart() != null ? new Date(from.getStart().getTime()) : null;
        this.end = from.getEnd() != null ? new Date(from.getEnd().getTime()) : null;
    }

    public TimePeriod(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
        start = new GregorianCalendar(fromYear, fromMonth, fromDay).getTime();
        end = new GregorianCalendar(toYear, toMonth, toDay).getTime();
    }

    public static TimePeriod valueOf(long start, long end) {
        return new TimePeriod(new Date(start), new Date(end));
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
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
        
        TimePeriod other = (TimePeriod) object;
        return new EqualsBuilder()
                .append(this.start, other.getStart())
                .append(this.end, other.getEnd())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getStart())
                .append(getEnd())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("start", getStart())
                .append("end", getEnd())
                .toString();
    }

    /**
     * Compares two time periods for ordering.
     * 
     * @param other the <code>TimePeriod</code>
     * @return a value less than 0 if the start is before the start of the argument; 
     * a value higher than 0 if the start is after the start of the argument. 
     * <p>
     * When start dates are equal, 
     * returns a value less than 0 if the end is before the end of the argument; 
     * a value higher than 0 if the end is after the end of the argument. 
     * </p>
     * <p>
     * returns 0 when both start and end of the periods are equal or one of the dates being compared is <code>null</code>.
     * </p>
     */
    @Override
    public int compareTo(TimePeriod other) {
        int result = 0;
        
        if (this.start != null && other.getStart() != null) {
            result = this.start.compareTo(other.getStart());
        }
        
        if (result == 0 && this.end != null && other.getEnd() != null) {
            result = this.end.compareTo(other.getEnd());
        }
        
        return result;
    }
    
    /**
     * Returns true if the given date falls within this <code>TimePeriod</code>
     * That is, if start &lt; date &lt; end.
     * 
     * @param date the date to test
     * @return true if the given date falls within this <code>TimePeriod</code>
     * @throws IllegalArgumentException when the given date is null
     */
    public boolean isInPeriod(Date date) throws IllegalArgumentException {
        Validate.notNull(date, "date can't be null");
        
        if (this.start == null || this.start.compareTo(date) >= 0) {
            return false;
        }

        if (this.end == null || this.end.compareTo(date) <= 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a TimePeriod that is a union of this time period and the given time period.
     * <p>
     * Set the start of the time period to the earliest of the two periods, 
     * and sets the end to the latest of the two periods.
     * </p>
     * 
     * @param timePeriod the time period to union with.
     * @return a TimePeriod that is a union of this time period and the given time period.
     * @throws IllegalArgumentException when the given timePeriod is null
     */
    public TimePeriod union(TimePeriod timePeriod) throws IllegalArgumentException {
        Validate.notNull(timePeriod, "argument can't be null");
        
        Date newStart;
        Date newEnd;
        
        if (timePeriod.getStart() != null) {
            if (this.start != null) {
                newStart = this.start.compareTo(timePeriod.getStart()) < 0 ? new Date(this.start.getTime()) : new Date(timePeriod.getStart().getTime());
            } else {
                newStart = timePeriod.getStart() != null ? new Date(timePeriod.getStart().getTime()) : null;;
            }
        } else {
            newStart = this.getStart() != null ? new Date(this.getStart().getTime()) : null;
        }
        
        if (timePeriod.getEnd() != null) {
            if (this.end != null) {
                newEnd = this.end.compareTo(timePeriod.getEnd()) > 0 ? new Date(this.end.getTime()) : new Date(timePeriod.getEnd().getTime());
            } else {
                newEnd = timePeriod.getEnd() != null ? new Date(timePeriod.getEnd().getTime()) : null;
            }
        } else {
            newEnd = this.getEnd() != null ? new Date(this.getEnd().getTime()) : null;
        }
        
        return new TimePeriod(newStart, newEnd);
    }

    public boolean isTouchedByPeriod(TimePeriod timePeriod) {
        if (DateUtils.isSameDay(start, timePeriod.start) || DateUtils.isSameDay(end, timePeriod.end)) {
            return true;
        }else if (start.after(timePeriod.start) && start.before(timePeriod.end)) {
            return true;
        } else if (end.before(timePeriod.end) && (end.after(timePeriod.start) || DateUtils.isSameDay(end, timePeriod.start))) {
            return true;
        }
        return false;
    }

    public boolean startsBefore(Date startDate) {
        return start.compareTo(startDate) <= 0;
    }
}
