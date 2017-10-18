package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.strategy.config.LklPayConfig;
import com.lsh.payment.core.util.PathUtil;
import com.lsh.payment.core.util.ftp.SFTPManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklBillStrategy implements BillStrategy {

    private static Logger logger = LoggerFactory.getLogger(LklBillStrategy.class);
    /**
     * 下载对账单
     *
     * @param billDate 查询参数
     * @param billType
     * @return
     */
    @Override
    public boolean downloadBillByDate(String billDate, int billType) {

        String sftpfileName = MessageFormat.format(LklPayConfig.LKL_FTP_FILENAME, billDate);
        String localfileName = MessageFormat.format(LklPayConfig.LKL_LOCAL_FILENAME, billDate);

        logger.info(" sftpfile Name is : " + sftpfileName);

        try {

            String localBillPath = BusiConstant.BILL_DOWNLOAD_PATH_LKL + File.separator + DateUtil.getDateString();
            PathUtil.createPath(localBillPath);

            SFTPManager sftpManager = new SFTPManager();
            if (!sftpManager.downFileBySFTP(LklPayConfig.SFTP_HOST, LklPayConfig.SFTP_PORT, LklPayConfig.SFTP_USERNAME, LklPayConfig.SFTP_PWD, "path", sftpfileName, localBillPath + localfileName)) {
                logger.error("连接FTP出错 或 FTP 下载报错：");
                throw new BusinessException(ExceptionStatus.E2004005.getCode(), ExceptionStatus.E2004005.getMessage());
            }
        } catch (Throwable e) {
            logger.error("连接FTP出错 或 FTP 下载报错：" + e);
            throw new BusinessException(ExceptionStatus.E2004005.getCode(), ExceptionStatus.E2004005.getMessage());
        }

        return true;
    }
}
