package me.khw7385.graduate_project.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserQueryPlanInfo {
    private List<QueryPlan> alternativeQueryPlans = new ArrayList<>();
    @Setter
    private QueryPlan optimalQueryPlan;
    @Setter
    private double minCost;
    @Setter
    private double maxCost;

    public void addQueryPlan(QueryPlan queryPlan){
        alternativeQueryPlans.add(queryPlan);
    }

    public void removeQueryPlan(QueryPlan queryPlan){
        alternativeQueryPlans.remove(queryPlan);
    }
}
