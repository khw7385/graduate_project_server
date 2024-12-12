package me.khw7385.graduate_project.repository;

import me.khw7385.graduate_project.domain.QueryPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedQueryPlanContainer {
    private static final SelectedQueryPlanContainer INSTANCE = new SelectedQueryPlanContainer();
    private Map<String, List<QueryPlan>> selectedQueryPlans = new HashMap<>();

    public static SelectedQueryPlanContainer getInstance(){
        return INSTANCE;
    }
    public void initUserContainer(String userId){
        selectedQueryPlans.put(userId, new ArrayList<>());
    }
    public boolean hasSelectedQueryPlan(String userId){
        return selectedQueryPlans.containsKey(userId);
    }

    public void addQueryPlan(String userId, QueryPlan queryPlan){
        selectedQueryPlans.get(userId).add(queryPlan);
    }

    public void addQueryPlans(String userId, List<QueryPlan> queryPlans){
        selectedQueryPlans.get(userId).addAll(queryPlans);
    }
    public QueryPlan getQueryPlan(String userId, int idx){
        return selectedQueryPlans.get(userId).get(idx);
    }

    public List<QueryPlan> getQueryPlans(String userId){
        return selectedQueryPlans.get(userId);
    }

    public void removeQueryPlans(String userId){
        selectedQueryPlans.remove(userId);
    }
}
