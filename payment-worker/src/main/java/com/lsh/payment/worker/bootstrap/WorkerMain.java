package com.lsh.payment.worker.bootstrap;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/16.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WorkerMain {

    public static void main(String[] args) {
        // 第一个参数用来标记程序名称
        String[] argAry = null;

        if (args != null && args.length > 1) {
            argAry = new String[args.length - 1];
            System.arraycopy(args, 1, argAry, 0, args.length - 1);
        }

        com.alibaba.dubbo.container.Main.main(argAry);
    }

}
