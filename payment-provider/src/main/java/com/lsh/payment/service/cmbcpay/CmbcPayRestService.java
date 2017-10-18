package com.lsh.payment.service.cmbcpay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.service.cmbcpay.ICMBCPayRestService;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.util.pay.cmbcpay.Md5SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Map;
import java.util.TreeMap;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/9/7.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest")
@Path("cmbcpay")
@Slf4j
public class CmbcPayRestService implements ICMBCPayRestService {


    @Override
    @POST
    @Path("notify")
    public String xyNotify(@Context HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        String notifyResponse = "";
        log.info("requestMap is " + JSON.toJSONString(requestMap));

        try {
            TreeMap<String, String> params = getParams(request);

            if (checkPass(params)) {

            }
        } catch (BusinessException e) {

            notifyResponse = "fail";
            log.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Throwable e) {

            notifyResponse = "fail";
            log.error("服务端异常", e);
        }

        return notifyResponse;
    }


    private boolean checkPass(TreeMap<String, String> params) {
        String sign = Md5SignUtil.md5Sign(params, CMBCPayConfig.KEY);

        return sign.equals(params.get("signIn"));
    }

    /**
     * 动态遍历获取所有收到的参数,此步非常关键,因为收银宝以后可能会加字段,动态获取可以兼容由于收银宝加字段而引起的签名异常
     *
     * @param request
     * @return
     */
    private TreeMap<String, String> getParams(HttpServletRequest request) {
        TreeMap<String, String> map = new TreeMap<>();
        Map reqMap = request.getParameterMap();
//        String desc = request.getParameter("desc");
        for (Object key : reqMap.keySet()) {
            String value = ((String[]) reqMap.get(key))[0];
            log.info(key + " = " + value);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            map.put(key.toString(), value);
        }

        return map;
    }

}
