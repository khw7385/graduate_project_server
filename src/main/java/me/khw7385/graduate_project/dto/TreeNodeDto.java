package me.khw7385.graduate_project.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class TreeNodeDto {
    private String operation;
    private String cost;
    private String relName;
    private final List<TreeNodeDto> children = new ArrayList<>();

    public void addChild(TreeNodeDto treeNodeDto){
        children.add(treeNodeDto);
    }
}
