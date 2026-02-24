package org.skypro.teamwork.dto;

import java.util.List;

public class DynamicRuleListResponse {
    private List<DynamicRuleDto> data;

    public DynamicRuleListResponse(List<DynamicRuleDto> data) {
        this.data = data;
    }

    public List<DynamicRuleDto> getData() {
        return data;
    }

    public void setData(List<DynamicRuleDto> data) {
        this.data = data;
    }
}