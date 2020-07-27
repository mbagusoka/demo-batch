package com.learn.demobatch.users.batch;

import com.learn.demobatch.users.entity.User;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
public class Processor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAMES;

    static {
        DEPT_NAMES = new HashMap<>();
        DEPT_NAMES.put("001", "Technology");
        DEPT_NAMES.put("002", "Operations");
        DEPT_NAMES.put("003", "Accounts");
    }

    @Override
    public User process(User user) {
        String deptName = DEPT_NAMES.get(user.getDept());
        user.setDept(deptName);
        return user;
    }
}
