package com.lsh.payment.core.service.alipay.impl;

import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.IPayQueryChannelService;
import com.lsh.payment.core.service.payment.builder.AliQueryBuilder;
import com.lsh.payment.core.service.payment.builder.QueryDirector;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.queryStrategy.QueryStrategyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class AliQueryServiceImpl implements IPayQueryChannelService {

    private final Logger logger = LoggerFactory.getLogger(AliQueryServiceImpl.class);

    @Autowired
    private PayBaseService payBaseService;

    /**
     * 统一查询接口
     * @param payDeal 查询支付记录对象
     * @return        查询结果对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryContent query(PayDeal payDeal) {

        Map<String, Object> params = Collections.<String,Object>singletonMap("out_trade_no", payDeal.getPayPaymentNo());
        QueryStrategyContext context = new QueryStrategyContext();
        BasePayResponse basePayResponse = context.queryPayStatusByParams(BusiConstant.ALIQUERY, params);

        AliQueryBuilder aliQueryBuilder = new AliQueryBuilder();
        QueryDirector director = new QueryDirector(aliQueryBuilder);
        director.queryParse(basePayResponse,payDeal);

        int payCode = aliQueryBuilder.getResult().getPayCode();

        logger.info("AliQueryServiceImpl payDeal is [" + payDeal.getPayPaymentNo() + "], PayStatus paycode is " + payCode);

        if (payCode !=PayStatus.CREATE_ERROR.getValue() && payCode != PayStatus.PAYING.getValue()){
            payBaseService.updPayDeal(aliQueryBuilder.getUpdatePaydeal(),payDeal);
        }

        return aliQueryBuilder.getResult();
    }


}
