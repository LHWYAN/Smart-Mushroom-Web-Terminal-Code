# Smart-Mushroom-Web-Terminal-Code

智慧蘑菇农场 Web 终端 —— Spring Boot + Vue 3 前后端分离架构。

## 项目结构

```
Smart-Mushroom-Web-Terminal-Code/
├── backend/          # Spring Boot 3 后端 (:8080)
│   ├── pom.xml
│   └── src/main/java/com/smartmushroom/
└── frontend/         # Vue 3 + Element Plus 前端 (:5173)
    ├── package.json
    └── src/
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17, Spring Boot 3, MyBatis-Plus, Knife4j |
| 前端 | Vue 3, Vite, Element Plus, ECharts, Axios |
| 数据库 | SQLite（开发）/ KingbaseES（生产） |
| 消息 | 华为云 IoTDA AMQP 1.0 / 模拟数据 |
| AI | Dify + Ollama |

## 快速开始

### 后端

```bash
cd backend
mvn spring-boot:run
```

- API 文档: http://localhost:8080/doc.html
- 健康检查: http://localhost:8080/api/v1/health

### 前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 配置说明

后端配置文件 `backend/src/main/resources/application.yml`：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `app.data-source` | 数据源：simulate / amqp | simulate |
| `app.db-type` | 数据库类型 | sqlite |
| `app.dify.api-url` | Dify API 地址 | http://localhost/v1/chat-messages |
| `app.dify.api-key` | Dify API Key | 需配置 |

## API 接口

共 20 个 REST 接口 + WebSocket，与 Python FastAPI 版本完全兼容：

- `GET /api/v1/health` — 健康检查
- `GET /api/v1/latest` — 最新传感器数据
- `GET /api/v1/history` — 历史数据
- `GET/POST/PUT/DELETE /api/v1/devices` — 设备 CRUD
- `GET/POST/DELETE /api/v1/sensor-data` — 数据 CRUD
- `GET/POST/PUT/DELETE /api/v1/commands` — 命令 CRUD
- `POST /api/v1/ai/chat` — AI 助手
- `WS /ws/realtime` — 实时推送

## 关联项目

| 项目 | 说明 |
|------|------|
| [21_huaweiiot](https://github.com/LHWYAN) | WS63 嵌入式端 |
| Smart-Mushroom-HarmonyOS-Terminal-Code | 鸿蒙端 App |
| Dify + Ollama | AI 大模型模块 |

## License

本项目仅供学习使用。
