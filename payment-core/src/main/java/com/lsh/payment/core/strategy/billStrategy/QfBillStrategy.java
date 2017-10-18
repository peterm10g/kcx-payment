package com.lsh.payment.core.strategy.billStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.base.common.utils.CollectionUtils;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payVo.qfpay.QFData;
import com.lsh.payment.core.strategy.payVo.qfpay.QFQueryResponse;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.PathUtil;
import com.lsh.payment.core.util.pay.qfpay.QFCore;
import com.lsh.payment.core.util.pay.qfpay.QFSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QfBillStrategy implements BillStrategy {

    private Logger logger = LoggerFactory.getLogger(QfBillStrategy.class);

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
            logger.info("钱方下载对账单开始-billType {}",billType);
            //区分微信扫码和支付宝扫码
            String payType;
            String billFileName;
            if (billType == BillTradeType.WXNATIVE.getCode()) {
                payType = QFPayConfig.PAY_TYPE_WX;
                billFileName = MessageFormat.format(WxPayConfig.WXSM_QF_BILL_NAME, billDate);
            } else if (billType == BillTradeType.ALINATIVE.getCode()) {
                payType = QFPayConfig.PAY_TYPE_ALI;
                billFileName = MessageFormat.format(WxPayConfig.ALISM_QF_BILL_NAME, billDate);
            } else {
                throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
            }
            //请求钱方
            List<QFData> dataList = handle(billDate, payType);

            //写文件
            writeFile(dataList, billFileName);
            logger.info("钱方下载对账单结束-billType {}",billType);
        } catch (Exception e) {
            logger.error("钱方下载对账单失败:", e);
            throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
        }
        return false;
    }


    /**
     * @param billDate 对账单日期
     * @return 返回对象
     */
    private List<QFData> handle(String billDate, String payType) {
        List<QFData> dataList = new ArrayList<>();
        //请求钱方
        String startTime = billDate + " 00:00:00";
        String endTime = billDate + " 23:59:59";

        for (int page = 1; ; page++) {
            //组织请求数据
            Map<String, Object> req = mkResp(startTime, endTime, page, payType);
            //请求钱方
            QFQueryResponse response = callQf(req);
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                logger.error("sleep fail",e);
            }
            //结果分析汇总
            boolean dataExistFlag = putList(response, dataList);

            if (!dataExistFlag) {
                break;
            }
        }

        return dataList;
    }

    /**
     * 组织请求数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      页码
     * @return 返回值
     */
    private Map<String, Object> mkResp(String startTime, String endTime, int page, String payType) {
        Map<String, Object> req = new HashMap<>();
        Map<String, Object> head = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        body.put("pay_type", payType);
        body.put("respcd", QFPayConfig.RESPCD_SUCCESS);
        body.put("start_time", startTime);
        body.put("end_time", endTime);
        body.put("page", page);
        body.put("page_size", QFPayConfig.QUERY_PAGE_SIZE);

        //签名
        String sign = QFSignature.getSign(body);
        head.put("X-QF-APPCODE", QFPayConfig.APP_CODE);
        head.put("X-QF-SIGN", sign);

        req.put("head", head);
        req.put("body", body);
        return req;
    }

    /**
     * 请求钱方,验证响应信息
     *
     * @param req 请求参数
     * @return QFQueryResponse
     */
    private QFQueryResponse callQf(Map<String, Object> req) {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        Map<String, String> head = (Map<String, String>) req.get("head");
        logger.info("请求钱方 head :" + head);
        logger.info("请求钱方 body :" + body);
        Map<String, String> callRsp = HttpClientUtils.doPostForQF(QFPayConfig.CHECK_API, body, head);
        logger.info("钱方响应:" + callRsp);
        //校验
        QFQueryResponse response = QFCore.mkRsp(callRsp);
        if (BusiConstant.PAY_REQUEST_SUCCESS.equals(response.getCode())) {
            return response;
        } else {
            logger.error("钱方返回数据失败:" + JSON.toJSONString(response));
            throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
        }
    }

    /**
     * 写文件
     *
     * @param dataList 查询出的数据列表
     * @param fileName 文件名称
     * @return 返回对象
     */
    private void writeFile(List<QFData> dataList, String fileName) {
        FileOutputStream outputStream = null;
        try {
            String path = BusiConstant.BILL_DOWNLOAD_PATH_QF + File.separator + DateUtil.getDateString();
            PathUtil.createPath(path);
            File file = new File(path + File.separator + fileName);
            outputStream = new FileOutputStream(file);
            //写文件头
            outputStream.write((FILEHEAD + System.getProperty("line.separator")).getBytes("UTF-8"));
            //写内容
            int sumCount = 0;
            BigDecimal sumAmount = new BigDecimal("0");
            StringBuffer sLines = new StringBuffer();
            for (QFData data : dataList) {
                StringBuffer sLine = new StringBuffer();
                sLine.append(data.getPay_type()).append(LINE_SEPARATOR);
                sLine.append(data.getSysdtm()).append(LINE_SEPARATOR);
                sLine.append(data.getOrder_type()).append(LINE_SEPARATOR);
                sLine.append(data.getTxcurrcd()).append(LINE_SEPARATOR);
                sLine.append(data.getTxdtm()).append(LINE_SEPARATOR);
                sLine.append(data.getTxamt()).append(LINE_SEPARATOR);
                sLine.append(data.getOut_trade_no()).append(LINE_SEPARATOR);
                sLine.append(data.getSyssn()).append(LINE_SEPARATOR);
                sLine.append(data.getCancel()).append(LINE_SEPARATOR);
                sLine.append(data.getRespcd()).append(LINE_SEPARATOR);
                sLine.append(data.getErrmsg());
                sLines.append(sLine).append(System.getProperty("line.separator"));

                sumCount++;
                sumAmount = sumAmount.add(BigDecimal.valueOf(Long.parseLong(data.getTxamt())));
            }
            outputStream.write(sLines.toString().getBytes("UTF-8"));

            //写文件尾
            String fileE = FILEEND + System.getProperty("line.separator") + sumCount + LINE_SEPARATOR + sumAmount + System.getProperty("line.separator");
            outputStream.write(fileE.getBytes("UTF-8"));

        } catch (Exception e) {
            logger.error("write file fail", e);
            throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * putList
     *
     * @param response 文件内容
     * @param dataList 查询出的数据列表
     * @return 返回对象
     */
    private boolean putList(QFQueryResponse response, List<QFData> dataList) {
        boolean dataExistFlag = false;
        List<QFData> datas = response.getData();
        if (CollectionUtils.isEmpty(datas)) {
            return dataExistFlag;
        }
        dataList.addAll(datas);
        dataExistFlag = true;
        return dataExistFlag;
    }
}
