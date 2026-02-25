package org.skypro.teamwork.repository.jpa;

import org.skypro.teamwork.models.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    boolean existsByProductId(UUID productId);
}