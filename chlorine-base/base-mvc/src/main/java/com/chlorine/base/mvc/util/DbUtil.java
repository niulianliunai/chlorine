package com.chlorine.base.mvc.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DbUtil {

    private final DataSource dataSource;

    public DbUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, String> getFieldTypeMap(String tableName) {
        Map<String, String> res = new HashMap<>();
        List<Map<String, Object>> fieldList = query("show full columns from " + tableName);
        fieldList.forEach(field -> {
            String fieldName = String.valueOf(field.getOrDefault("Field", ""));
            String fieldType = String.valueOf(field.getOrDefault("Type", ""));
            res.put(fieldName,fieldType);
        });
        return res;
    }

    public List<Map<String, String>> listField(String tableName) {
        List<Map<String, String>> res = new ArrayList<>();
        List<Map<String, Object>> fieldList = query("show full columns from " + tableName);
        fieldList.forEach(field -> {
            String fieldName = String.valueOf(field.getOrDefault("Field", ""));
            String comment = String.valueOf(field.get("Comment"));
            if (!StringUtils.hasLength(comment)) {
                comment = fieldName;
            }
            String fieldType = String.valueOf(field.getOrDefault("Type", ""));
            Map<String, String> item = new TreeMap<>();
            item.put("fieldName", fieldName);
            item.put("fieldComment", comment);
            item.put("fieldType", fieldType);
            res.add(item);
        });
        return res;
    }

    public void execute(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute(sql);
    }


    public List<Map<String, Object>> query(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList(sql);
    }

    public void insert(String tableName, Map<String, String> dataMap) {
        StringBuilder insertSQL = new StringBuilder("insert into " + tableName + " (" + String.join(",", dataMap.keySet()) + ") values ");
        List<String> list = dataMap.values().stream().map(item -> item.replace("'", "\\'")).collect(Collectors.toList());
        list = list.stream().map(item -> {
            if (item.endsWith("\\")) {
                item = item.substring(0, item.length() - 1);
            }
            return item;
        }).collect(Collectors.toList());
        insertSQL.append(" ('").append(String.join("','", list)).append("'),");
        insertSQL.deleteCharAt(insertSQL.length() - 1);
        insertSQL.append(" ON DUPLICATE KEY UPDATE ").append(dataMap.entrySet().stream().filter(entry -> !entry.getKey().equals("create_time")).map(entry -> {
            String key = entry.getKey();
            String value = entry.getValue().replace("'", "\\'");
            if (value.endsWith("\\")) {
                value = value.substring(0, value.length() - 1);
            }
            return key + "='" + value + "'";
        }).collect(Collectors.joining(",")));
        execute(insertSQL.toString().replace("''", "null").replace("\\\\'", "\\'"));
    }
}
