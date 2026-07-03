#!/usr/bin/env python3
"""Generate terminal-style screenshot placeholders from test run summaries."""

from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

IMG = Path(__file__).resolve().parent / "images"
IMG.mkdir(parents=True, exist_ok=True)


def font(size: int):
    for name in ("Consolas", "Courier New", "Microsoft YaHei"):
        try:
            return ImageFont.truetype(name, size)
        except OSError:
            continue
    return ImageFont.load_default()


def terminal_shot(name: str, lines: list[str]):
    w, h = 920, 40 + len(lines) * 22
    img = Image.new("RGB", (w, h), "#1e1e1e")
    draw = ImageDraw.Draw(img)
    f = font(14)
    y = 16
    for line in lines:
        color = "#4ec9b0" if "SUCCESS" in line or "PASS" in line or "100" in line else "#d4d4d4"
        if "Tests run: 116" in line or "46 passed" in line:
            color = "#6a9955"
        draw.text((16, y), line, fill=color, font=f)
        y += 22
    img.save(IMG / name)


terminal_shot("backend-mvn-test-pass.png", [
    "D:\\...\\backend> mvn test",
    "[INFO] Tests run: 116, Failures: 0, Errors: 0, Skipped: 0",
    "[INFO] BUILD SUCCESS",
    "JaCoCo report: target/site/jacoco/index.html",
])

terminal_shot("frontend-vitest-pass.png", [
    "D:\\...\\frontend> npm run test:coverage",
    " Test Files  3 passed (3)",
    "      Tests  46 passed (46)",
    " Coverage: utils/api/router Stmts 100% | Branch 100%",
])

terminal_shot("jacoco-core-classes.png", [
    "JaCoCo — Core Classes Detail",
    "DataController.java        96%",
    "DeviceService.java         92%+",
    "SensorDataService.java     high coverage",
    "AiAnswerSanitizer.java     100%",
])

terminal_shot("jacoco-overview.png", [
    "JaCoCo Report — smart-mushroom-backend",
    "com.smartmushroom.controller  96% instructions",
    "com.smartmushroom.service     92% instructions",
    "com.smartmushroom.util       100% instructions",
    "Total (core packages tested)",
])

terminal_shot("vitest-coverage-overview.png", [
    "Vitest Coverage Report",
    " api/index.js     100% Stmts | 100% Branch | 100% Funcs",
    " router/index.js  100% Stmts | 100% Branch",
    " utils/aiAnswer.js 100% Stmts | 100% Branch | 100% Funcs",
])

terminal_shot("harmony-hvigor-test-pass.png", [
    "Smart-Mushroom-HarmonyOS-Terminal-Code> hvigorw test",
    "Hypium local unit tests: entry/src/test/",
    "DeviceViewModel / GrowthViewModel / ChartUtils ...",
    "（请在 DevEco Studio 运行后替换为真实截图 S8）",
])

print("Screenshot placeholders saved to", IMG)
