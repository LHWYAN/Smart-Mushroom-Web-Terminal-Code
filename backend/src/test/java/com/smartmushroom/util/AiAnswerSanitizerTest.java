package com.smartmushroom.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link AiAnswerSanitizer} 单元测试。
 * 覆盖正常标签剥离、orphan 标签处理、null/空白边界、多余换行压缩。
 */
@DisplayName("AiAnswerSanitizer - 大模型回答清洗器")
class AiAnswerSanitizerTest {

    // ==================== 正常用例 ====================

    @Test
    @DisplayName("含 <think> 标签时正确剥离，保留正文")
    void sanitize_thinkTag_stripped() {
        String raw = "<think>这是思考过程</think>蘑菇适宜温度是18-25℃。";
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("蘑菇适宜温度是18-25℃。", result);
    }

    @Test
    @DisplayName("含 <thinking> 标签时正确剥离")
    void sanitize_thinkingTag_stripped() {
        String raw = "<thinking>分析用户问题</thinking>湿度应保持在80%-90%。";
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("湿度应保持在80%-90%。", result);
    }

    @Test
    @DisplayName("含 <redacted_thinking> 标签时正确剥离")
    void sanitize_redactedThinkingTag_stripped() {
        String raw = "<redacted_thinking>隐藏的推理链</redacted_thinking>CO2浓度应低于1000ppm。";
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("CO2浓度应低于1000ppm。", result);
    }

    @Test
    @DisplayName("大小写不敏感：THINK 标签也能剥离")
    void sanitize_caseInsensitive_stripped() {
        String raw = "<THINK>大写标签</THINK>光照强度需要控制。";
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("光照强度需要控制。", result);
    }

    @Test
    @DisplayName("多个思考块全部剥离")
    void sanitize_multipleBlocks_stripped() {
        String raw = "<think>第一段思考</think>答案A<think>第二段思考</think>答案B";
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("答案A答案B", result);
    }

    @Test
    @DisplayName("非字符串对象通过 String.valueOf 转换后处理")
    void sanitize_nonStringObject_converted() {
        Object raw = 12345;
        String result = AiAnswerSanitizer.sanitize(raw);
        assertEquals("12345", result);
    }

    // ==================== 边界用例 ====================

    @Nested
    @DisplayName("边界条件")
    class BoundaryCases {

        @Test
        @DisplayName("null 输入返回空字符串")
        void sanitize_null_returnsEmpty() {
            assertEquals("", AiAnswerSanitizer.sanitize(null));
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void sanitize_emptyString_returnsEmpty() {
            assertEquals("", AiAnswerSanitizer.sanitize(""));
        }

        @Test
        @DisplayName("纯空白字符串返回空字符串")
        void sanitize_blankString_returnsEmpty() {
            assertEquals("", AiAnswerSanitizer.sanitize("   \n\t  "));
        }

        @Test
        @DisplayName("仅有开标签无闭标签（orphan open tag）：开标签及后续内容被清除，保留前置正文")
        void sanitize_orphanOpenTag_removed() {
            // ORPHAN_OPEN_TAG 正则贪婪匹配从开标签到字符串末尾
            String raw = "正文内容<think>这段思考没有闭合标签";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("正文内容", result);
        }

        @Test
        @DisplayName("仅有开标签且无前置正文时返回空字符串")
        void sanitize_orphanOpenTagOnly_returnsEmpty() {
            String raw = "<think>后续全部被清除";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("", result);
        }

        @Test
        @DisplayName("仅有闭标签无开标签（orphan close tag）被清除")
        void sanitize_orphanCloseTag_removed() {
            String raw = "正文内容</think>后续文字";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("正文内容后续文字", result);
        }

        @Test
        @DisplayName("多余连续换行（3个及以上）压缩为双换行")
        void sanitize_excessBlankLines_compressed() {
            String raw = "第一段\n\n\n\n\n第二段";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("第一段\n\n第二段", result);
        }

        @Test
        @DisplayName("标签内含换行也能正确匹配剥离")
        void sanitize_blockWithNewlines_stripped() {
            String raw = "<think>\n多行思考\n内容\n</think>\n正文";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("正文", result);
        }

        @Test
        @DisplayName("无任何标签的纯文本原样返回")
        void sanitize_plainText_unchanged() {
            String raw = "这是一段普通文本，没有任何标签。";
            assertEquals(raw, AiAnswerSanitizer.sanitize(raw));
        }

        @Test
        @DisplayName("结果首尾空白被 trim")
        void sanitize_resultTrimmed() {
            String raw = "  正文内容  ";
            String result = AiAnswerSanitizer.sanitize(raw);
            assertEquals("正文内容", result);
        }
    }
}
