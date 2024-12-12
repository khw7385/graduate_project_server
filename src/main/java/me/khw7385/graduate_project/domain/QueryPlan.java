package me.khw7385.graduate_project.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class QueryPlan {
    private Double cost;
    private TreeNode root;
    @Setter
    private Type type;

    private final List<String> structuralHashes = new ArrayList<>();
    private final List<String> contents = new ArrayList<>();
    private Double structuralDistance;
    private Double contentDistance;
    private Double costDistance;
    private Double relevance;
    public QueryPlan(Double cost, TreeNode node) {
        this.cost = cost;
        this.root = node;
        structuralDistance = null;
        contentDistance = null;
        costDistance = null;
    }

    public boolean isStructuralHashesEmpty(){
        return structuralHashes.isEmpty();
    }

    public boolean isContentsEmpty(){
        return contents.isEmpty();
    }
    public void addAllStructuralHashes(List<String> hashes){
        this.structuralHashes.addAll(hashes);
    }
    public List<TreeNode> extractAllSubTrees(){
        return root.extractSubTrees();
    }
    public List<TreeNode> extractJoinSubTress(){return root.extractJoinSubTrees();}
    public List<String> extractAllContents(){
        contents.addAll(root.extractContents());
        return contents;
    }
    public void assignRelationNameForAllNodes(){root.assignAllRelationName();}
    public void extractOperationByRelation(Map<String, String> relationOperationMap){root.extractOperationByRelation(relationOperationMap);}

    public static QueryPlan fromMap(Map<String, Object> data){
        double cost;
        if(data.get("pathtype").equals("Limit")){
            Map<String, Object> nextData = (Map<String, Object>) data.get("path");
            cost = (Double)nextData.get("total_cost");
        }else{
            cost = (Double) data.get("total_cost");
        }

        return new QueryPlan(cost, TreeNode.fromMap(data));
    }

    public boolean hasDistance(){
        return structuralDistance != null || contentDistance != null || costDistance != null;
    }

    public void setDistance(double structuralDistance, double contentDistance, double costDistance){
        this.structuralDistance = structuralDistance;
        this.contentDistance = contentDistance;
        this.costDistance = costDistance;
    }

    public void setRelevance(double relevance){
        this.relevance = relevance;
    }
    public enum Type{
        QEP, AQP
    }
}
