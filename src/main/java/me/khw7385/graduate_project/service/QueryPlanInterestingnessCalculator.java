package me.khw7385.graduate_project.service;

import lombok.RequiredArgsConstructor;
import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.service.distance.QueryPlanContentDistanceCalculator;
import me.khw7385.graduate_project.service.distance.QueryPlanCostDistanceCalculator;
import me.khw7385.graduate_project.service.distance.QueryPlanStructureDistanceCalculator;
import org.springframework.stereotype.Component;

public class QueryPlanInterestingnessCalculator {
    private final QueryPlanStructureDistanceCalculator structureDistanceCalculator = QueryPlanStructureDistanceCalculator.getInstance();
    private final QueryPlanContentDistanceCalculator contentDistanceCalculator = QueryPlanContentDistanceCalculator.getInstance();
    private final QueryPlanCostDistanceCalculator costDistanceCalculator = QueryPlanCostDistanceCalculator.getInstance();

    private static final QueryPlanInterestingnessCalculator INSTANCE = new QueryPlanInterestingnessCalculator();

    public static QueryPlanInterestingnessCalculator getInstance(){
        return INSTANCE;
    }

    public double calculateInterestingness(QueryPlan queryPlan1, QueryPlan queryPlan2, double maxCost, double minCost){
        double structuralDistance, contentDistance, costDistance;

        if(queryPlan1.getType() == QueryPlan.Type.QEP || queryPlan2.getType() == QueryPlan.Type.QEP){
            if(queryPlan1.hasDistance() && queryPlan2.hasDistance()){
                structuralDistance = Math.abs(queryPlan1.getStructuralDistance() - queryPlan2.getStructuralDistance());
                contentDistance = Math.abs(queryPlan1.getContentDistance() - queryPlan2.getContentDistance());
                costDistance = Math.abs(queryPlan1.getCostDistance() - queryPlan2.getCostDistance());
            }else{
                structuralDistance = structureDistanceCalculator.calculate(queryPlan1, queryPlan2);
                contentDistance = contentDistanceCalculator.calculate(queryPlan1, queryPlan2);
                costDistance = costDistanceCalculator.calculate(queryPlan1, queryPlan2, maxCost, minCost);

                if (queryPlan1.getType() != QueryPlan.Type.QEP){
                    queryPlan1.setDistance(structuralDistance, contentDistance, costDistance);
                }else{
                    queryPlan2.setDistance(structuralDistance, contentDistance, costDistance);
                }
            }
        }else{
            structuralDistance = structureDistanceCalculator.calculate(queryPlan1, queryPlan2);
            contentDistance = contentDistanceCalculator.calculate(queryPlan1, queryPlan2);
            costDistance = costDistanceCalculator.calculate(queryPlan1, queryPlan2, maxCost, minCost);
        }

        double distance = calculateDistance(structuralDistance, contentDistance, costDistance);
        double relevanceScore = calculateRelevanceScore(structuralDistance, contentDistance, costDistance);

        if(queryPlan1.getType() == QueryPlan.Type.QEP){
            queryPlan2.setRelevance(relevanceScore);
        }else if(queryPlan2.getType() == QueryPlan.Type.QEP){
            queryPlan1.setRelevance(relevanceScore);
        }

        return distance / 2 + relevanceScore / 2;
    }

    public double calculateDistance(double structuralDistance, double contentDistance, double costDistance){
        return (structuralDistance + contentDistance + costDistance) / 3;
    }

    public double calculateRelevanceScore(double structuralDistance, double contentDistance, double costDistance){
        return (structuralDistance + contentDistance + costDistance - structuralDistance * contentDistance
                - 2 * structuralDistance * costDistance - 2 * contentDistance * costDistance + 2 * structuralDistance * contentDistance * costDistance);
    }
}
