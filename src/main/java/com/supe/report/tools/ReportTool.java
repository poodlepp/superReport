package com.supe.report.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 报告工具集 — Agent 可调用的工具函数
 */
@Component
public class ReportTool {

    @Tool(description = "获取当前系统时间，用于报告中的时间戳")
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Tool(description = "根据主题生成报告摘要大纲")
    public String generateOutline(@ToolParam(description = "报告主题") String topic) {
        return String.format("""
                报告主题: %s
                生成时间: %s
                大纲:
                1. 背景与目标
                2. 数据分析
                3. 关键发现
                4. 结论与建议
                """, topic, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Tool(description = "查询指定指标的模拟数据")
    public String queryMetric(@ToolParam(description = "指标名称") String metricName) {
        // 模拟数据查询
        return String.format("指标 [%s] 当前值: %.2f, 环比增长: %.1f%%",
                metricName, Math.random() * 100, (Math.random() - 0.5) * 20);
    }
}
