package org.skypro.teamwork.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "rule_stats")
public class RuleStat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_id", nullable = false, unique = true)
    private UUID ruleId;

    @Column(name = "count", nullable = false)
    private long count = 0;

    public RuleStat() {}

    public RuleStat(UUID ruleId) {
        this.ruleId = ruleId;
        this.count = 0;
    }

    // геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }

    public void increment() { this.count++; }
}