package me.khw7385.graduate_project.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class QueryPlan {
    private double cost;
    private TreeNode root;
    @Setter
    private Type type;

    private final List<String> structuralHashes = new ArrayList<>();
    private final List<String> contents = new ArrayList<>();
    private Double structuralDistance;
    private Double contentDistance;
    private Double costDistance;
    public QueryPlan(double cost, TreeNode node) {
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
    public List<String> extractAllContents(){
        contents.addAll(root.extractContents());
        return contents;
    }
    public static QueryPlan fromMap(Map<String, Object> data){
        double cost = (Double) data.get("total_cost");
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

    public enum Type{
        QEP, AQP
    }
}
