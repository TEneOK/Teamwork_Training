package org.skypro.teamwork.service;

import org.skypro.teamwork.models.RuleQuery;
import org.skypro.teamwork.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class RuleEvaluator {

    private final TransactionRepository transactionRepository;

    public RuleEvaluator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean evaluateRule(UUID userId, List<RuleQuery> queries) {
        for (RuleQuery query : queries) {
            boolean result = evaluateQuery(userId, query);
            if (!result) {
                return false; // Если один запрос false, правило не выполняется
            }
        }
        return true;
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        boolean result;

        switch (query.getQuery()) {
            case "USER_OF":
                result = isUserOfProduct(userId, query.getArguments().get(0));
                break;

            case "ACTIVE_USER_OF":
                result = isActiveUserOfProduct(userId, query.getArguments().get(0));
                break;

            case "TRANSACTION_SUM_COMPARE":
                result = compareTransactionSumWithConstant(userId, query.getArguments());
                break;

            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                result = compareDepositWithWithdraw(userId, query.getArguments());
                break;

            default:
                throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        }

        return query.isNegate() ? !result : result;
    }

    private boolean isUserOfProduct(UUID userId, String productType) {
        return transactionRepository.existsByUserIdAndProductType(userId, productType);
    }

    private boolean isActiveUserOfProduct(UUID userId, String productType) {
        return transactionRepository.countTransactionsByUserIdAndProductType(userId, productType) >= 5;
    }

    private boolean compareTransactionSumWithConstant(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operation = arguments.get(2);
        BigDecimal constant = new BigDecimal(arguments.get(3));

        BigDecimal sum = transactionRepository.getSumByUserIdAndProductTypeAndTransactionType(
                userId, productType, transactionType);

        return compare(sum, operation, constant);
    }

    private boolean compareDepositWithWithdraw(UUID userId, List<String> arguments) {
        String productType = arguments.get(0);
        String operation = arguments.get(1);

        BigDecimal depositSum = transactionRepository.getSumByUserIdAndProductTypeAndTransactionType(
                userId, productType, "DEPOSIT");
        BigDecimal withdrawSum = transactionRepository.getSumByUserIdAndProductTypeAndTransactionType(
                userId, productType, "WITHDRAW");

        return compare(depositSum, operation, withdrawSum);
    }

    private boolean compare(BigDecimal left, String operation, BigDecimal right) {
        switch (operation) {
            case ">": return left.compareTo(right) > 0;
            case "<": return left.compareTo(right) < 0;
            case "=": return left.compareTo(right) == 0;
            case ">=": return left.compareTo(right) >= 0;
            case "<=": return left.compareTo(right) <= 0;
            default: throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }
}