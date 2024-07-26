package com.chlorine.base.mvc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class JdbcTemplateUtil {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // 查询多行数据，返回Map列表，每个Map代表一行数据
    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    // 执行更新操作（INSERT, UPDATE, DELETE）
    public int update(String sql) {
        return jdbcTemplate.update(sql);
    }

    // 批量更新操作
    public int[] batchUpdate(String sql) {
        return jdbcTemplate.batchUpdate(sql);
    }

    // 执行任意SQL，通常用于DDL语句
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }
}
