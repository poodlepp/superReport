# superReport

Spring AI Alibaba Agent Framework 示例项目，演示 ReactAgent 的报告生成能力。

## 特性

- 基于 ReactAgent 的多轮对话报告生成
- 内置 Interceptors：
  - **TodoListInterceptor** — 规划：Agent 在执行工具前先生成执行计划
  - **ToolRetryInterceptor** — 工具重试：工具调用失败时自动重试（最多 2 次），失败后返回消息而非抛异常

## API

### 1) 同步接口（兼容保留）

`POST /api/agent/chat`

- Content-Type: `application/json`
- Body: `{"message":"请生成周报","conversationId":"demo-1"}`
- Response: `{"reply":"..."}`

### 2) 流式接口（SSE）

`POST /api/agent/chat/stream`

- Content-Type: `application/json`
- Accept: `text/event-stream`
- Body: `{"message":"请分步生成报告","conversationId":"demo-1"}`
- Response: `text/event-stream`，按 `data: <chunk>` 持续返回，结束时发送 `data: [DONE]`

示例：

```bash
curl -N -X POST 'http://localhost:8080/api/agent/chat/stream' \
  -H 'Content-Type: application/json' \
  -H 'Accept: text/event-stream' \
  -d '{"message":"请生成一份项目周报","conversationId":"team-a"}'
```

说明：`conversationId` 仍用于会话隔离，不同 `conversationId` 会映射到不同 threadId。

## Studio 可视化调试

已集成 Spring AI Alibaba Studio embedded mode，启动后可直接访问内置 UI 进行 Agent 调试。

- 访问地址：http://localhost:8080/chatui/index.html
- appName：推荐使用 `research_agent`（Spring AI Alibaba Studio 默认值），同时兼容 `report-agent`。
- 如果 UI 默认发送 `research_agent`，现在无需额外配置即可直接调试。
- Studio 提供的后端接口（`/list-apps`、`/run_sse` 等）由 Studio 自动注册，原 `/api/agent/*` 接口仍保留。
