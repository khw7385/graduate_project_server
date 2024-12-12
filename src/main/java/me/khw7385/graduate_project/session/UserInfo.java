package me.khw7385.graduate_project.session;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import me.khw7385.graduate_project.repository.QueryPlanRepository;
import me.khw7385.graduate_project.repository.SelectedQueryPlanContainer;

public class UserInfo implements HttpSessionBindingListener {
    private final SelectedQueryPlanContainer selectedQueryPlanContainer = SelectedQueryPlanContainer.getInstance();
    private final QueryPlanRepository queryPlanRepository = QueryPlanRepository.getInstance();
    String userId;
    public UserInfo() {
    }
    public UserInfo(String userId) {
        this.userId = userId;
    }

    public String getId(){
        return userId;
    }
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if(selectedQueryPlanContainer.hasSelectedQueryPlan(userId)) selectedQueryPlanContainer.removeQueryPlans(userId);
        if(queryPlanRepository.hasUserQueryPlanInfo(userId)) queryPlanRepository.removeUserQueryInfo(userId);
    }
}
