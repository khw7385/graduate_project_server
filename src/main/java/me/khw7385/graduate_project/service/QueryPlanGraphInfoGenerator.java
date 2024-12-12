package me.khw7385.graduate_project.service;

import lombok.RequiredArgsConstructor;
import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.TreeNode;
import me.khw7385.graduate_project.dto.QueryPlanGraphDto;
import me.khw7385.graduate_project.dto.QueryPlanGraphItemDto;
import me.khw7385.graduate_project.dto.TreeNodeDto;
import me.khw7385.graduate_project.repository.SelectedQueryPlanContainer;
import me.khw7385.graduate_project.service.distance.QueryPlanContentDistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryPlanGraphInfoGenerator {
    // 편집 거리를 구하는 용도로 사용한다.
    private final QueryPlanContentDistanceCalculator contentDistanceCalculator = QueryPlanContentDistanceCalculator.getInstance();
    private final SelectedQueryPlanContainer selectedQueryPlanContainer = SelectedQueryPlanContainer.getInstance();

    public QueryPlanGraphDto getGraphInfo(String userId, Integer index){
        List<QueryPlanGraphItemDto> others = new ArrayList<>();

        QueryPlan queryPlan = selectedQueryPlanContainer.getQueryPlan(userId, index);
        List<QueryPlan> queryPlans = selectedQueryPlanContainer.getQueryPlans(userId);

        for (int i = 0; i < queryPlans.size(); i++) {
            if(i == index) continue;
            double editDistance = contentDistanceCalculator.calculateEditDistance(queryPlan, queryPlans.get(i));

            others.add(toQueryPlanGraphItemDto(i, editDistance, queryPlans.get(i)));
        }

        QueryPlanGraphItemDto target = toQueryPlanGraphItemDto(index, null, queryPlan);

        return toQueryPlanGraphDto(target, others);
    }
    private QueryPlanGraphDto toQueryPlanGraphDto(QueryPlanGraphItemDto target, List<QueryPlanGraphItemDto> others){
        QueryPlanGraphDto queryPlanGraphDto = new QueryPlanGraphDto();
        queryPlanGraphDto.setTarget(target);
        queryPlanGraphDto.setOthers(others);

        return queryPlanGraphDto;
    }

    private QueryPlanGraphItemDto toQueryPlanGraphItemDto(int index, Double editDistance, QueryPlan queryPlan){
        QueryPlanGraphItemDto queryPlanGraphItemDto = new QueryPlanGraphItemDto();
        queryPlanGraphItemDto.setIndex(index);
        queryPlanGraphItemDto.setEditDifference(editDistance);
        queryPlanGraphItemDto.setRoot(toTreeNodeDto(queryPlan.getRoot()));
        return queryPlanGraphItemDto;
    }

    private TreeNodeDto toTreeNodeDto(TreeNode treeNode){
        TreeNodeDto treeNodeDto = new TreeNodeDto();
        if(treeNode.hasChildren()){
            for (TreeNode node : treeNode.getChildren()){
                treeNodeDto.addChild(toTreeNodeDto(node));
            }
        }
        treeNodeDto.setCost(String.format("%.2f", treeNode.getCost()));
        treeNodeDto.setOperation(treeNode.getOperation());
        treeNodeDto.setRelName(treeNode.getRelName());

        return treeNodeDto;
    }
}
