package com.lsh.payment.core.util.IdWork;

import com.lsh.payment.core.util.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;


/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class RandomUtil {

    public static final String numberChar = "0123456789";
    public static final String numberCharNum = "123456789";

    public static final SnowflakeId snowflakeId;
    private static Logger logger = LoggerFactory.getLogger(RandomUtil.class);

    static {
        String machine = System.getenv("PAY_MACHINE");
        logger.info("machine:" + machine);
        int workerId;
        try {
            workerId = StringUtils.isBlank(machine) ? 0 : Integer.parseInt(machine);
        } catch (Exception e) {
            logger.error("error:", e);
            workerId = 0;
        }
        logger.info("workerId:" + workerId);
        snowflakeId = new SnowflakeId(workerId, 0);
    }

    public static String getRandomString(int length, int type) { //length表示生成字符串的长度

        String base = numberCharNum;
        if (type == 1) {
            base = numberCharNum;
        }
        if (type == 2) {
            base = numberChar;
        }
        return getRandomString(base, length);
    }

    public static String getRandomString(String base, int length) { //length表示生成字符串的长度

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成支付订单号,支付流水号
     *
     * @return
     */
    public static String getPayNoStr() {
        return getRandomString(2, 1) + System.currentTimeMillis() + getRandomString(3, 2);
    }

    public static String getPayNoStrUtil() {
        return DateUtil.defaultTimestampStr(new Date()) + RandomStringUtils.randomNumeric(5);
    }

    public static String snowFlakeId() {
        return String.valueOf(snowflakeId.nextId());
    }

    public static Long key() {
        return snowflakeId.nextId();
    }
}