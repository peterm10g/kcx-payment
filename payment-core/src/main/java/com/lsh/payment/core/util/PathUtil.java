package com.lsh.payment.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/14.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class PathUtil {

    private static Logger logger = LoggerFactory.getLogger(PathUtil.class);

    /**
     * 文件全路径
     * @param filePath 全路径
     */
    public static void createPath(String filePath){

        File fMkdir = new File(filePath);

        logger.info(("路径是****** " + filePath ));

        if (!fMkdir.exists()){

            boolean mkdr = fMkdir.mkdirs();
            if(mkdr){
                logger.info( "*****************  mkdirs success *******************");
            }else{
                logger.info( "*****************  mkdirs fail **********************");
            }
            logger.info("创建路径结束");
        }
    }


}
