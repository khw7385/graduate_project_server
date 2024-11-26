package me.khw7385.graduate_project.service.distance;

import me.khw7385.graduate_project.domain.QueryPlan;

import java.util.List;

public class QueryPlanContentDistanceCalculator {
    private static final QueryPlanContentDistanceCalculator INSTANCE = new QueryPlanContentDistanceCalculator();
    public static QueryPlanContentDistanceCalculator getInstance(){
        return INSTANCE;
    }
    public double calculate(QueryPlan queryPlan1, QueryPlan queryPlan2) {
        double editDistance = calculateEditDistance(queryPlan1, queryPlan2);
        return (double)2 * editDistance / (queryPlan1.getContents().size() + queryPlan2.getContents().size() + editDistance);
    }

    private double calculateEditDistance(QueryPlan queryPlan1, QueryPlan queryPlan2){
        List<String> contentsOfQueryPlan1 = deriveContents(queryPlan1);
        List<String> contentsOfQueryPlan2 = deriveContents(queryPlan2);
        int [][] dp = new int[contentsOfQueryPlan1.size() + 1][contentsOfQueryPlan2.size() + 1];

        for (int i = 0; i < contentsOfQueryPlan1.size(); i++){
            dp[i][0] = i;
        }

        for(int i = 0 ; i < contentsOfQueryPlan2.size(); i++){
            dp[0][i] = i;
        }

        for (int i = 1; i < contentsOfQueryPlan1.size() + 1; i++) {
            for (int j = 1; j < contentsOfQueryPlan2.size() + 1; j++){
                if(contentsOfQueryPlan1.get(i - 1).equals(contentsOfQueryPlan2.get(j - 1))){
                    dp[i][j] = dp[i - 1][j -1];
                }else{
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[contentsOfQueryPlan1.size()][contentsOfQueryPlan2.size()];
    }

    private List<String> deriveContents(QueryPlan queryPlan){
        if(queryPlan.isContentsEmpty()) return queryPlan.getContents();
        return queryPlan.extractAllContents();
    }
}
