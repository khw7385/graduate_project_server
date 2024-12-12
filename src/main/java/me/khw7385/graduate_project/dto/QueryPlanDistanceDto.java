package me.khw7385.graduate_project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QueryPlanDistanceDto {
    private String sDist;
    private String cDist;
    private String cost;
    private String relevance;

    @Builder
    public QueryPlanDistanceDto(double sDist, double cDist, double cost, double relevance) {
        this.sDist = String.format("%.2f", sDist);
        this.cDist = String.format("%.2f", cDist);
        this.cost = String.format("%.2f", cost);
        this.relevance = String.format("%.2f", relevance);
    }

    public void setDist0(){
        this.sDist = "0.00";
        this.cDist = "0.00";
    }
}
