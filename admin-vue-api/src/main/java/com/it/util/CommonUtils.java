package com.it.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 描述：〈工具类〉
 */
@Component
public class CommonUtils {


    /**
     * 生成随便编号
     *
     * @return
     */
    public static String getOrderIdByUUId() {
        int machineId = 1;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) { //有可能是负数
            hashCodeV = -hashCodeV;
        }
//         0 代表前面补充0     
//         4 代表长度为4     
//         d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }

    /**
     * 字符串不为空的判断函数
     *
     * @param string
     * @return
     */
    public static boolean stringIsNotBlack(String string) {
        if (string != null && !"".equals(string)) {
            return true;
        }
        return false;
    }
}
