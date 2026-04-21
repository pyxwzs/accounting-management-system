package com.it.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmojiFilter {
    /**
     * 判断是否存在Emoji
     *
     * @param codePoint
     * @return
     * @author madaha
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9)
                || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source 待过滤字符串
     * @return
     * @author madaha
     */
    public static String filterEmoji(String source) {
        if (!CommonUtils.stringIsNotBlack(source)) {
            return source;
        }
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return source;
        } else {
            if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    /**
     * @param str 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException exception
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     */
    public static String emojiConvert1(String str) {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb, "[[" + URLEncoder.encode(matcher.group(1), "UTF-8") + "]]");
            } catch (UnsupportedEncodingException e) {
                throw new BusinessException(ResponseCode.EMOJIfILTER_ERROR.getCode(), ResponseCode.EMOJIfILTER_ERROR.getMessage());

            }
        }
        matcher.appendTail(sb);
        // System.out.println("emojiConvert " + str + " to " + sb.toString() + ", len：" + sb.length());
        return sb.toString();
    }

    /**
     * @param str 转换前的字符串
     * @return 转换后的字符串
     * @throws UnsupportedEncodingException exception
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     */
    public static String emojiRecovery2(String str) {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        if (CommonUtils.stringIsNotBlack(str)) {
            Matcher matcher = pattern.matcher(str);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                try {
                    matcher.appendReplacement(sb, URLDecoder.decode(matcher.group(1), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new BusinessException(ResponseCode.EMOJIfILTER_ERROR.getCode(), ResponseCode.EMOJIfILTER_ERROR.getMessage());
                }
            }
            matcher.appendTail(sb);
            // System.out.println("emojiRecovery " + str + " to " + sb.toString());
            return sb.toString();
        }
        return "";
    }
}
