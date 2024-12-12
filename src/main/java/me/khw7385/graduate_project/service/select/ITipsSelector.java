package me.khw7385.graduate_project.service.select;

import lombok.Getter;
import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.UserQueryPlanInfo;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.service.QueryPlanInterestingnessCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITipsSelector {
    private static final ITipsSelector INSTANCE = new ITipsSelector();

    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();
    private final Map<String, QueryPlan> preferredQueryPlans = new HashMap<>();
    private final QueryPlanInterestingnessCalculator interestingnessCalculator = QueryPlanInterestingnessCalculator.getInstance();

    private static final double CATEGORY_INFINITY = Math.sqrt(3);
    private static final double STEP_SIZE = 0.5;

    public static ITipsSelector getInstance(){
        return INSTANCE;
    }

    public QueryPlan select(String userId){
        UserQueryPlanInfo queryPlanInfo = queryPlanRepository.findByUserId(userId);
        QueryPlan optimalQueryPlan = queryPlanInfo.getOptimalQueryPlan();

        if(preferredQueryPlans.containsKey(userId)){
            QueryPlan preferredQueryPlan = preferredQueryPlans.get(userId);
            Category category = findCategory(preferredQueryPlan);

            double newSDist = optimalQueryPlan.getStructuralDistance() - STEP_SIZE * (category.getSDist() - preferredQueryPlan.getStructuralDistance());
            double newCDist = optimalQueryPlan.getContentDistance() - STEP_SIZE * (category.getCDist() - preferredQueryPlan.getContentDistance());
            double newCostDist = optimalQueryPlan.getCostDistance() - STEP_SIZE * (category.getCostDist() - preferredQueryPlan.getCostDistance());

            optimalQueryPlan.setDistance(newSDist, newCDist, newCostDist);

        }
        QueryPlan maxInterestingnessQueryPlan = findMaxInterestingnessQueryPlan(optimalQueryPlan, queryPlanInfo.getAlternativeQueryPlans(), queryPlanInfo.getMaxCost(), queryPlanInfo.getMinCost());

        preferredQueryPlans.put(userId, maxInterestingnessQueryPlan);
        queryPlanInfo.removeQueryPlan(maxInterestingnessQueryPlan);

        return maxInterestingnessQueryPlan;
    }

    private QueryPlan findMaxInterestingnessQueryPlan(QueryPlan optimalQueryPlan, List<QueryPlan> alternativeQueryPlans, double maxCost, double minCost){
        double maxInterestingness = 0;
        QueryPlan maxQueryPlan = null;
        for (QueryPlan queryPlan : alternativeQueryPlans){
            double interestingness = interestingnessCalculator.calculateInterestingness(optimalQueryPlan, queryPlan, maxCost, minCost);
            if(interestingness > maxInterestingness){
                maxInterestingness = interestingness;
                maxQueryPlan = queryPlan;
            }
        }
        return maxQueryPlan;
    }

    private Category findCategory(QueryPlan queryPlan){
        double minDistance = CATEGORY_INFINITY;
        Category nearestCategory = null;

        for(Category category: Category.values()){
            double distance = Math.sqrt(Math.pow(queryPlan.getStructuralDistance() - category.sDist, 2)
                    + Math.pow(queryPlan.getContentDistance() - category.cDist, 2)
                    + Math.pow(queryPlan.getCostDistance() - category.costDist, 2));
            if(distance < minDistance){
                minDistance = distance;
                nearestCategory = category;
            }
        }
        return nearestCategory;
    }

    @Getter
    public enum Category{
        S_C_COST_0_0_0(0, 0, 0),
        S_C_COST_0_0_1(0, 0, 1),
        S_C_COST_0_1_0(0, 1, 0),
        S_C_COST_1_0_0(1, 0, 0),
        S_C_COST_0_1_1(0, 1, 1),
        S_C_COST_1_0_1(1, 0, 1),
        S_C_COST_1_1_0(1, 1, 0),
        S_C_COST_1_1_1(1, 1, 1);

        private int sDist;
        private int cDist;
        private int costDist;

        Category(int sDist, int cDist, int costDist) {
            this.sDist = sDist;
            this.cDist = cDist;
            this.costDist = costDist;
        }
    }
}
