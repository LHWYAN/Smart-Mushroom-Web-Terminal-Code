package com.smartmushroom.util;

import java.util.regex.Pattern;

/**
 * 清理大模型回答中的思考链内容（DeepSeek-R1 等模型的  / redacted_thinking 标签）。
 */
public final class AiAnswerSanitizer {

    private static final Pattern THINKING_BLOCKS = Pattern.compile(
            "<\\s*(?:think|thinking|redacted_thinking)\\s*>[\\s\\S]*?<\\s*/\\s*(?:think|thinking|redacted_thinking)\\s*>",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ORPHAN_OPEN_TAG = Pattern.compile(
            "<\\s*(?:think|thinking|redacted_thinking)\\s*>[\\s\\S]*",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ORPHAN_CLOSE_TAG = Pattern.compile(
            "<\\s*/\\s*(?:think|thinking|redacted_thinking)\\s*>",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern EXCESS_BLANK_LINES = Pattern.compile("\\n{3,}");

    private AiAnswerSanitizer() {
    }

    public static String sanitize(Object raw) {
        if (raw == null) {
            return "";
        }
        String text = String.valueOf(raw);
        if (text.isBlank()) {
            return "";
        }

        text = THINKING_BLOCKS.matcher(text).replaceAll("");
        text = ORPHAN_OPEN_TAG.matcher(text).replaceAll("");
        text = ORPHAN_CLOSE_TAG.matcher(text).replaceAll("");
        text = EXCESS_BLANK_LINES.matcher(text).replaceAll("\n\n");
        return text.trim();
    }
}
