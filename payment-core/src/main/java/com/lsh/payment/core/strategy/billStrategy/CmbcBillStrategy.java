package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.util.DateUtil;
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
public class CmbcBillStrategy implements BillStrategy {

    private static Logger logger = LoggerFactory.getLogger(CmbcBillStrategy.class);

    /**
     * 下载对账单
     *
     * @param billDate 查询参数
     * @param billType
     * @return
     */
    @Override
    public boolean downloadBillByDate(String billDate, int billType) {

//        String sftpfileName = MessageFormat.format(LklPayConfig.LKL_FTP_FILENAME, billDate);
//        String localfileName = MessageFormat.format(LklPayConfig.LKL_LOCAL_FILENAME, billDate);

//        logger.info(" sftpfile Name is : " + sftpfileName);

        try {
            String sftpfileName;
            String localfileName;
            if (billType == BillTradeType.WXNATIVE.getCode()) {
                sftpfileName = MessageFormat.format(CMBCPayConfig.CMBC_FTP_FILENAME, "wx", billDate);
                localfileName = MessageFormat.format(CMBCPayConfig.CMBC_LOCAL_FILENAME, "wx", billDate);
            } else if (billType == BillTradeType.ALINATIVE.getCode()) {
                sftpfileName = MessageFormat.format(CMBCPayConfig.CMBC_LOCAL_FILENAME, "zfb", billDate);
                localfileName = MessageFormat.format(CMBCPayConfig.CMBC_LOCAL_FILENAME, "wx", billDate);
            } else {
                throw new BusinessException(ExceptionStatus.E2004003.getCode(), ExceptionStatus.E2004003.getMessage());
            }

            String localBillPath = BusiConstant.BILL_DOWNLOAD_PATH_CMBC + File.separator + DateUtil.getDateString();
            PathUtil.createPath(localBillPath);

            SFTPManager sftpManager = new SFTPManager();
            if (!sftpManager.downFileBySFTP(CMBCPayConfig.SFTP_HOST, CMBCPayConfig.SFTP_PORT, CMBCPayConfig.SFTP_USERNAME, CMBCPayConfig.SFTP_PWD, "path", sftpfileName, localBillPath + localfileName)) {
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
