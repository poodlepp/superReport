package com.supe.report.studio;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.supe.report.agent.ReportAgent;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class StudioAgentLoader implements AgentLoader {

    private static final String PRIMARY_AGENT_NAME = "report-agent";
    private static final String DEFAULT_STUDIO_AGENT_NAME = "research_agent";
    private static final List<String> SUPPORTED_AGENT_NAMES =
            List.of(PRIMARY_AGENT_NAME, DEFAULT_STUDIO_AGENT_NAME);

    private final ReportAgent reportAgent;

    public StudioAgentLoader(ReportAgent reportAgent) {
        this.reportAgent = reportAgent;
    }

    @Override
    @Nonnull
    public List<String> listAgents() {
        return SUPPORTED_AGENT_NAMES;
    }

    @Override
    public Agent loadAgent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent name cannot be null or empty");
        }
        if (!SUPPORTED_AGENT_NAMES.contains(name)) {
            throw new NoSuchElementException("Agent not found: " + name);
        }
        return reportAgent.getReactAgent();
    }
}
