package com.lsh.payment.worker.service;

import com.lsh.payment.core.service.payment.impl.PayDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class PayDealHistoryService {

    private static Logger logger = LoggerFactory.getLogger(PayDealHistoryService.class);

    private static String deleteTrue = "1";

    @Autowired
    private PayDealService payDealService;

    @Value("${history.time}")
    private Long time;

    @Value("${history.delete}")
    private String deleteFlag;

    @Value("${history.pageSize}")
    private int pageSize;

    /**
     *
     */
    public void payDeal2History(){

        Date date = this.time2Date(time);

        int historyCount = payDealService.historyCount(date);

        logger.info("数据总量是 : " + historyCount);

        int start = 0;
        int page = 1;

        while (((page - 1) * pageSize) < historyCount) {

            logger.info(" 第 (" + page + ") 批数据同步, " + "start is : " + start);

            Map<String,Object> params = new HashMap<>();

            params.put("date",date);
            params.put("pageSize",pageSize);
            params.put("start",start);

            payDealService.payDeal2History(params);

            start = page * pageSize;
            page++;
        }

        if(deleteFlag.equals(deleteTrue)){
            payDealService.delete2History(Collections.<String,Object>singletonMap("date",date));
        }

    }


    private Date time2Date(long time){

        logger.info("deleteFlag is " + (deleteFlag.equals(deleteTrue)));

        Date date = new Date(time);
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        logger.info("Format To String(Date): " + format.format(date));

        return date;
    }

}
