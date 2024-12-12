package me.khw7385.graduate_project.repository;

import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.UserQueryPlanInfo;

import java.util.HashMap;
import java.util.Map;


public class QueryPlanRepository {
    private final Map<String, UserQueryPlanInfo> queryInfoMap = new HashMap<>();
    private static final QueryPlanRepository INSTANCE = new QueryPlanRepository();

    public static QueryPlanRepository getInstance(){
        return INSTANCE;
    }

    public void initUserQueryInfo(String userId){
        queryInfoMap.put(userId, new UserQueryPlanInfo());
    }

    public boolean hasUserQueryPlanInfo(String userId){
        return queryInfoMap.containsKey(userId);
    }
    public void addQueryPlan(String userId, QueryPlan queryPlan){
        UserQueryPlanInfo userQueryPlanInfo = queryInfoMap.get(userId);
        userQueryPlanInfo.addQueryPlan(queryPlan);
    }

    public UserQueryPlanInfo findByUserId(String userId){
        return queryInfoMap.get(userId);
    }

    public void removeUserQueryInfo(String userId){
        queryInfoMap.remove(userId);
    }

    public void setOptimalQueryPlan(String userId, QueryPlan queryPlan){
        queryPlan.setDistance(0 ,0 ,0);
        queryPlan.setRelevance(0);
        UserQueryPlanInfo userQueryPlanInfo = queryInfoMap.get(userId);
        userQueryPlanInfo.setOptimalQueryPlan(queryPlan);
        userQueryPlanInfo.setMinCost(queryPlan.getCost());
    }

    public void setMaxCost(String userId, double maxValue){
        UserQueryPlanInfo userQueryPlanInfo = queryInfoMap.get(userId);
        userQueryPlanInfo.setMaxCost(maxValue);
    }
}
