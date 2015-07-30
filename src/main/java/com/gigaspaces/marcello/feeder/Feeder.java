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

    void feedData(GigaSpace gigaSpace, final int amount, final int batchSize){
        TelephoneNumber telephoneNumber;
        SubscriberChargeData subscriberChargeData;
        List<SubscriberChargeData> objects;
        for(int j = 0, i = 0;j < amount/batchSize; j++) {
            objects = new ArrayList<>();
            for (; i < j*batchSize+batchSize; i++) {
                int rndTelephoneNumber = randomGenerator.nextInt(100);
                int billSequence = randomGenerator.nextInt(1000000);
                telephoneNumber = new TelephoneNumber(String.valueOf(rndTelephoneNumber), String.valueOf(rndTelephoneNumber));
                subscriberChargeData = new SubscriberChargeData(String.valueOf(i), billSequence, telephoneNumber, generateTimePeriod());
                List<ChargeRow> rows = new ArrayList<ChargeRow>();
                for (int k = 0; k < 13; k++) {
                    rows.add(generateChargeRow());
                }
                subscriberChargeData.setChargeRows(rows);

                List<ChargeRow> rowTotals = new ArrayList<ChargeRow>();
                rowTotals.add(generateChargeRow());
                subscriberChargeData.setChargeGroupTotals(rowTotals);
                objects.add(subscriberChargeData);
            }
            gigaSpace.writeMultiple(((List<SubscriberChargeData>)objects).toArray(new SubscriberChargeData[objects.size()]));
        }
    }

    TimePeriod generateTimePeriod(){
        long beginTime = Timestamp.valueOf("2000-01-01 00:00:00").getTime();
        long endTime = Timestamp.valueOf("2015-12-31 00:58:00").getTime();
        long diff = endTime - beginTime + 1;

        Date beginDate = new Date(beginTime + (long) (Math.random() * diff));
        Date endDate = new Date(beginDate.getTime() + (long) (Math.random() * diff));
        return new TimePeriod(beginDate, endDate);
    }

    ChargeRow generateChargeRow(){
        String groupName = String.valueOf(randomGenerator.nextInt(10000));
        String name = String.valueOf(randomGenerator.nextInt(10000000));
        AmountWithDoubles amountWithDoubles = new AmountWithDoubles(randomGenerator.nextDouble()
                , String.valueOf(randomGenerator.nextInt(100)));
        return new ChargeRow(groupName, name, amountWithDoubles);
    }

    SubscriberChargeData[] qury(){
        return null;
    };

    public static void main(String[] args) {
//        GigaSpace gigaSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer("mySpace")).gigaSpace();
        GigaSpace gigaSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer("mySpace").lookupGroups("")).gigaSpace();

        Feeder feeder = new Feeder();
        feeder.feedData(gigaSpace, 1000, 100);




    }
}
