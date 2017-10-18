package com.lsh.payment.consumer.demo;

/**
 * Created by fuhao on 16/1/20.
 */
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DemoConsumer {
    public static void main(String[] args) {
        final String port = "8888";

        //测试Rest服务
//        getUser("http://localhost:" + port + "/services/users/1.json");
//        getUser("http://localhost:" + port + "/services/users/1.xml");

        //测试常规服务
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/*.xml");
        context.start();
//        UserService userService = context.getBean(UserService.class);
//        HoldRequest holdRequest = new HoldRequest();
//        holdRequest.setZoneCode("1000");
//        holdRequest.setChannel("1");
//        holdRequest.setHoldEndTime(1488654534L);
//        holdRequest.setSequence("12343430987");
//
//        context.getBean(IHoldRpcService.class).preHoldInventory(holdRequest);
//


    }

}
