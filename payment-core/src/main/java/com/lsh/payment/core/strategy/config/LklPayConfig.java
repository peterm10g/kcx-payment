package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklPayConfig {

    //拉卡拉证书路径
    public static final String LKL_PERM_PATH = PropertiesUtil.getValue("lkl.perm.path");
//    public static final String LKL_PERM_PATH = "/Users/peter/tool/";
    //拉卡拉证书名称
    public static final String LKL_PERM_NAME = "rsa_public_key.pem";

    //title partern
    public static final String TITLE_NAME = "支付订单:{0}";
    //拉卡拉ftp文件名 partern
    public static final String LKL_FTP_FILENAME = "TP_lsh_{0}.txt";
    //拉卡拉下载文件名
    public static final String LKL_LOCAL_FILENAME = "lkl_bill_{0}.txt";
    //拉卡拉SFTP host
    public static final String SFTP_HOST = "sfftp.lakala.com";
    //
    public static final int SFTP_PORT = 2022;
    //用户名
    public static final String SFTP_USERNAME = "lsh";
    //密码
    public static final String SFTP_PWD = "lsh@1114";








//    public static final String LKL_FTP_DOWMLOAD_PATH = "/User/peter/test/";

//    public static final String LKL_FTP_HOST = "ip";
//
//    public static final int LKL_FTP_PORT = 21;
//
//    public static final String LKL_FTP_USERNAME = "ftp";
//
//    public static final String LKL_FTP_PWD = "pwd";
//
//    public static final String LKL_FTP_PATH = "data/";

}
