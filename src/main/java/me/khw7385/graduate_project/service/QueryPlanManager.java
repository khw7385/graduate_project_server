package me.khw7385.graduate_project.service;

import lombok.RequiredArgsConstructor;
import me.khw7385.graduate_project.domain.QueryPlan;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.util.QueryPlanFileReader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class QueryPlanManager {
    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();
    private final QueryPlanFileReader queryPlanFileReader = QueryPlanFileReader.getInstance();
    private final JdbcTemplate jdbcTemplate;

    public String requestQuery(String sql){
        String preprocessedSql = preprocessSql(sql);

        String userId = UUID.randomUUID().toString();
        String requestSql = String.format("/*%s*/%s", userId, sql);
        jdbcTemplate.query(requestSql, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return null;
            }
        });

        return userId;
    }

    public void saveQueryPlan(String userId){
        queryPlanRepository.initUserQueryInfo(userId);

        Map<String, Object> data = queryPlanFileReader.readFile(userId);

        List<Object> queryPaths = (List<Object>) data.get("pathlist");

        MinCostInfo minCostQueryPathInfo = getMinCostQueryPathInfo(queryPaths);
        MaxCostInfo maxCostQueryPathInfo = getMaxCostQueryPathInfo(queryPaths);

        for (int i = 0; i < queryPaths.size(); i++) {
            Map<String, Object> path = (Map<String, Object>) queryPaths.get(i);
            QueryPlan queryPlan = QueryPlan.fromMap(path);

            if(i == minCostQueryPathInfo.minIdx){
                queryPlan.setType(QueryPlan.Type.QEP);
                queryPlanRepository.setOptimalQueryPlan(userId, queryPlan);
            }else{
                if(i == maxCostQueryPathInfo.maxIdx) queryPlanRepository.setMaxCost(userId, maxCostQueryPathInfo.maxvalue);
                queryPlan.setType(QueryPlan.Type.AQP);
                queryPlanRepository.addQueryPlan(userId, queryPlan);
            }
        }
    }

    private String preprocessSql(String sql){
        // 주석 제거
        String regexComments = "(?m)^\\s*--.*|/\\*[\\s\\S]*?\\*/";
        Pattern pattern = Pattern.compile(regexComments);
        Matcher matcher = pattern.matcher(sql);

        String sqlHavingNoComments = matcher.replaceAll("");

        // 앞뒤 공백 제거
        String sqlNoWhite = sqlHavingNoComments.trim();

        // 한 줄로 만들기
        String sqlOneLine = sqlNoWhite.replaceAll("\n", " ");

        if(!sqlOneLine.toUpperCase().startsWith("SELECT")){
            throw new IllegalArgumentException("Not Select SQL");
        }

        return matcher.replaceAll("");
    }

    private MinCostInfo getMinCostQueryPathInfo(List<Object> queryPaths){
        Map<String, Object> queryPath = (Map<String, Object>) queryPaths.get(0);
        double minCost;

        if(queryPath.get("pathtype").equals("Limit")){
            Map<String, Object> nextData = (Map<String, Object>) queryPath.get("path");
            minCost = (Double)nextData.get("total_cost");
        }else{
            minCost = (Double) queryPath.get("total_cost");
        }
        int minIdx = 0;

        for (int i = 0; i < queryPaths.size(); i++) {
            queryPath = (Map<String, Object>)queryPaths.get(i);
            double cost;
            if(queryPath.get("pathtype").equals("Limit")){
                Map<String, Object> nextData = (Map<String, Object>) queryPath.get("path");
                cost = (Double)nextData.get("total_cost");
            }else{
                cost = (Double) queryPath.get("total_cost");
            }
            if(cost < minCost){
                minIdx = i;
                minCost = cost;
            }
        }
        return new MinCostInfo(minCost, minIdx);
    }

    private MaxCostInfo getMaxCostQueryPathInfo(List<Object> queryPaths){
        Map<String, Object> queryPath = (Map<String, Object>) queryPaths.get(0);
        double maxCost;

        if(queryPath.get("pathtype").equals("Limit")){
            Map<String, Object> nextData = (Map<String, Object>) queryPath.get("path");
            maxCost = (Double)nextData.get("total_cost");
        }else{
            maxCost = (Double) queryPath.get("total_cost");
        }

        int maxIdx = 0;

        for(int i = 0; i <queryPaths.size(); i++){
            queryPath = (Map<String, Object>)queryPaths.get(i);
            double cost;

            if(queryPath.get("pathtype").equals("Limit")){
                Map<String, Object> nextData = (Map<String, Object>) queryPath.get("path");
                cost = (Double)nextData.get("total_cost");
            }else{
                cost = (Double) queryPath.get("total_cost");
            }
            if(cost > maxCost){
                maxIdx = i;
                maxCost = cost;
            }
        }

        return new MaxCostInfo(maxCost, maxIdx);
    }
    public record MinCostInfo(double minValue, int minIdx){}
    public record MaxCostInfo(double maxvalue, int maxIdx){}
}
