#!/usr/bin/env python3
"""Generate high-quality figures for unit test report."""

from pathlib import Path
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch

plt.rcParams['font.sans-serif'] = ['Microsoft YaHei', 'SimHei', 'Arial Unicode MS', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False

OUT = Path(__file__).resolve().parent / 'images'
OUT.mkdir(parents=True, exist_ok=True)


def save(fig, name):
    fig.savefig(OUT / name, dpi=160, bbox_inches='tight', facecolor='white')
    plt.close(fig)
    print('saved', name)


def fig01_system_architecture():
    fig, ax = plt.subplots(figsize=(12, 6))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 6)
    ax.axis('off')
    ax.set_title('图1  智慧蘑菇系统三端架构与测试对象', fontsize=16, fontweight='bold', pad=16)

    boxes = [
        (0.5, 3.8, 3.2, 1.4, '#E3F2FD', 'Web 前端\nVue3 + Vite\nVitest 测试'),
        (4.4, 3.8, 3.2, 1.4, '#E8F5E9', 'Web 后端\nSpring Boot\nJUnit5 + JaCoCo'),
        (8.3, 3.8, 3.2, 1.4, '#FFF3E0', '鸿蒙终端\nArkTS + Hypium'),
        (2.0, 1.0, 3.5, 1.2, '#F3E5F5', '嵌入式 WS63\n(本报告未纳入)'),
        (6.5, 1.0, 3.5, 1.2, '#ECEFF1', '华为云 IoTDA / Dify\n(测试中 Mock)'),
    ]
    for x, y, w, h, color, text in boxes:
        rect = FancyBboxPatch((x, y), w, h, boxstyle='round,pad=0.02', fc=color, ec='#455A64', lw=1.5)
        ax.add_patch(rect)
        ax.text(x + w / 2, y + h / 2, text, ha='center', va='center', fontsize=11)

    arrows = [((2.1, 3.8), (5.6, 3.2)), ((6.0, 3.8), (7.6, 3.2)), ((9.9, 3.8), (8.2, 2.2))]
    for start, end in arrows:
        ax.annotate('', xy=end, xytext=start, arrowprops=dict(arrowstyle='->', color='#546E7A', lw=1.5))

    ax.text(6, 0.3, '单元测试覆盖：Web 前后端（实训要求）+ 鸿蒙端（扩展）', ha='center', fontsize=10, color='#37474F')
    save(fig, 'fig01-system-architecture.png')


def fig02_test_layers():
    fig, ax = plt.subplots(figsize=(10, 7))
    ax.axis('off')
    ax.set_title('图2  Web 后端测试分层策略', fontsize=16, fontweight='bold')

    layers = [
        ('Controller 层  @WebMvcTest + MockMvc', 5, '#1565C0', '5 个 Controller，37 用例\n验证 HTTP 接口、参数校验、limit 边界'),
        ('Service 层  JUnit5 + Mockito', 4, '#2E7D32', '5 个 Service，53 用例\nMock Mapper，验证 CRUD 与业务规则'),
        ('Util / DTO / Holder', 3, '#6A1B9A', '3 类，26 用例\n纯逻辑，无 Spring 容器'),
    ]
    y = 6
    for title, h, color, desc in layers:
        rect = FancyBboxPatch((1, y - h), 8, h - 0.3, boxstyle='round,pad=0.02', fc=color, ec='white', alpha=0.85)
        ax.add_patch(rect)
        ax.text(5, y - h / 2 + 0.3, title, ha='center', va='center', color='white', fontsize=13, fontweight='bold')
        ax.text(5, y - h / 2 - 0.8, desc, ha='center', va='center', color='white', fontsize=10)
        y -= h + 0.2

    save(fig, 'fig02-backend-test-layers.png')


def fig03_workflow():
    fig, ax = plt.subplots(figsize=(12, 4.5))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 3)
    ax.axis('off')
    ax.set_title('图3  三端单元测试实施流程', fontsize=16, fontweight='bold')

    steps = ['环境搭建', '用例设计', '编写测试', '执行测试', '覆盖率分析', '报告输出']
    xs = [0.6 + i * 1.95 for i in range(6)]
    for i, (x, label) in enumerate(zip(xs, steps)):
        circle = plt.Circle((x, 1.5), 0.55, fc='#1976D2' if i < 4 else '#388E3C', ec='white', lw=2)
        ax.add_patch(circle)
        ax.text(x, 1.5, str(i + 1), ha='center', va='center', color='white', fontsize=14, fontweight='bold')
        ax.text(x, 0.5, label, ha='center', va='top', fontsize=10)
        if i < len(steps) - 1:
            ax.annotate('', xy=(xs[i + 1] - 0.65, 1.5), xytext=(x + 0.65, 1.5),
                        arrowprops=dict(arrowstyle='->', color='#78909C', lw=2))

    save(fig, 'fig03-test-workflow.png')


