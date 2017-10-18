package com.lsh.payment.core.util.pay.weChatpay;

import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.payVo.wxpay.WxBasePayResponse;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/28
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.util.pay.weChatpay
 * desc:
 */
public class WeChatCore {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(WeChatCore.class);

    /**
     *
     * @param json
     * @param response
     * @param key
     * @return
     */
    public static WxBasePayResponse mkResponse(JSONObject json, WxBasePayResponse response, String key) {

        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg("第三方查询请求失败");

        //return_code 通信标识，非交易标识，交易是否成功需要查看trade_state来判断
        if(WxPayConfig.SUCCESS_CODE.equals(response.getReturn_code())){
            //业务结果	result_code
            if(WxPayConfig.SUCCESS_CODE.equals(response.getResult_code())){
                //验签
                Map<String, String> payResponeMap = json;
                if (WxSignature.getNewSign(payResponeMap, key).equals(response.getSign())) {
                    response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
                    response.setMsg("第三方查询请求签名验证成功");
                }else{
                    logger.info("支付结果查询验签失败");
                }
            }else{
                logger.info("支付结果查询业务异常 {} " ,response.getResult_code());
            }
        }else{
            logger.info("支付结果查询通信异常 {} " ,response.getReturn_code() + " : " + response.getReturn_msg());
        }

        return response;
    }

}
