package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.strategy.payVo.alipay.AliPrePayResponse;
import com.lsh.payment.core.util.pay.alipay.AlipaySubmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**  
 *  支付宝app支付 
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AlipayStrategy implements PayStrategy<AliPrePayResponse> {

    private static Logger logger = LoggerFactory.getLogger(AlipayStrategy.class);

    @Override
    public AliPrePayResponse generatePayParams(int payType,final Map<String, String> paramMap) {
//        AliPrePayResponse aliPrePayResponse = new AliPrePayResponse();

        //APP和H5参数不一致
        if (paramMap.get("payWay").equals(PayWay.IOS.getName()) || paramMap.get("payWay").equals(PayWay.ANDROID.getName())) {

            return alipayApp_old(paramMap);
        } else if (paramMap.get("payWay").equals(PayWay.H5.getName())) {

            return alipayH5(paramMap);
        } else {
            logger.info("pay_way para is : " + paramMap.get("payWay"));
            return null;
        }

//        return aliPrePayResponse;
    }


    private AliPrePayResponse alipayH5(final Map<String, String> paramMap){
        AliPrePayResponse aliPrePayResponse = new AliPrePayResponse();

        //组装报文
        Map<String, String> alipayBodyMap = new HashMap<>(15);
        alipayBodyMap.put("service", AliPayConfig.service);
        alipayBodyMap.put("partner", AliPayConfig.partner);
        alipayBodyMap.put("_input_charset", AliPayConfig.input_charset);
        alipayBodyMap.put("notify_url", AliPayConfig.notify_url);
        alipayBodyMap.put("return_url", paramMap.get("returnUrl"));
        alipayBodyMap.put("out_trade_no", paramMap.get("payPaymentNo"));
        alipayBodyMap.put("subject", AliPayConfig.GOODS_NAME);
        alipayBodyMap.put("total_fee", paramMap.get("payAmount"));
        alipayBodyMap.put("seller_id", AliPayConfig.seller_id);
        alipayBodyMap.put("payment_type", AliPayConfig.payment_type);
        //构造请求(签名)
        String alipayRequestUrl ;
        try {
            logger.info("支付宝下单参数 {}",JSON.toJSONString(alipayBodyMap));
            alipayRequestUrl = AlipaySubmit.buildRequestUrl(AliPayConfig.ALIPAY_GATEWAY_NEW, alipayBodyMap);
        } catch (Exception e) {
            logger.error("支付宝 payPaymentNo is : " + paramMap.get("payPaymentNo") + "支付失败", e);
            aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
            aliPrePayResponse.setMsg("失败");
            return aliPrePayResponse;
        }
        aliPrePayResponse.setResult(alipayRequestUrl);
        aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
        aliPrePayResponse.setMsg("成功");
        logger.info("支付宝支付返回:" + JSON.toJSONString(aliPrePayResponse));

        return aliPrePayResponse;

    }

    /**
     *
     * @param paramMap
     * @return
     */
    private AliPrePayResponse alipayApp_old(final Map<String, String> paramMap){
        AliPrePayResponse aliPrePayResponse = new AliPrePayResponse();

        //组装报文
        Map<String, String> alipayBodyMap = new HashMap<>(15);
        alipayBodyMap.put("service", AliPayConfig.service);
        alipayBodyMap.put("partner", AliPayConfig.partner);
        alipayBodyMap.put("_input_charset", AliPayConfig.input_charset);
        alipayBodyMap.put("notify_url", AliPayConfig.notify_url);
        alipayBodyMap.put("return_url", paramMap.get("returnUrl"));
        alipayBodyMap.put("out_trade_no", paramMap.get("payPaymentNo"));
        alipayBodyMap.put("subject", MessageFormat.format(AliPayConfig.GOODS_APP_NAME,paramMap.get("payPaymentNo")));
        alipayBodyMap.put("body", AliPayConfig.GOODS_APP_DETAIL);
        alipayBodyMap.put("total_fee", paramMap.get("payAmount"));
        alipayBodyMap.put("seller_id", AliPayConfig.seller_id_new);
        alipayBodyMap.put("payment_type", AliPayConfig.payment_type);
//        alipayBodyMap.put("it_b_pay",AliPayConfig.it_b_pay);
        //构造请求(签名)
        String alipayRequestUrl ;
        try {
            logger.info("支付宝下单参数 {}",JSON.toJSONString(alipayBodyMap));
            alipayRequestUrl = AlipaySubmit.buildRequestUrl(alipayBodyMap);

        } catch (Exception e) {
            logger.error("支付宝 payPaymentNo is : " + paramMap.get("payPaymentNo") + "支付失败", e);
            aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
            aliPrePayResponse.setMsg("失败");
            return aliPrePayResponse;
        }
        aliPrePayResponse.setResult(alipayRequestUrl);
        aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
        aliPrePayResponse.setMsg("成功");
        logger.info("支付宝支付返回:" + JSON.toJSONString(aliPrePayResponse));

        return aliPrePayResponse;

    }

    /**
     *
     * @param paramMap
     * @return
     */
    private static AliPrePayResponse alipayApp_new(final Map<String, String> paramMap){
        AliPrePayResponse aliPrePayResponse = new AliPrePayResponse();

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AliPayConfig.app_id, AliPayConfig.private_key, "json", AliPayConfig.input_charset, AliPayConfig.public_key, AliPayConfig.sign_type);

        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(AliPayConfig.GOODS_APP_DETAIL);
        model.setSubject(MessageFormat.format(AliPayConfig.GOODS_APP_NAME,paramMap.get("payPaymentNo")));
        model.setOutTradeNo(paramMap.get("payPaymentNo"));
        model.setTimeoutExpress(AliPayConfig.TIMEOUT_EXPRESS);
        model.setTotalAmount(paramMap.get("payAmount"));
        model.setProductCode(AliPayConfig.PRODUCT_CODE);

//        model.setSellerId(AliPayConfig.seller_id);

        request.setBizModel(model);
        request.setNotifyUrl(AliPayConfig.notify_url);
        //构造请求(签名)
        String alipayRequestUrl ;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);

            System.out.println(response.isSuccess() + "," + response.getCode() + "," + response.getMsg());
//            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
            if(response.isSuccess()){
                alipayRequestUrl = response.getBody();
            }else{
               throw new AlipayApiException("alipay exception");
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
            aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
            aliPrePayResponse.setMsg("失败");
            return aliPrePayResponse;
        }

        aliPrePayResponse.setResult(alipayRequestUrl);
        aliPrePayResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
        aliPrePayResponse.setMsg("成功");
        logger.info("支付宝支付返回:" + JSON.toJSONString(aliPrePayResponse));

        return aliPrePayResponse;

    }

//    public static void main(String[] args) {
//
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("payPaymentNo","2017031600010001");
//        paramMap.put("payAmount","0.02");
//
//        alipayApp(paramMap);
//
//
//    }



}
