package com.supe.report.controller;

import com.supe.report.agent.ReportAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController("supeReportAgentController")
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
     * POST /api/agent/chat/stream
     * 请求体: {"message": "用户消息", "conversationId": "可选会话ID"}
     * 响应: text/event-stream (SSE)
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        String conversationId = request.getOrDefault("conversationId", "default");

        SseEmitter emitter = new SseEmitter(0L);
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();

        Disposable disposable = reportAgent.streamChat(message, conversationId).subscribe(
                chunk -> {
                    try {
                        emitter.send(SseEmitter.event().data(chunk));
                    } catch (IOException e) {
                        throw new RuntimeException("SSE send failed", e);
                    }
                },
                emitter::completeWithError,
                () -> {
                    try {
                        emitter.send(SseEmitter.event().data("[DONE]"));
                        emitter.complete();
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }
        );
        disposableRef.set(disposable);

        Runnable disposeTask = () -> {
            Disposable d = disposableRef.get();
            if (d != null && !d.isDisposed()) {
                d.dispose();
            }
        };
        emitter.onCompletion(disposeTask);
        emitter.onTimeout(() -> {
            disposeTask.run();
            emitter.complete();
        });
        emitter.onError(ex -> disposeTask.run());

        return emitter;
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
