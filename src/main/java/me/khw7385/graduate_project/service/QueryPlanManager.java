package me.khw7385.graduate_project.service;

import lombok.RequiredArgsConstructor;
import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.util.QueryPlanFileReader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryPlanManager {
    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();
    private final QueryPlanFileReader queryPlanFileReader;
    public void requestQuery(){
    }

    public void saveQueryPlan(String userId){
        queryPlanRepository.initUserQueryInfo(userId);

        Map<String, Object> data = queryPlanFileReader.readFile(userId);

        List<Object> queryPaths = (List<Object>) data.get("pathlist");

        MinCostInfo minCostQueryPathInfo = getMinCostQueryPathInfo(queryPaths);
        MaxCostInfo maxCostQueryPathInfo = getMaxCostQueryPathInfo(queryPaths);

        for (int i = 0; i < queryPaths.size(); i++) {
            Map<String, Object> path = (Map<String, Object>) queryPaths.get(i);
            QueryPlan queryPlan = QueryPlan.fromMap(path);

            if(i == minCostQueryPathInfo.minIdx){
                queryPlan.setType(QueryPlan.Type.QEP);
                queryPlanRepository.setOptimalQueryPlan(userId, queryPlan);
            }else{
                if(i == maxCostQueryPathInfo.maxIdx) queryPlanRepository.setMaxCost(userId, maxCostQueryPathInfo.maxvalue);
                queryPlan.setType(QueryPlan.Type.AQP);
                queryPlanRepository.addQueryPlan(userId, queryPlan);
            }
        }
    }

    private MinCostInfo getMinCostQueryPathInfo(List<Object> queryPaths){
        Map<String, Object> queryPath = (Map<String, Object>) queryPaths.get(0);
        double minCost = (Double)queryPath.get("total_cost");
        int minIdx = 0;

        for (int i = 0; i < queryPaths.size(); i++) {
            queryPath = (Map<String, Object>)queryPaths.get(i);
            double cost = (Double)queryPath.get("total_cost");
            if(cost < minCost){
                minIdx = i;
                minCost = cost;
            }
        }
        return new MinCostInfo(minCost, minIdx);
    }

    private MaxCostInfo getMaxCostQueryPathInfo(List<Object> queryPaths){
        Map<String, Object> queryPath = (Map<String, Object>) queryPaths.get(0);
        double maxCost = (Double) queryPath.get("total_cost");
        int maxIdx = 0;

        for(int i = 0; i <queryPaths.size(); i++){
            queryPath = (Map<String, Object>)queryPaths.get(i);
            double cost = (Double)queryPath.get("total_cost");
            if(cost > maxCost){
                maxIdx = i;
                maxCost = cost;
            }
        }

        return new MaxCostInfo(maxCost, maxIdx);
    }
    public record MinCostInfo(double minValue, int minIdx){}
    public record MaxCostInfo(double maxvalue, int maxIdx){}
}
