package me.khw7385.graduate_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QueryPlanGraphItemDto {
    private int index;
    private Double editDifference;
    private TreeNodeDto root;
}
