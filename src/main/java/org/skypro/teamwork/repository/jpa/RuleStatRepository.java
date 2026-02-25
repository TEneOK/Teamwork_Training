package org.skypro.teamwork.repository.jpa;

import org.skypro.teamwork.models.RuleStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleStatRepository extends JpaRepository<RuleStat, UUID> {
    Optional<RuleStat> findByRuleId(UUID ruleId);
    void deleteByRuleId(UUID ruleId);
}