package com.supe.report.agent;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.interceptor.todolist.TodoListInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.toolretry.ToolRetryInterceptor;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.supe.report.tools.ReportTool;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportAgent {

    private static final String SYSTEM_PROMPT = """
            你是一个专业的报告生成助手。你可以：
            1. 获取当前时间用于报告时间戳
            2. 根据主题生成报告大纲
            3. 查询相关指标数据

            请根据用户的需求，合理调用工具来辅助生成报告内容。
            回答请使用中文。
            """;

    private final ReactAgent reactAgent;
    private final ConcurrentHashMap<String, String> conversationThreadIds = new ConcurrentHashMap<>();

    public ReportAgent(ChatModel chatModel, ReportTool reportTool) {
        this.reactAgent = ReactAgent.builder()
                .name("report-agent")
                .description("报告生成智能体")
                .model(chatModel)
                .instruction(SYSTEM_PROMPT)
                .methodTools(reportTool)
                .includeContents(true)
                .interceptors(
                        TodoListInterceptor.builder().build(),
                        ToolRetryInterceptor.builder()
                                .maxRetries(2)
                                .onFailure(ToolRetryInterceptor.OnFailureBehavior.RETURN_MESSAGE)
                                .build()
                )
                .saver(MemorySaver.builder().build())
                .build();
    }

    public String chat(String userMessage, String conversationId) {
        String threadId = resolveThreadId(conversationId);
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .build();

        try {
            AssistantMessage result = reactAgent.call(userMessage, config);
            String text = (result != null) ? result.getText() : null;
            if (text != null && !text.isBlank()) {
                return text;
            }
            return extractTextFromThreadState(threadId);
        } catch (GraphRunnerException e) {
            throw new RuntimeException("Agent call failed: " + e.getMessage(), e);
        }
    }

    public String chat(String userMessage) {
        return chat(userMessage, "default");
    }

    public void clearMemory(String conversationId) {
        conversationThreadIds.put(conversationId, conversationId + "_" + System.currentTimeMillis());
    }

    private String resolveThreadId(String conversationId) {
        return conversationThreadIds.getOrDefault(conversationId, conversationId);
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromThreadState(String threadId) {
        Map<String, Object> state = reactAgent.getThreadState(threadId);
        if (state == null) {
            return "";
        }
        Object messagesObj = state.get("messages");
        if (messagesObj instanceof List<?> messages) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                Object msg = messages.get(i);
                if (msg instanceof AssistantMessage assistant) {
                    String text = assistant.getText();
                    if (text != null && !text.isBlank()) {
                        return text;
                    }
                } else if (msg instanceof Message message) {
                    String text = message.getText();
                    if (text != null && !text.isBlank()
                            && message.getMessageType() == org.springframework.ai.chat.messages.MessageType.ASSISTANT) {
                        return text;
                    }
                }
            }
        }
        return "";
    }
}
