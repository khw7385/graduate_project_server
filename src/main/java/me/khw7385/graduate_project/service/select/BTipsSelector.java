package me.khw7385.graduate_project.service.select;

import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.UserQueryPlanInfo;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.service.QueryPlanInterestingnessCalculator;

import java.util.*;

public class BTipsSelector {
    private static final BTipsSelector INSTANCE = new BTipsSelector();
    private final QueryPlanInterestingnessCalculator interestingnessCalculator = QueryPlanInterestingnessCalculator.getInstance();
    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();

    public static BTipsSelector getInstance(){
        return INSTANCE;
    }

    public List<QueryPlan> select(String userId, int k){
        UserQueryPlanInfo queryPlanInfo = queryPlanRepository.findByUserId(userId);

        PriorityQueue<Map.Entry<Double, QueryPlan>> maxHeap = initMaxHeap(queryPlanInfo);
        List<QueryPlan> resultQueryPlans = new ArrayList<>();

        for(int i = 0; i < k; i++){
            Map.Entry<Double, QueryPlan> optTuple = new AbstractMap.SimpleEntry<>(0.0, null);
            List<Map.Entry<Double, QueryPlan>> tmpRecord = new ArrayList<>();

            while(!maxHeap.isEmpty()){
                Map.Entry<Double, QueryPlan> tmpTuple = maxHeap.poll();

                if(tmpTuple.getKey() < optTuple.getKey()) {
                    tmpRecord.add(tmpTuple);
                    break;
                }
                tmpTuple = updateTuple(resultQueryPlans, i - 1, tmpTuple, queryPlanInfo.getMaxCost(),  queryPlanInfo.getMinCost());

                if(tmpTuple.getKey() > optTuple.getKey()){
                    optTuple = tmpTuple;
                }

                tmpRecord.add(tmpTuple);
            }

            resultQueryPlans.add(optTuple.getValue());

            tmpRecord.remove(optTuple);

            maxHeap.addAll(tmpRecord);
        }

        return resultQueryPlans;
    }

    private PriorityQueue<Map.Entry<Double, QueryPlan>> initMaxHeap(UserQueryPlanInfo queryPlanInfo){
        QueryPlan optimalQueryPlan = queryPlanInfo.getOptimalQueryPlan();
        List<QueryPlan> queryPlans = queryPlanInfo.getAlternativeQueryPlans();
        double maxCost = queryPlanInfo.getMaxCost();
        double minCost = queryPlanInfo.getMinCost();

        PriorityQueue<Map.Entry<Double, QueryPlan>> maxHeap = new PriorityQueue<>(
                (pair1, pair2) -> Double.compare(pair2.getKey(), pair1.getKey())
        );

        for (QueryPlan queryPlan: queryPlans){
            double interestValue  = interestingnessCalculator.calculateInterestingness(optimalQueryPlan, queryPlan, maxCost, minCost);
            maxHeap.add(new AbstractMap.SimpleEntry<>(interestValue, queryPlan));
        }
        return maxHeap;
    }

    private boolean isLatest(List<Map.Entry<Double, QueryPlan>> record, int idx, Map.Entry<Double, QueryPlan> tuple, double maxCost, double minCost){
        if(idx < 0) return true;

        return tuple.getKey() < interestingnessCalculator.calculateInterestingness(tuple.getValue(), record.get(idx).getValue(), maxCost, minCost);
    }

    private Map.Entry<Double, QueryPlan> updateTuple(List<QueryPlan> result, int idx, Map.Entry<Double, QueryPlan> tuple, double maxCost, double minCost){
        if(idx < 0) return tuple;

        double interestWithAddedPlan = interestingnessCalculator.calculateInterestingness(tuple.getValue(), result.get(idx), maxCost, minCost);

        if(interestWithAddedPlan >= tuple.getKey()){
            return tuple;
        }

        return new AbstractMap.SimpleEntry<>(interestWithAddedPlan, tuple.getValue());
    }
}
