package com.ai.iot;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ValidAcct {

    private static Set<Long> acctSet = new HashSet<>();
    private static AtomicInteger iniTag = new AtomicInteger(0);

    private static void init() {

        if(iniTag.compareAndSet(0,1)){
            acctSet.add(3100000000000004L);
            acctSet.add(3100000000000007L);
            acctSet.add(3100000000000025L);
            acctSet.add(3100000000000029L);
            acctSet.add(3100000000000043L);
            acctSet.add(3100000000000065L);
            acctSet.add(3100000000000080L);
            acctSet.add(3100000000000081L);
            acctSet.add(3100000000000091L);
            acctSet.add(3100000000000105L);
            acctSet.add(3100000000000120L);
            acctSet.add(3100000000000126L);
            acctSet.add(3100000000000127L);
            acctSet.add(3100000000000128L);
            acctSet.add(3100000000000129L);
            acctSet.add(3100000000000141L);
            acctSet.add(3100000000000153L);
            acctSet.add(3100000000000160L);
            acctSet.add(3100000000000217L);
            acctSet.add(3100000000000230L);
            acctSet.add(3100000000000255L);
            acctSet.add(3100000000000277L);
            acctSet.add(3100000000000284L);
            acctSet.add(3100000000000285L);
            acctSet.add(3100000000000448L);
            acctSet.add(3100000000000643L);
            acctSet.add(3100000000000709L);
            acctSet.add(3100000000000777L);

            iniTag.set(3);
        }

        try {
            if(iniTag.get() !=3){
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static boolean isValid(long acctId){
        init();
        return  acctSet.contains(acctId);
    }

}
