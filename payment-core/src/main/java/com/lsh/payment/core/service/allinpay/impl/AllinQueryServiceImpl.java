package com.lsh.payment.core.service.allinpay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.IPayQueryChannelService;
import com.lsh.payment.core.service.payment.builder.CmbcQueryBuilder;
import com.lsh.payment.core.service.payment.builder.QueryDirector;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayQueryBaseService;
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
public class AllinQueryServiceImpl implements IPayQueryChannelService {

    private static Logger logger = LoggerFactory.getLogger(PayQueryBaseService.class);

    @Autowired
    private PayBaseService payBaseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryContent query(PayDeal payDeal) {

        logger.info("通联查询开始 {}" , JSON.toJSONString(payDeal));

        Map<String, Object> params = Collections.<String, Object>singletonMap("channelId", payDeal.getChannelTransaction());
        QueryStrategyContext context = new QueryStrategyContext();
        BasePayResponse basePayResponse = context.queryPayStatusByParams(BusiConstant.ALLINQUERY, params);

        CmbcQueryBuilder cmbcQueryBuilder = new CmbcQueryBuilder();
        QueryDirector director = new QueryDirector(cmbcQueryBuilder);
        director.queryParse(basePayResponse, payDeal);

        QueryContent content = cmbcQueryBuilder.getResult();
        if (content.getPayCode() != null) {
            int payCode = content.getPayCode();
            if (payCode == PayStatus.PAY_SUCCESS.getValue()) {
                payBaseService.updPayDeal(cmbcQueryBuilder.getUpdatePaydeal(), payDeal);
            }
        }
        logger.info("通联查询结果 {}" , JSON.toJSONString(content));
        return content;

    }
}
