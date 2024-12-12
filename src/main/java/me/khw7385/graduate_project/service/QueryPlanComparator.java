package me.khw7385.graduate_project.service;

import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.TreeNode;
import me.khw7385.graduate_project.dto.QueryPlanCompareDto;
import me.khw7385.graduate_project.repository.SelectedQueryPlanContainer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryPlanComparator {
    private final SelectedQueryPlanContainer selectedQueryPlanContainer = SelectedQueryPlanContainer.getInstance();

    public QueryPlanCompareDto compareQueryPlan(String userId, Integer index){
        QueryPlan qep = selectedQueryPlanContainer.getQueryPlan(userId, 0);
        QueryPlan aqp = selectedQueryPlanContainer.getQueryPlan(userId, index);

        qep.assignRelationNameForAllNodes();
        aqp.assignRelationNameForAllNodes();

        Map<String, String> qepRelationOperations = new HashMap<>();
        Map<String, String> aqpRelationOperations = new HashMap<>();

        qep.extractOperationByRelation(qepRelationOperations);
        aqp.extractOperationByRelation(aqpRelationOperations);

        List<TreeNode> qepJoinSubtrees = qep.extractJoinSubTress();
        List<TreeNode> aqpJoinSubtrees = aqp.extractJoinSubTress();

        List<String> sameJoinSubtrees = findSameJoinSubtree(qepJoinSubtrees, aqpJoinSubtrees);

        return QueryPlanCompareDto.builder()
                .relationOperations1(qepRelationOperations)
                .relationOperations2(aqpRelationOperations)
                .sameJoinTrees(sameJoinSubtrees)
                .build();
    }

    private List<String> findSameJoinSubtree(List<TreeNode> joinSubtrees1, List<TreeNode> joinSubtrees2){
        List<String> result = new ArrayList<>();

        for (int i = joinSubtrees1.size() - 1; i >= 0; i--) {
            for (int j = joinSubtrees2.size() - 1; j >= 0 ; j--) {
                TreeNode joinSubtree1 = joinSubtrees1.get(i);
                TreeNode joinSubtree2 = joinSubtrees2.get(j);

                if(!joinSubtree1.getRelName().equals(joinSubtree2.getRelName())) continue;

                if(isSameJoinSubtree(joinSubtree1, joinSubtree2)){
                    Iterator<String> iterator = result.iterator();

                    while (iterator.hasNext()) {
                        String item = iterator.next();
                        if (joinSubtree1.getRelName().contains(item)) {
                            iterator.remove();
                        }
                    }
                    result.add(joinSubtree1.getRelName());
                }
            }
        }
        return result;
    }

    private boolean isSameJoinSubtree(TreeNode joinSubtree1, TreeNode joinSubtree2){
        List<String> operations1 = joinSubtree1.extractOperationAndRelation();
        List<String> operations2 = joinSubtree2.extractOperationAndRelation();

        for(int i = 0 ; i < Math.min(operations1.size(), operations2.size()); i++){
            if(!operations1.get(i).equals(operations2.get(i))){
                return false;
            }
        }
        return true;
    }


}
