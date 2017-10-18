package com.lsh.payment.core.service.AsyncService;

import com.google.common.eventbus.AsyncEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/22
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.AsyncService
 * desc:异步处理类
 */
@Component
@Lazy(value = false)
public class AsyncEvent {

    private static AsyncEventBus asyncEventBus;

    @Autowired
    private AsyncEventListener asyncEventListener;

    @Value("${async.thread.num}")
    private int threadNum;

    @PostConstruct
    public void init() {
        asyncEventBus = new AsyncEventBus(Executors.newFixedThreadPool(threadNum));
        asyncEventBus.register(asyncEventListener);
    }

    public static void post(Object obj){
        asyncEventBus.post(obj);
    }
}
