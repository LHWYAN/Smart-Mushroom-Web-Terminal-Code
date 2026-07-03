/** 移除模型思考链标签（与后端 AiAnswerSanitizer 保持一致） */
export function stripThinking(text) {
  if (!text) return ''
  let s = String(text)
  s = s.replace(/<\s*(?:think|thinking|redacted_thinking)\s*>[\s\S]*?<\s*\/\s*(?:think|thinking|redacted_thinking)\s*>/gi, '')
  s = s.replace(/<\s*(?:think|thinking|redacted_thinking)\s*>[\s\S]*/gi, '')
  s = s.replace(/<\s*\/\s*(?:think|thinking|redacted_thinking)\s*>/gi, '')
  return s.replace(/\n{3,}/g, '\n\n').trim()
}
