package me.khw7385.graduate_project.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class TreeNode {
    private String operation;
    private Double cost;
    private String relName;
    private List<TreeNode> children;
    public TreeNode(String operation, Double cost, String relName, List<TreeNode> children) {
        this.operation = operation;
        this.cost = cost;
        this.relName = relName;
        this.children = children;
    }

    public boolean hasChildren(){
        return !children.isEmpty();
    }

    public List<TreeNode> extractSubTrees(){
        List<TreeNode> subtrees = new ArrayList<>();
        subtrees.add(this);
        for (TreeNode node: children){
            subtrees.addAll(node.extractSubTrees());
        }
        return subtrees;
    }

    public List<TreeNode> extractJoinSubTrees(){
        List<TreeNode> subtrees = new ArrayList<>();

        if(this.operation.equals("NestLoop")
                || this.operation.equals("HashJoin")
                || this.operation.equals("MergeJoin")
                || this.operation.equals("NestedLoopParam")){
            subtrees.add(this);

            for (TreeNode node: children){
                subtrees.addAll(node.extractJoinSubTrees());
            }
        }

        return subtrees;
    }

    public void assignAllRelationName(){
        StringBuilder name = new StringBuilder();

        if(relName == null) {
            for (int i = 0 ; i < children.size(); i++){
                TreeNode node = children.get(i);
                node.assignAllRelationName();

                if(i > 0){
                    name.append("*");
                }
                name.append(node.relName);
            }
            this.relName = name.toString();
        }
    }

    public void extractOperationByRelation(Map<String, String> relationOperationMap){
        if(this.operation.contains("Join") || this.operation.contains("Scan") || this.operation.contains("NestLoop")){
            relationOperationMap.put(this.relName, this.operation);
        }

        for (TreeNode node: this.children){
            node.extractOperationByRelation(relationOperationMap);
        }
    }

    public List<String> extractContents(){
        List<String> contents = new ArrayList<>();
        contents.add(operation);
        for (TreeNode node: children){
            contents.addAll(node.extractContents());
        }
        return contents;
    }

    public List<String> extractOperationAndRelation(){
        List<String> result = new ArrayList<>();
        result.add(operation);

        if(children.isEmpty()){
            result.add(relName);
        }else{
            for(TreeNode node:children){
                result.addAll(node.extractOperationAndRelation());
            }
        }
        return result;
    }

    public static TreeNode fromMap(Map<String, Object> data){
        String operation = (String) data.get("pathtype");
        Object costObject = data.getOrDefault("total_cost", null);
        Double cost = (costObject instanceof Number) ? ((Number) costObject).doubleValue() : null;
        String relName = (String) data.get("relname");

        List<TreeNode> children = new ArrayList<>();

        if(data.containsKey("outer_join_path")){
            Object outerJoinPath = data.get("outer_join_path");
            if(outerJoinPath instanceof  Map){
                children.add(fromMap((Map<String, Object>) outerJoinPath));
            }
        }

        if(data.containsKey("outer_join_path")){
            Object innerJoinPath = data.get("inner_join_path");
            if(innerJoinPath instanceof Map){
                children.add(fromMap((Map<String, Object>)innerJoinPath));
            }
        }

        if(data.containsKey("path")){
            Object path = data.get("path");
            if(path instanceof  Map){
                children.add(fromMap((Map<String, Object>)path));
            }
        }

        return new TreeNode(operation, cost, relName, children);
    }
}
