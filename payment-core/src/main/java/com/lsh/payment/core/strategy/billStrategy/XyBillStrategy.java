package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.util.*;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
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
public class XyBillStrategy implements BillStrategy {

    String prefix = "【兴业下载对账单】";

    private Logger logger = LoggerFactory.getLogger(XyBillStrategy.class);

    private final String FILEHEAD = "支付类型,系统交易时间,订单类型,币种,请求交易时间,订单支付金额(单位分),外部订单号,钱方订单号,撤销/退款标记,交易结果码,交易结果描述";

    private final String FILEEND = "总笔数,总金额";

    private final String LINE_SEPARATOR = ",";

    /**
     * 下载对账单
     *
     * @param billDate 查询参数
     * @param billType 对账单类型
     * @return boolean
     */
    @Override
    public boolean downloadBillByDate(String billDate, int billType) {

        try {
            logger.info("{}开始",prefix);
            //区分微信扫码和支付宝扫码
//            String payType;
            String billFileName;
            if (billType == BillTradeType.WXNATIVE.getCode()) {
//                payType = XYPayConfig.PAY_TYPE_WX;
                billFileName = MessageFormat.format(WxPayConfig.WXSM_XY_BILL_NAME, billDate);
            } else if (billType == BillTradeType.ALINATIVE.getCode()) {
//                payType = XYPayConfig.PAY_TYPE_ALI;
                billFileName = MessageFormat.format(WxPayConfig.ALISM_XY_BILL_NAME, billDate);
            } else {
                throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
            }
            //请求
//            List<QFData> dataList = handle(billDate, payType);
            this.postRequest(billDate,billFileName);

            logger.info("{}结束",prefix);
        } catch (Exception e) {
            logger.error("{}失败:{}", prefix,e);
            throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
        }
        return false;
    }

    private void postRequest(String billDate,String fileName){

        Map<String, String> billRequestBody = new HashMap<>(18);
        billRequestBody.put("service",XYPayConfig.Bill_SERVICE);
        billRequestBody.put("bill_date",billDate);
        billRequestBody.put("bill_type",XYPayConfig.Bill_TYPE);
        billRequestBody.put("mch_id",XYPayConfig.MAC_ID);
        billRequestBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串
        billRequestBody.put("sign", WxSignature.getNewSign(billRequestBody, XYPayConfig.KEY));

        String billXml = XmlUtil.toXml(billRequestBody);

        String path = BusiConstant.BILL_DOWNLOAD_PATH_XY + File.separator + DateUtil.getDateString();
        PathUtil.createPath(path);

        HttpClientUtils.doPostXmlXYWX(XYPayConfig.BILL_API,billXml,path,fileName);
    }



}
