package com.dt.example.metrics;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 进程内人员字典，维护调用人的组织维度。
 */
@Component
public class PersonDictionary {

    private final Map<String, Person> persons = new LinkedHashMap<>();

    public PersonDictionary() {
        register(new Person("u001", "张三", "正式员工", "P6", "研发部"));
        register(new Person("u002", "李四", "正式员工", "P5", "产品部"));
        register(new Person("u003", "王五", "外包", "P4", "研发部"));
        register(new Person("u004", "赵六", "实习生", "P3", "市场部"));
    }

    private void register(Person person) {
        persons.put(person.id(), person);
    }

    public Person find(String id) {
        return persons.getOrDefault(id, new Person(id, id, "未知", "未知", "未知"));
    }

    public List<Person> all() {
        return List.copyOf(persons.values());
    }
}