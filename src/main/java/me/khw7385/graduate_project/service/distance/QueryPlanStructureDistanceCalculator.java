package me.khw7385.graduate_project.service.distance;

import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.domain.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryPlanStructureDistanceCalculator {
    public static final QueryPlanStructureDistanceCalculator INSTANCE = new QueryPlanStructureDistanceCalculator();
    public static QueryPlanStructureDistanceCalculator getInstance(){
        return INSTANCE;
    }

    public double calculate(QueryPlan queryPlan1, QueryPlan queryPlan2) {
        int kernelValue_1_1 = calculateSubtreeKernel(queryPlan1, queryPlan1);
        int kernelValue_2_2 = calculateSubtreeKernel(queryPlan2, queryPlan2);

        int kernelValue_1_2 = calculateSubtreeKernel(queryPlan1, queryPlan2);

        double regularKernelValue = kernelValue_1_2 / Math.sqrt(kernelValue_1_1 * kernelValue_2_2);

        return Math.sqrt(1 - regularKernelValue);
    }

    private int calculateSubtreeKernel(QueryPlan queryPlan1, QueryPlan queryPlan2){
        List<String> hashesOfQueryPlan1 = deriveStructuralHashes(queryPlan1);
        List<String> hashesOfQueryPlan2 = deriveStructuralHashes(queryPlan2);

        Map<String, Integer> hashMap = new HashMap<>();
        for (String hash : hashesOfQueryPlan1){
            hashMap.put(hash, hashMap.getOrDefault(hash, 0) + 1);
        }

        int kernelValue = 0;

        for (String hash: hashesOfQueryPlan2){
            kernelValue += hashMap.getOrDefault(hash, 0);
        }

        return kernelValue;
    }

    private List<String> deriveStructuralHashes(QueryPlan queryPlan){
        if(!queryPlan.isStructuralHashesEmpty()) return queryPlan.getStructuralHashes();

        List<TreeNode> subtrees = queryPlan.extractAllSubTrees();
        List<String> structuralHashes = new ArrayList<>();
        for (TreeNode subTree: subtrees){
            structuralHashes.add(generateStructuralHash(subTree));
        }
        queryPlan.addAllStructuralHashes(structuralHashes);

        return structuralHashes;
    }

    private String generateStructuralHash(TreeNode node){
        if(node == null)
            return "#";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (TreeNode child: node.getChildren()){
            sb.append(generateStructuralHash(child));
        }
        sb.append("]");
        return sb.toString();
    }
}
