package com.lsh.payment.core.strategy.payVo.xypay;


import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class XypayResponse extends BasePayResponse {

   private XyPayUnifiedOrderResult xyPayUnifiedOrderResult;

   public XyPayUnifiedOrderResult getXyPayUnifiedOrderResult() {
      return xyPayUnifiedOrderResult;
   }

   public void setXyPayUnifiedOrderResult(XyPayUnifiedOrderResult xyPayUnifiedOrderResult) {
      this.xyPayUnifiedOrderResult = xyPayUnifiedOrderResult;
   }
}