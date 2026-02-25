package org.skypro.teamwork.dto;

import java.util.List;

public class RuleStatsResponse {
    private List<RuleStatDto> stats;

    public RuleStatsResponse(List<RuleStatDto> stats) {
        this.stats = stats;
    }

    public List<RuleStatDto> getStats() { return stats; }
    public void setStats(List<RuleStatDto> stats) { this.stats = stats; }
}