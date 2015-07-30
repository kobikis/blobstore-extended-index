/***************************************************************************************************
 * Copyright 2012 TeliaSonera. All rights reserved.
 **************************************************************************************************/
package com.gigaspaces.marcello.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Represents a telephone number by the 2 digit ISO country code with a local number.
 * 
 * @author <a href="mailto:stephan.kohler@netcom-gsm.no">Stephan Köhler</a>
 */
public class TelephoneNumber implements Serializable, Comparable<TelephoneNumber> {
    private static final long serialVersionUID = 6958818444094272042L;

    /**
     * The 2 digit country code.
     */
    private String countryCode;
    
    /**
     * The local telephone number.
     */
    private String localNumber;

    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^\\d{1,4}$");
    private static final Pattern LOCAL_NUMBER_PATTERN = Pattern.compile("^\\d+$");

    public TelephoneNumber(final String countryCode, final String localNumber) {
        Validate.notNull(countryCode, "countryCode can't be null");
        Validate.notEmpty(countryCode, "countryCode can't be empty");

        Validate.isTrue(COUNTRY_CODE_PATTERN.matcher(countryCode).matches(), "countryCode should be a 2 digit country code");

        Validate.notNull(localNumber, "localNumber can't be null");
        Validate.notEmpty(localNumber, "localNumber can't be empty");

        Validate.isTrue(LOCAL_NUMBER_PATTERN.matcher(localNumber).matches(), "localNumber should be only digits");
        
        this.countryCode = countryCode;
        this.localNumber = localNumber;
    }
    
    /**
     * Parses the telephone number String to create an instance of <code>TelephoneNumber</code>.
     * <p>
     * Removes any '+' or '00' prefixes, then takes the first 2 digits as the country code and the rest as the local number.
     * </p>
     * 
     * @param telephoneNumber the telephone number to parse.
     * @return the <code>TelephoneNumber</code> represented by the input string
     */
    public static TelephoneNumber valueOf(String telephoneNumber) {
        if (telephoneNumber.startsWith("GSM0")) {
            telephoneNumber = telephoneNumber.substring(4);
        } else if (telephoneNumber.startsWith("GSM")) {
            telephoneNumber = telephoneNumber.substring(3);
        }

        if (telephoneNumber.startsWith("+")) {
            telephoneNumber = telephoneNumber.substring(1);
        } else if (telephoneNumber.startsWith("00")) {
            telephoneNumber = telephoneNumber.substring(2);
        }

        String countryCode = telephoneNumber.substring(0, 2);
        String localNumber = telephoneNumber.substring(2);
        
        return new TelephoneNumber(countryCode, localNumber);
    }

    public static TelephoneNumber valueOf(String telephoneNumber, String languageCode) {
        if (telephoneNumber.startsWith("GSM")) {
            telephoneNumber = telephoneNumber.substring(3);
        }

        if (telephoneNumber.startsWith("+")) {
            telephoneNumber = telephoneNumber.substring(1);
        } else if (telephoneNumber.startsWith("00")) {
            telephoneNumber = telephoneNumber.substring(2);
        }
        String countryCode;
        String localNumber;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = phoneNumberUtil.parse("00" + telephoneNumber, languageCode);
            countryCode = String.valueOf(phoneNumber.getCountryCode());
            localNumber = String.valueOf(phoneNumber.getNationalNumber());

        }catch(NumberParseException e) {
            countryCode = telephoneNumber.substring(0, 2);
            localNumber = telephoneNumber.substring(2);
        }



        return new TelephoneNumber(countryCode, localNumber);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLocalNumber() {
        return localNumber;
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
        
        TelephoneNumber other = (TelephoneNumber) object;
        return new EqualsBuilder()
                .append(this.countryCode, other.getCountryCode())
                .append(this.localNumber, other.getLocalNumber())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCountryCode())
                .append(getLocalNumber())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("+")
                .append(getCountryCode())
                .append(getLocalNumber())
                .toString();
    }

    @Override
    public int compareTo(TelephoneNumber o) {
        return ComparisonChain.start().compare(countryCode, o.countryCode, Ordering.natural().nullsFirst()).compare(localNumber, o.localNumber, Ordering.natural().nullsFirst()).result();
    }
}
