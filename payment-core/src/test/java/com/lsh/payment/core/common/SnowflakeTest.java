package com.lsh.payment.core.common;

import com.lsh.payment.core.util.IdWork.RandomUtil;
import com.lsh.payment.core.util.IdWork.SnowflakeId;

import java.util.HashSet;
import java.util.Set;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class SnowflakeTest {

    static class IdWorkThread implements Runnable {
        private Set<Long> set;
        private SnowflakeId snowflakeId;

        public IdWorkThread(Set<Long> set, SnowflakeId snowflakeId) {
            this.set = set;
            this.snowflakeId = snowflakeId;
        }

        @Override
        public void run() {
            while (true) {
                long id = snowflakeId.nextId();
                if (!set.add(id)) {
                    System.out.println("duplicate:" + id);
                }
            }
        }
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
//        final IdWorker idWorker1 = new IdWorker(0, 0);
//        final IdWorker idWorker2 = new IdWorker(1, 0);
//        Thread t1 = new Thread(new IdWorkThread(set, idWorker1));
//        Thread t2 = new Thread(new IdWorkThread(set, idWorker2));
//        t1.setDaemon(true);
//        t2.setDaemon(true);
//        t1.start();
//        t2.start();
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        SnowflakeId snowflakeId = new SnowflakeId(1, 0);

        for (int i = 0; i < 10000; i++) {
            String id = RandomUtil.snowFlakeId();
            set.add(id);
        }

        System.out.println("set size is : " + set.size());
    }
}
