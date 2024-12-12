package me.khw7385.graduate_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class QueryPlanGraphDto {
    QueryPlanGraphItemDto target;
    List<QueryPlanGraphItemDto> others;
}
