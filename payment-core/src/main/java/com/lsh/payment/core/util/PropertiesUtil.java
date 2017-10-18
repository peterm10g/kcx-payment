package com.lsh.payment.core.util;


import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/10.
 * 北京链商电子商务有限公司
 * desc:
 */
public class PropertiesUtil {

    private static final Properties properties = new Properties();

    private static final List<String> configFile = Arrays.asList("props/pay_request_config.properties");

    static {

        try {
            for (String fileName : configFile) {

                InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);

                properties.load(in);
            }
        } catch (IOException e) {

            throw new BusinessException(ExceptionStatus.E2002001.getCode(), ExceptionStatus.E2002001.getMessage());
        }

    }

    /**
     * 获取配置项
     *
     * @param configKey 关键字
     * @return String
     */
    public static String getValue(String configKey) {

        if (StringUtils.isBlank(configKey)) {

            return "";
        }

        String configValue = properties.getProperty(configKey, "");

        if (StringUtils.isBlank(configValue)) {

            throw new BusinessException(ExceptionStatus.E2002001.getCode(), configKey + "值不存在");
        }

        return configValue;
    }


}

