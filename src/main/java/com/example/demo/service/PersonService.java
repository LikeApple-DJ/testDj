package com.example.demo.service;

import com.example.demo.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟对接外部人员系统
 * 根据 callerId 返回人员维度信息（type/level/dept）
 */
@Service
public class PersonService {

    // 模拟外部人员系统的数据
    private static final Map<String, UserInfo> MOCK_PERSON_DB = new HashMap<>();

    static {
        // 初始化模拟数据
        MOCK_PERSON_DB.put("user001", createUser("user001", "张三", "正式", "P7", "技术部"));
        MOCK_PERSON_DB.put("user002", createUser("user002", "李四", "正式", "P6", "技术部"));
        MOCK_PERSON_DB.put("user003", createUser("user003", "王五", "外包", "P5", "产品部"));
        MOCK_PERSON_DB.put("user004", createUser("user004", "赵六", "正式", "P8", "运营部"));
        MOCK_PERSON_DB.put("user005", createUser("user005", "陈七", "实习生", "P5", "技术部"));
        MOCK_PERSON_DB.put("user006", createUser("user006", "刘八", "外包", "P6", "产品部"));
        MOCK_PERSON_DB.put("user007", createUser("user007", "周九", "正式", "P7", "运营部"));
        MOCK_PERSON_DB.put("user008", createUser("user008", "吴十", "正式", "P6", "技术部"));
        MOCK_PERSON_DB.put("user009", createUser("user009", "郑十一", "外包", "P5", "技术部"));
        MOCK_PERSON_DB.put("user010", createUser("user010", "孙十二", "正式", "P7", "产品部"));
    }

    private static UserInfo createUser(String callerId, String name, String type, String level, String dept) {
        UserInfo user = new UserInfo();
        user.setCallerId(callerId);
        user.setDisplayName(name);
        user.setCallerType(type);
        user.setCallerLevel(level);
        user.setCallerDept(dept);
        return user;
    }

    /**
     * 根据 callerId 查询人员信息（模拟对接外部系统）
     */
    public UserInfo getPersonInfo(String callerId) {
        // 模拟外部系统调用延迟
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return MOCK_PERSON_DB.get(callerId);
    }
}