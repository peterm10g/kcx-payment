package com.lsh.payment.core.strategy.billStrategy;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface BillStrategy {

    /**
     * 下载对账单
     * @param billDate 查询参数
     * @return
     */
     boolean downloadBillByDate(String billDate, int billType);
}
