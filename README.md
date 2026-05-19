# superReport

Spring AI Alibaba Agent Framework 示例项目，演示 ReactAgent 的报告生成能力。

## 特性

- 基于 ReactAgent 的多轮对话报告生成
- 内置 Interceptors：
  - **TodoListInterceptor** — 规划：Agent 在执行工具前先生成执行计划
  - **ToolRetryInterceptor** — 工具重试：工具调用失败时自动重试（最多 2 次），失败后返回消息而非抛异常
