package me.khw7385.graduate_project.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class QueryPlanCompareDto {
    private Map<String, String> relationOperations1;
    private Map<String, String> relationOperations2;
    private List<String> sameJoinTrees;

    @Builder
    public QueryPlanCompareDto(Map<String, String> relationOperations1, Map<String, String> relationOperations2, List<String> sameJoinTrees) {
        this.relationOperations1 = relationOperations1;
        this.relationOperations2 = relationOperations2;
        this.sameJoinTrees = sameJoinTrees;
    }
}
