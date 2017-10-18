package com.lsh.payment.core.util.pay.qfpay;

import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.qfpay.QFData;
import com.lsh.payment.core.strategy.payVo.qfpay.QFQueryResponse;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QFCore {

    public static QFQueryResponse mkRsp(Map<String, String> callRsp) {
        QFQueryResponse response = new QFQueryResponse();
        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg("失败");
        String rspBody = callRsp.get("httpStr");
        String rspSign = callRsp.get("sign");
        if (QFSignature.check(rspBody, rspSign)) {
            JSONObject jsonObject = JSONObject.fromObject(rspBody);
            Map<String, Class> classMap = new HashMap<>();
            classMap.put("data", QFData.class);
            response = (QFQueryResponse) JSONObject.toBean(jsonObject, QFQueryResponse.class, classMap);
            //校验返回参数
            if (QFPayConfig.RESPCD_SUCCESS.equals(response.getRespcd())) {

                response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
                response.setMsg("成功");
            } else {

                response.setCode(response.getRespcd());
                response.setMsg(response.getResperr());
            }

        }

        return response;
    }
}
