package com.supe.report.agent;

import com.supe.report.tools.ReportTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

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

    private final ChatClient chatClient;
    private final ReportTool reportTool;

    public ReportAgent(ChatModel chatModel, ReportTool reportTool) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.reportTool = reportTool;
    }

    public String chat(String userMessage) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .tools(reportTool)
                .call()
                .content();
    }
}
