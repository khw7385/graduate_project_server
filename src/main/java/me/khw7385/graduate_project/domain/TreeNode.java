package me.khw7385.graduate_project.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class TreeNode {
    private String operation;
    private double cost;
    private String relName;
    private List<TreeNode> children;
    public TreeNode(String operation, double cost, String relName, List<TreeNode> children) {
        this.operation = operation;
        this.cost = cost;
        this.relName = relName;
        this.children = children;
    }

    public List<TreeNode> extractSubTrees(){
        List<TreeNode> subtrees = new ArrayList<>();
        subtrees.add(this);
        for (TreeNode node: children){
            subtrees.addAll(node.extractSubTrees());
        }
        return subtrees;
    }

    public List<String> extractContents(){
        List<String> contents = new ArrayList<>();
        contents.add(operation);
        for (TreeNode node: children){
            contents.addAll(node.extractContents());
        }
        return contents;
    }

    public static TreeNode fromMap(Map<String, Object> data){
        String operation = (String) data.get("pathtype");
        Double cost = (Double) data.get("total_cost");
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

        return new TreeNode(operation, cost, relName, children);
    }
}
