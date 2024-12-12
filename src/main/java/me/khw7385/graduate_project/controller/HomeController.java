package me.khw7385.graduate_project.controller;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.khw7385.graduate_project.dto.QueryPlanCompareDto;
import me.khw7385.graduate_project.dto.QueryPlanDistanceDto;
import me.khw7385.graduate_project.dto.QueryPlanDto;
import me.khw7385.graduate_project.dto.QueryPlanGraphDto;
import me.khw7385.graduate_project.service.QueryPlanComparator;
import me.khw7385.graduate_project.service.QueryPlanGraphInfoGenerator;
import me.khw7385.graduate_project.service.QueryPlanManager;
import me.khw7385.graduate_project.service.QueryPlanSelector;
import me.khw7385.graduate_project.session.UserInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final QueryPlanManager queryPlanManager;
    private final QueryPlanSelector queryPlanSelector;
    private final QueryPlanComparator queryPlanComparator;
    private final QueryPlanGraphInfoGenerator queryPlanGraphInfoGenerator;

    @GetMapping("/")
    public String home(HttpSession session){
        session.invalidate();
        return "page/index.html";
    }

    @GetMapping("/erd")
    public String erd(){
        return "fragments/home/main.html::erd";
    }

    @PostMapping("/run-sql")
    public Object createQueryPlans(@RequestBody SqlRequest request,  Model model, HttpSession session){
        UserInfo userInfo = (UserInfo) session.getAttribute("userId");
        String userId = null;
        Map<String, Object> response = new HashMap<>();

        if(userInfo == null){
            try {
                userId = queryPlanManager.requestQuery(request.getSql());
            }catch(IllegalArgumentException e){
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }catch(DataAccessException e){
                response.put("message", "Invalid SQL Query");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            session.setMaxInactiveInterval(600);
            session.setAttribute("userId", new UserInfo(userId));

            queryPlanManager.saveQueryPlan(userId);
        }else{
            userId = userInfo.getId();
        }

        try {
            List<QueryPlanDistanceDto> queryPlanDistanceDtoList = queryPlanSelector.select(userId, request.selectedValue, request.number);
            queryPlanDistanceDtoList.get(0).setDist0();
            model.addAttribute("results", queryPlanDistanceDtoList);
        }catch(NoSuchElementException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return "fragments/home/main.html::planTable";
    }

    @PostMapping("/clear-userId")
    public ResponseEntity<Object> clearUserId(HttpSession session){
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/one-tree")
    public Object getQueryPlan(@RequestParam("index") Integer index, HttpSession session){
        UserInfo userInfo = (UserInfo) session.getAttribute("userId");
        String userId = null;

        if(userInfo == null){
            return "redirect:/page/index.html";
        }
        userId = userInfo.getId();

        QueryPlanDto result = queryPlanSelector.getQueryPlan(userId, index);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/compare-tree")
    public Object compareTree(@RequestParam("index")Integer index, Model model,HttpSession session){
        UserInfo userInfo = (UserInfo) session.getAttribute("userId");
        String userId = null;

        if(userInfo == null){
            return "redirect:/page/index.html";
        }

        userId = userInfo.getId();
        QueryPlanCompareDto compareData = queryPlanComparator.compareQueryPlan(userId, index);
        QueryPlanDto qep = queryPlanSelector.getQueryPlan(userId, 0);
        QueryPlanDto aqp = queryPlanSelector.getQueryPlan(userId, index);

        model.addAttribute("compareData", compareData);
        model.addAttribute("qep", qep);
        model.addAttribute("aqp", aqp);

        return "page/compare.html";
    }

    @GetMapping("/visualize-difference")
    public Object map(@RequestParam("index")Integer index, Model model, HttpSession session){
        UserInfo userInfo = (UserInfo) session.getAttribute("userId");
        String userId = null;

        if(userInfo == null){
            return "redirect:/page/index.html";
        }

        userId = userInfo.getId();

        QueryPlanGraphDto graphDto = queryPlanGraphInfoGenerator.getGraphInfo(userId, index);
        model.addAttribute("graphData", graphDto);

        return "page/graph.html";
    }
    @Getter @Setter
    static public class SqlRequest{
        private String sql;
        private int selectedValue;
        private Integer number;
    }
}
