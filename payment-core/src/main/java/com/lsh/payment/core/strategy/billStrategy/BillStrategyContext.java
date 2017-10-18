package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class BillStrategyContext {

    private Logger logger = LoggerFactory.getLogger(BillStrategyContext.class);

    private BillStrategy billStrategy;

    /**
     * @param payType 类型
     * @return boolean
     */
    public boolean downloadBill(String payType) {

        String yesterdayLine = DateUtil.getYesterdayLine();
        String yesterdayStr = DateUtil.getYesterdayStr();

        String billDate = null;

        String payName = null;

        int billType = 0;

        switch (payType) {

            case "WXH5":

                billDate = yesterdayStr;
                billType = BillTradeType.JSAPI.getCode();
                payName = PayChannel.WXPAY.getName();
                break;

            case "WXH5GROUPON":

                billDate = yesterdayStr;
                billType = BillTradeType.JSAPI.getCode();
                payName = PayChannel.WXPAYGROUPON.getName();
                break;

            case "WXAPP":

                billDate = yesterdayStr;
                billType = BillTradeType.APP.getCode();
                payName = PayChannel.WXPAY.getName();
                break;

            case "WXQF":

                billType = BillTradeType.WXNATIVE.getCode();
                billDate = yesterdayLine;
                payName = PayChannel.QFPAY.getName();
                break;

            case "ALIQF":

                billType = BillTradeType.ALINATIVE.getCode();
                billDate = yesterdayLine;
                payName = PayChannel.QFPAY.getName();
                break;

            case "ALI":

                billDate = yesterdayLine;
                payName = PayChannel.ALIPAY.getName();
                break;

            case "LAL":

                billDate = yesterdayStr;
                payName = PayChannel.LKLPAY.getName();
                break;

            case "WXXY":

                billType = BillTradeType.WXNATIVE.getCode();
                billDate = yesterdayStr;
                payName = PayChannel.XYPAY.getName();
                break;

            case "ALIXY":

                billType = BillTradeType.ALINATIVE.getCode();
                billDate = yesterdayStr;
                payName = PayChannel.XYPAY.getName();
                break;

            case "WXCMBC":

                billType = BillTradeType.WXNATIVE.getCode();
                billDate = yesterdayStr;
                payName = PayChannel.CMBCPAY.getName();
                break;

            case "ALICMBC":

                billType = BillTradeType.ALINATIVE.getCode();
                billDate = yesterdayStr;
                payName = PayChannel.CMBCPAY.getName();
                break;

            default:

                logger.info("分片参数有误");
        }

        if(StringUtils.isNotBlank(billDate)){

            billStrategy = BillStrategyFactory.getInstance().creator(payName);
            return billStrategy.downloadBillByDate(billDate, billType);
        }

        return false;
    }

}
