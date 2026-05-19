package com.supe.report.studio;

import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.supe.report.agent.ReportAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudioAgentLoaderTest {

    private ReportAgent reportAgent;
    private ReactAgent reactAgent;
    private StudioAgentLoader loader;

    @BeforeEach
    void setUp() {
        reportAgent = mock(ReportAgent.class);
        reactAgent = mock(ReactAgent.class);
        when(reportAgent.getReactAgent()).thenReturn(reactAgent);
        loader = new StudioAgentLoader(reportAgent);
    }

    @Test
    void listAgents_containsPrimaryAndStudioDefaultAgent() {
        List<String> agents = loader.listAgents();
        assertEquals(2, agents.size());
        assertEquals("report-agent", agents.get(0));
        assertEquals("research_agent", agents.get(1));
    }

    @Test
    void loadAgent_primaryName_returnsReactAgent() {
        Agent agent = loader.loadAgent("report-agent");
        assertSame(reactAgent, agent);
    }

    @Test
    void loadAgent_studioDefaultName_returnsReactAgent() {
        Agent agent = loader.loadAgent("research_agent");
        assertSame(reactAgent, agent);
    }

    @Test
    void loadAgent_unknownName_throwsNoSuchElement() {
        assertThrows(NoSuchElementException.class, () -> loader.loadAgent("unknown"));
    }

    @Test
    void loadAgent_nullName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> loader.loadAgent(null));
    }

    @Test
    void loadAgent_emptyName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> loader.loadAgent("  "));
    }
}
