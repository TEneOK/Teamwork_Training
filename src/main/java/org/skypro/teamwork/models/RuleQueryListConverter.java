package org.skypro.teamwork.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
public class RuleQueryListConverter implements AttributeConverter<List<RuleQuery>, String> {

    private static final Logger logger = LoggerFactory.getLogger(RuleQueryListConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RuleQuery> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting RuleQuery list to JSON", e);
            return "[]";
        }
    }

    @Override
    public List<RuleQuery> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<RuleQuery>>() {});
        } catch (IOException e) {
            logger.error("Error converting JSON to RuleQuery list", e);
            return null;
        }
    }
}