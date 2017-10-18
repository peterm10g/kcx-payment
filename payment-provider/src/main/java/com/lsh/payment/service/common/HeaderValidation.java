package com.lsh.payment.service.common;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Project Name: lsh-payment
 * Created by zhangqiang on 16/7/4.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class HeaderValidation {

    private static Logger logger = LoggerFactory.getLogger(HeaderValidation.class);


    public static boolean validateHeader(HttpServletRequest request){
        if(request == null){
            return false;
        }
        String apiversion = request.getHeader("api-version");
        String random = request.getHeader("random");
        String secretType = request.getHeader("secret-type");
        String sign = request.getHeader("sign");
        String platform = request.getHeader("platform");

        logger.info("apiversion:"+apiversion+" random:"+random+" secretType:"+secretType+" sign:"+sign+" platform:"+ platform);

        if(StringUtils.isAnyBlank(apiversion,random,platform)){
            return false;
        }
        /*
        if(StringUtils.isNotBlank(secretType) && StringUtils.isBlank(sign)){
            return false;
        }*/

        return true;
    }
}
