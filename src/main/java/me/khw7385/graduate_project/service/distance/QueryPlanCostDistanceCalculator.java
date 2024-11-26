package me.khw7385.graduate_project.service.distance;

import me.khw7385.graduate_project.domain.QueryPlan;

public class QueryPlanCostDistanceCalculator {
    private static final QueryPlanCostDistanceCalculator INSTANCE = new QueryPlanCostDistanceCalculator();
    public static QueryPlanCostDistanceCalculator getInstance(){
        return INSTANCE;
    }
    public double calculate(QueryPlan queryPlan1, QueryPlan queryPlan2, double MaxCost, double minCost) {
        return Math.abs(queryPlan1.getCost() - queryPlan2.getCost()) / MaxCost - minCost;
    }
}
