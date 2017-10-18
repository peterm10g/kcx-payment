package com.lsh.payment.core.strategy.billStrategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.PathUtil;
import org.apache.commons.lang.StringUtils;
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
public class AliBillStrategy implements BillStrategy {

    private static Logger logger = LoggerFactory.getLogger(AliBillStrategy.class);

    private static AlipayClient alipayClient = new DefaultAlipayClient(AliPayConfig.ALIPAY_DOWNLOAD_API, AliPayConfig.app_id, AliPayConfig.private_key, "json", "UTF-8", AliPayConfig.alipay_public_key);

    /**
     * 下载对账单
     *
     * @param billDate 查询参数
     * @param billType
     * @return
     */
    @Override
    public boolean downloadBillByDate(String billDate, int billType) {

        if (StringUtils.isBlank(billDate)) {

            return false;
        }
        String downLoadPath = BusiConstant.BILL_DOWNLOAD_PATH_ALI + File.separator + DateUtil.getDateString();
        PathUtil.createPath(downLoadPath);

        AlipayDataDataserviceBillDownloadurlQueryRequest alipayQueryrequest = new AlipayDataDataserviceBillDownloadurlQueryRequest();

        Map<String, String> paraMap = new HashMap<>(5);
        paraMap.put("bill_type", AliPayConfig.DOWNLOAD_BILL_TYPE);
        paraMap.put("bill_date", billDate);

        alipayQueryrequest.setBizContent(JSON.toJSONString(paraMap));
        AlipayDataDataserviceBillDownloadurlQueryResponse alipayBillQueryresponse;

        try {
            alipayBillQueryresponse = alipayClient.execute(alipayQueryrequest);

            if (alipayBillQueryresponse.isSuccess()) {
                logger.info("调用下载支付宝对账单成功");
                String alipayBillQueryresponseBody = alipayBillQueryresponse.getBody().toString();
                logger.info("alipay downLoad bill response.getBody() is " + alipayBillQueryresponseBody);

                JSONObject downloadBillResponseJson = JSON.parseObject(alipayBillQueryresponseBody);
                JSONObject billDownloadurlQuery = downloadBillResponseJson.getJSONObject(BusiConstant.ALIPAY_RESPONSE);

                String billDownloadUrl = null;
                if (billDownloadurlQuery.getString("code").equals(BusiConstant.ALIPAY_CODE_SUCCESS)) {
                    billDownloadUrl = billDownloadurlQuery.getString(BusiConstant.DOWNLOAD_BILL_URL);
                }

                if (StringUtils.isBlank(billDownloadUrl)) {
                    logger.info("billDownloadUrl is null or checkRSASign is false");
                    return false;
                }

                String billZipName = MessageFormat.format(AliPayConfig.ALI_BILL_ZIP_NAME, billDate);

                logger.info(" ************* download bill start");
                HttpClientUtils.doPostAliBill(billDownloadUrl, downLoadPath, billZipName);
                logger.info(" ************* download bill end ");

            } else {
                logger.info("调用下载支付宝对账单失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ExceptionStatus.E2004004.getCode(), ExceptionStatus.E2004004.getMessage());
        }

        return false;
    }
}
