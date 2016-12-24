package com.jiang.android.architecture.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.util.Random;
import java.util.UUID;

/**
 * Created by jiang on 2016/11/23.
 */

public class CommonUtils {
    public static String getNumberRadim(int n) {
        if (n < 1 || n > 10) {
            throw new IllegalArgumentException("cannot random " + n + " bit number");
        }
        Random ran = new Random();
        if (n == 1) {
            return String.valueOf(ran.nextInt(10));
        }
        int bitField = 0;
        char[] chs = new char[n];
        for (int i = 0; i < n; i++) {
            while (true) {
                int k = ran.nextInt(10);
                if ((bitField & (1 << k)) == 0) {
                    bitField |= 1 << k;
                    chs[i] = (char) (k + '0');
                    break;
                }
            }
        }
        return new String(chs);
    }
    public static String getGUID() {

        // 创建 GUID 对象
        UUID uuid = UUID.randomUUID();
        // 得到对象产生的ID
        String a = uuid.toString();
        // 转换为大写
        a = a.toUpperCase();
        // 替换 -
        a = a.replaceAll("-", "");
        L.i(a);
        return a;
    }

    /**
     * 数字转换中文
     */
    public static String int2Chinese(int i) {
        String[] zh = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};

        String str = "";
        StringBuffer sb = new StringBuffer(String.valueOf(i));
        sb = sb.reverse();
        int r = 0;
        int l = 0;
        for (int j = 0; j < sb.length(); j++) {
            /**
             * 当前数字
             */
            r = Integer.valueOf(sb.substring(j, j + 1));

            if (j != 0)
            /**
             * 上一个数字
             */
                l = Integer.valueOf(sb.substring(j - 1, j));

            if (j == 0) {
                if (r != 0 || sb.length() == 1)
                    str = zh[r];
                continue;
            }

            if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 9) {
                if (r != 0)
                    str = zh[r] + unit[j] + str;
                else if (l != 0)
                    str = zh[r] + str;
                continue;
            }

            if (j == 4 || j == 8) {
                str = unit[j] + str;
                if ((l != 0 && r == 0) || r != 0)
                    str = zh[r] + str;
                continue;
            }
        }
        return str;
    }
    public static String byte2Common(Context context, String fsize) {
        if (TextUtils.isEmpty(fsize)) {
            return "";
        }
        Long sizeLong = Long.parseLong(fsize);
        return Formatter.formatFileSize(context, sizeLong);
    }
}