def fig04_backend_coverage():
    labels = ['Controller\n96%', 'Service\n92%', 'Util\n100%', 'DTO\n21%', 'Config\n1%']
    values = [96, 92, 100, 21, 1]
    colors = ['#43A047', '#66BB6A', '#2E7D32', '#FFB74D', '#E0E0E0']

    fig, ax = plt.subplots(figsize=(9, 5))
    bars = ax.barh(labels, values, color=colors, edgecolor='#455A64')
    ax.set_xlim(0, 110)
    ax.set_xlabel('指令覆盖率 (%)', fontsize=11)
    ax.set_title('图4  Web 后端 JaCoCo 包级覆盖率', fontsize=14, fontweight='bold')
    ax.axvline(80, color='#EF5350', ls='--', lw=1, label='工具类目标线 80%')
    ax.axvline(100, color='#1565C0', ls='--', lw=1, label='核心功能目标线 100%')
    for bar, val in zip(bars, values):
        ax.text(val + 1, bar.get_y() + bar.get_height() / 2, f'{val}%', va='center', fontsize=10)
    ax.legend(loc='lower right', fontsize=9)
    save(fig, 'fig04-backend-coverage.png')


def fig05_frontend_coverage():
    modules = ['api/index.js', 'utils/aiAnswer.js', 'router/index.js']
    stmt = [100, 100, 100]
    branch = [100, 100, 100]

    x = range(len(modules))
    w = 0.35
    fig, ax = plt.subplots(figsize=(9, 5))
    ax.bar([i - w / 2 for i in x], stmt, w, label='语句 Stmts', color='#1976D2')
    ax.bar([i + w / 2 for i in x], branch, w, label='分支 Branch', color='#00897B')
    ax.set_xticks(list(x))
    ax.set_xticklabels(modules, fontsize=10)
    ax.set_ylim(0, 110)
    ax.set_ylabel('覆盖率 (%)')
    ax.set_title('图5  Web 前端 Vitest 模块覆盖率', fontsize=14, fontweight='bold')
    ax.legend()
    save(fig, 'fig05-frontend-coverage.png')


def fig06_case_types():
    labels = ['正常场景', '异常场景', '边界场景']
    sizes = [145, 68, 64]
    colors = ['#42A5F5', '#EF5350', '#FFA726']
    explode = (0.03, 0.03, 0.03)

    fig, ax = plt.subplots(figsize=(7, 6))
    ax.pie(sizes, explode=explode, labels=labels, colors=colors, autopct='%1.1f%%',
           startangle=90, textprops={'fontsize': 11})
    ax.set_title('图6  三端测试用例类型分布（估算）', fontsize=14, fontweight='bold')
    save(fig, 'fig06-case-type-distribution.png')


def fig07_test_inventory():
    platforms = ['Web 后端\n(JUnit5)', 'Web 前端\n(Vitest)', '鸿蒙端\n(Hypium)']
    files = [13, 3, 10]
    cases = [116, 46, 145]

    fig, ax1 = plt.subplots(figsize=(9, 5))
    x = range(len(platforms))
    ax1.bar([i - 0.2 for i in x], files, 0.4, label='测试文件数', color='#5C6BC0')
    ax1.set_ylabel('测试文件数', color='#5C6BC0')
    ax1.set_xticks(list(x))
    ax1.set_xticklabels(platforms)

    ax2 = ax1.twinx()
    ax2.bar([i + 0.2 for i in x], cases, 0.4, label='测试用例数', color='#26A69A')
    ax2.set_ylabel('测试用例数', color='#26A69A')

    ax1.set_title('图7  三端测试规模统计', fontsize=14, fontweight='bold')
    lines1, labels1 = ax1.get_legend_handles_labels()
    lines2, labels2 = ax2.get_legend_handles_labels()
    ax1.legend(lines1 + lines2, labels1 + labels2, loc='upper left')
    save(fig, 'fig07-test-inventory.png')


def fig08_harmony_modules():
    modules = ['ViewModel', 'IotTypes/常量', 'RecipeModel', 'ChartUtils', 'IotToken', 'ThemeManager', 'BaseVM']
    counts = [40, 35, 19, 10, 7, 9, 6]

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.barh(modules, counts, color='#FF8F00')
    ax.set_xlabel('用例数量（it 计数）')
    ax.set_title('图8  鸿蒙端 Hypium 各模块用例数量', fontsize=14, fontweight='bold')
    for i, v in enumerate(counts):
        ax.text(v + 0.5, i, str(v), va='center')
    save(fig, 'fig08-harmony-module-cases.png')


if __name__ == '__main__':
    fig01_system_architecture()
    fig02_test_layers()
    fig03_workflow()
    fig04_backend_coverage()
    fig05_frontend_coverage()
    fig06_case_types()
    fig07_test_inventory()
    fig08_harmony_modules()
    print('All figures ->', OUT)
