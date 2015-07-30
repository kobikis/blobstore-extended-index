package com.gigaspaces.marcello.feeder;

import com.gigaspaces.marcello.model.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author kobi on 7/30/15.
 * @since 10.2
 */
public class Feeder {

    static Random randomGenerator = new Random();

    static void feedData(GigaSpace gigaSpace, int amount){
        TelephoneNumber telephoneNumber;
        SubscriberChargeData subscriberChargeData;
        for(int i = 0; i < amount; i++){
            int rndTelephoneNumber = randomGenerator.nextInt(100);
            int billSequence = randomGenerator.nextInt(1000000);
            telephoneNumber = new TelephoneNumber(String.valueOf(rndTelephoneNumber), String.valueOf(rndTelephoneNumber));
            subscriberChargeData = new SubscriberChargeData(String.valueOf(i), billSequence, telephoneNumber, generateTimePeriod());
            List<ChargeRow> rows = new ArrayList<ChargeRow>();
            for(int j = 0; j < 13; j++){
                rows.add(generateChargeRow());
            }
            subscriberChargeData.setChargeRows(rows);

            List<ChargeRow> rowTotals = new ArrayList<ChargeRow>();
            rowTotals.add(generateChargeRow());
            subscriberChargeData.setChargeGroupTotals(rowTotals);

            gigaSpace.write(subscriberChargeData);
        }
    }

    static TimePeriod generateTimePeriod(){
        long beginTime = Timestamp.valueOf("2010-01-01 00:00:00").getTime();
        long endTime = Timestamp.valueOf("2015-12-31 00:58:00").getTime();
        long diff = endTime - beginTime + 1;

        Date beginDate = new Date(beginTime + (long) (Math.random() * diff));
        Date endDate = new Date(beginDate.getTime() + (long) (Math.random() * diff));
        return new TimePeriod(beginDate, endDate);
    }

    static ChargeRow generateChargeRow(){
        String groupName = String.valueOf(randomGenerator.nextInt(10000));
        String name = String.valueOf(randomGenerator.nextInt(10000000));
        AmountWithDoubles amountWithDoubles = new AmountWithDoubles(randomGenerator.nextDouble()
                , String.valueOf(randomGenerator.nextInt(100)));
        return new ChargeRow(groupName, name, amountWithDoubles);
    }

    public static void main(String[] args) {
//        GigaSpace gigaSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer("mySpace")).gigaSpace();
        GigaSpace gigaSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer("mySpace")).gigaSpace();
        feedData(gigaSpace, 1);

    }
}
