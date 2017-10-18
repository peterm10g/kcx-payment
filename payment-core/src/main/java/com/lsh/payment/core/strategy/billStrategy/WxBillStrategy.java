package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.MD5Util;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxBillStrategy implements BillStrategy {

    private Logger logger = LoggerFactory.getLogger(WxBillStrategy.class);

//    public static String url = "https://api.mch.weixin.qq.com/pay/downloadbill";

    /**
     * 下载对账单
     *
     * @param billDate 查询参数
     * @param wxType   对账单类型
     * @return boolean
     */
    @Override
    public boolean downloadBillByDate(String billDate, int wxType) {
        try {
            String wxpayKey = "";
            String billFileName = "";
            String downLoadBillPath = BusiConstant.BILL_DOWNLOAD_PATH_WX + File.separator + DateUtil.getDateString();
            PathUtil.createPath(downLoadBillPath);

            Map<String, String> wxPayRequestBody = new HashMap<>(10);
            if (BillTradeType.JSAPI.getCode() == wxType) {
                //微信公众号
                logger.info("wx bill downLoad type is : " + BillTradeType.JSAPI.getName());
                wxPayRequestBody.put("appid", WxPayConfig.APPID);// 应用ID
                wxPayRequestBody.put("mch_id", WxPayConfig.MCHID);// 商户号
                wxpayKey = WxPayConfig.KEY;
                billFileName = MessageFormat.format(WxPayConfig.WX_H5_BILL_NAME, billDate);

            } else if (BillTradeType.APP.getCode() == wxType) {
                //开放平台
                logger.info("wx bill downLoad type is : " + BillTradeType.APP.getName());
                wxPayRequestBody.put("appid", WxPayConfig.APPID_APP);// 应用ID
                wxPayRequestBody.put("mch_id", WxPayConfig.MCHID_APP);// 商户号
                wxpayKey = WxPayConfig.KEY_APP;
                billFileName = MessageFormat.format(WxPayConfig.WX_APP_BILL_NAME, billDate);
            }

            wxPayRequestBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串
            wxPayRequestBody.put("bill_date", billDate);// 随机字符串
            wxPayRequestBody.put("bill_type", "SUCCESS");
            //设置签名
            wxPayRequestBody.put("sign", WxSignature.getNewSign(wxPayRequestBody, wxpayKey));
            //请求xml字符串
            String wxPayRequestXml = XmlUtil.toXml(wxPayRequestBody);
            String wxPayResponseXml = HttpClientUtils.doPostXmlBill(WxPayConfig.WX_BILL_DOWNLOAD_URL, wxPayRequestXml, downLoadBillPath, billFileName);

            logger.info("wx bill downLoad wxPayResponseXml is : " + wxPayResponseXml);

        } catch (Throwable e) {

            if (wxType == BillTradeType.JSAPI.getCode()) {
                throw new BusinessException(ExceptionStatus.E2004001.getCode(), ExceptionStatus.E2004001.getMessage());
            } else if (wxType == BillTradeType.APP.getCode()) {
                throw new BusinessException(ExceptionStatus.E2004002.getCode(), ExceptionStatus.E2004002.getMessage());
            }

            e.printStackTrace();
        }
        
        return true;
    }
}
