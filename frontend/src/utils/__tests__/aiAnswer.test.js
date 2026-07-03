import { describe, it, expect } from 'vitest'
import { stripThinking } from '../aiAnswer'

describe('stripThinking - 大模型回答清洗', () => {
  // ==================== 正常用例 ====================

  it('含 <think> 标签时正确剥离，保留正文', () => {
    const raw = '<think>这是思考过程</think>蘑菇适宜温度是18-25℃。'
    expect(stripThinking(raw)).toBe('蘑菇适宜温度是18-25℃。')
  })

  it('含 <thinking> 标签时正确剥离', () => {
    const raw = '<thinking>分析用户问题</thinking>湿度应保持在80%-90%。'
    expect(stripThinking(raw)).toBe('湿度应保持在80%-90%。')
  })

  it('含 <redacted_thinking> 标签时正确剥离', () => {
    const raw = '<redacted_thinking>隐藏的推理链</redacted_thinking>CO2浓度应低于1000ppm。'
    expect(stripThinking(raw)).toBe('CO2浓度应低于1000ppm。')
  })

  it('大小写不敏感：THINK 标签也能剥离', () => {
    const raw = '<THINK>大写标签</THINK>光照强度需要控制。'
    expect(stripThinking(raw)).toBe('光照强度需要控制。')
  })

  it('多个思考块全部剥离', () => {
    const raw = '<think>第一段思考</think>答案A<think>第二段思考</think>答案B'
    expect(stripThinking(raw)).toBe('答案A答案B')
  })

  it('非字符串对象通过 String() 转换后处理', () => {
    expect(stripThinking(12345)).toBe('12345')
  })

  // ==================== 边界用例 ====================

  it('null 输入返回空字符串', () => {
    expect(stripThinking(null)).toBe('')
  })

  it('undefined 输入返回空字符串', () => {
    expect(stripThinking(undefined)).toBe('')
  })

  it('空字符串返回空字符串', () => {
    expect(stripThinking('')).toBe('')
  })

  it('纯空白字符串返回空字符串', () => {
    expect(stripThinking('   \n\t  ')).toBe('')
  })

  it('orphan open tag：开标签及后续内容被清除，保留前置正文', () => {
    const raw = '正文内容<think>这段思考没有闭合标签'
    expect(stripThinking(raw)).toBe('正文内容')
  })

  it('orphan open tag 且无前置正文时返回空字符串', () => {
    const raw = '<think>后续全部被清除'
    expect(stripThinking(raw)).toBe('')
  })

  it('orphan close tag 被清除', () => {
    const raw = '正文内容</think>后续文字'
    expect(stripThinking(raw)).toBe('正文内容后续文字')
  })

  it('多余连续换行（3个及以上）压缩为双换行', () => {
    const raw = '第一段\n\n\n\n\n第二段'
    expect(stripThinking(raw)).toBe('第一段\n\n第二段')
  })

  it('标签内含换行也能正确匹配剥离', () => {
    const raw = '<think>\n多行思考\n内容\n</think>\n正文'
    expect(stripThinking(raw)).toBe('正文')
  })

  it('无任何标签的纯文本原样返回', () => {
    const raw = '这是一段普通文本，没有任何标签。'
    expect(stripThinking(raw)).toBe(raw)
  })

  it('结果首尾空白被 trim', () => {
    expect(stripThinking('  正文内容  ')).toBe('正文内容')
  })
})
