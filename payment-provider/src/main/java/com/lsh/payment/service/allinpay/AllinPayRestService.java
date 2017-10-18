package com.lsh.payment.service.allinpay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.service.allinpay.IAllinPayRestService;
import com.lsh.payment.core.service.allinpay.IAllinPayNotifyService;
import com.lsh.payment.core.strategy.config.AllinPayConfig;
import com.lsh.payment.core.util.pay.allinpay.SybUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;
import java.util.TreeMap;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/9/22.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest")
@Path("allinpay")
@Produces({ContentType.TEXT_PLAIN_UTF_8})
@Slf4j
public class AllinPayRestService implements IAllinPayRestService {

    @Autowired
    private IAllinPayNotifyService allinPayNotifyService;


    @Path("notify")
    @POST
    @Override
    public String AllinPayNotify() {

        HttpServletRequest request = (HttpServletRequest) RpcContext.getContext().getRequest();

        String allinPayResponse;
        try {
            request.setCharacterEncoding("gbk");//通知传输的编码为GBK

            TreeMap<String,String> params = getParams(request);//动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容

            log.info("params is = " + JSON.toJSONString(params));

            boolean isSign = SybUtil.validSign(params, AllinPayConfig.KEY);// 接受到推送通知,首先验签
            log.info("验签结果:"+isSign);

            allinPayNotifyService.allinNotify(params);

            allinPayResponse = "success";
            //验签完毕进行业务处理
        } catch (Exception e) {//处理异常
            e.printStackTrace();
            allinPayResponse = "fail";
        } finally{//收到通知,返回success

        }

        return allinPayResponse;
    }

    /**
     * 动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容由于收银宝加字段而引起的签名异常
     * @param request
     * @return
     */
    private TreeMap<String, String> getParams(HttpServletRequest request){
        TreeMap<String, String> map = new TreeMap<>();
        Map reqMap = request.getParameterMap();
        for(Object key:reqMap.keySet()){
            String value = ((String[])reqMap.get(key))[0];
            log.info(key+";"+value);
            map.put(key.toString(),value);
        }
        return map;
    }

}
