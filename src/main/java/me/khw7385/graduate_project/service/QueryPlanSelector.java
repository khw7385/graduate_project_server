package me.khw7385.graduate_project.service;

import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.TreeNode;
import me.khw7385.graduate_project.domain.UserQueryPlanInfo;
import me.khw7385.graduate_project.dto.QueryPlanDistanceDto;
import me.khw7385.graduate_project.dto.QueryPlanDto;
import me.khw7385.graduate_project.dto.TreeNodeDto;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.repository.SelectedQueryPlanContainer;
import me.khw7385.graduate_project.service.select.BTipsSelector;
import me.khw7385.graduate_project.service.select.ITipsSelector;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueryPlanSelector {
    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();
    private final BTipsSelector bTipsSelector = BTipsSelector.getInstance();
    private final ITipsSelector iTipsSelector = ITipsSelector.getInstance();
    private final SelectedQueryPlanContainer selectedQueryPlanContainer = SelectedQueryPlanContainer.getInstance();

    public List<QueryPlanDistanceDto> select(String userId, int selectedValue, Integer number){
        List<QueryPlan> queryPlans = new ArrayList<>();

        UserQueryPlanInfo queryPlanInfo = queryPlanRepository.findByUserId(userId);

        if(!selectedQueryPlanContainer.hasSelectedQueryPlan(userId)){
            selectedQueryPlanContainer.initUserContainer(userId);
            queryPlans.add(queryPlanInfo.getOptimalQueryPlan());
        }else{
            if(queryPlanInfo.getAlternativeQueryPlanLength() == 0){
                throw new NoSuchElementException("No Query Plan");
            }
            queryPlans.add(iTipsSelector.select(userId));
        }

        if(selectedValue == 0){
            if(!selectedQueryPlanContainer.hasSelectedQueryPlan(userId)){
                selectedQueryPlanContainer.initUserContainer(userId);
                queryPlans.add(queryPlanInfo.getOptimalQueryPlan());
            }
            int length = queryPlanInfo.getAlternativeQueryPlanLength();
            queryPlans.addAll(bTipsSelector.select(userId, Math.min(length, number - 1)));
        }

        selectedQueryPlanContainer.addQueryPlans(userId, queryPlans);

        return toQueryPlanDistanceDtoList(selectedQueryPlanContainer.getQueryPlans(userId));
    }

    public QueryPlanDto getQueryPlan(String userId, int index){
        QueryPlan queryPlan = selectedQueryPlanContainer.getQueryPlan(userId, index);

        return toQueryPlanDto(queryPlan);
    }

    private List<QueryPlanDistanceDto> toQueryPlanDistanceDtoList(List<QueryPlan> queryPlans){
        return queryPlans.stream()
                .map(this::toQueryPlanDistanceDto)
                .collect(Collectors.toList());
    }

    private QueryPlanDistanceDto toQueryPlanDistanceDto(QueryPlan queryPlan){
        return QueryPlanDistanceDto.builder()
                .sDist(queryPlan.getStructuralDistance())
                .cDist(queryPlan.getContentDistance())
                .cost(queryPlan.getCost())
                .relevance(queryPlan.getRelevance())
                .build();
    }

    private QueryPlanDto toQueryPlanDto(QueryPlan queryPlan){
        QueryPlanDto queryPlanDto = new QueryPlanDto();

        queryPlanDto.setRoot(toTreeNodeDto(queryPlan.getRoot()));

        return queryPlanDto;
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
