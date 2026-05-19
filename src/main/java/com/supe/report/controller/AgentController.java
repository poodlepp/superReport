package com.supe.report.controller;

import com.supe.report.agent.ReportAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ReportAgent reportAgent;

    public AgentController(ReportAgent reportAgent) {
        this.reportAgent = reportAgent;
    }

    /**
     * POST /api/agent/chat
     * 请求体: {"message": "用户消息", "conversationId": "可选会话ID"}
     * 响应: {"reply": "Agent 回复"}
     */
    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        String conversationId = request.getOrDefault("conversationId", "default");
        String reply = reportAgent.chat(message, conversationId);
        return Map.of("reply", reply);
    }

    /**
     * DELETE /api/agent/memory/{conversationId}
     * 清除指定会话的记忆
     */
    @DeleteMapping("/memory/{conversationId}")
    public Map<String, String> clearMemory(@PathVariable String conversationId) {
        reportAgent.clearMemory(conversationId);
        return Map.of("status", "cleared");
    }
}
