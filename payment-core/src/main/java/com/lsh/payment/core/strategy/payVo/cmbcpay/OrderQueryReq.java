package com.lsh.payment.core.strategy.payVo.cmbcpay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderQueryReq {

	private String merNo;
	private String orderNo;
	private String termNo;
	private String orgReqId;
	private String orgTransId;
	private String operatorId;
	private String signIn;
}
