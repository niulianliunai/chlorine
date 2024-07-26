package com.chlorine.base.mvc.util;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TupleUtil {

    public static List<Map<String, Object>> tupleListToMapList(List<Tuple> tuples, Expression[] fields) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Tuple tuple : tuples) {
            maps.add(tupleToMap(tuple, fields));
        }
        return maps;
    }

    public static Map<String, Object> tupleToMap(Tuple tuple, Expression[] fields) {
        if(tuple==null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (Expression field : fields) {
            String fieldStr = field.toString();
            if (fieldStr.contains(" as ")) {
                map.put(fieldStr.split(" as ")[1].trim(), tuple.get(field));
            } else {
                map.put(fieldStr.split("\\.")[1], tuple.get(field));
            }
        }
        return map;

    }
}
