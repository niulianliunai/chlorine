package com.chlorine.base.mvc.util;

import com.chlorine.base.mvc.annotation.ColumnComment;
import com.chlorine.base.mvc.annotation.TableComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DatabaseCommentUtil implements CommandLineRunner{

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DbUtil dbUtil;

    @Override
    public void run(String... args) throws Exception {
        saveDatabaseComment();
    }
    public void saveDatabaseComment() {
        Set<EntityType<?>> entityTypes = entityManager.getMetamodel().getEntities();

        for (EntityType<?> entityType : entityTypes) {
            Class<?> javaType = entityType.getJavaType();
            TableComment tableComment = javaType.getAnnotation(TableComment.class);
            Table table = javaType.getAnnotation(Table.class);
            if (table != null && StringUtils.hasLength(table.name())) {
                String tableName = table.name();
                if (tableComment != null) {
                    dbUtil.execute(generateTableCommentSQL(tableName, tableComment.value()));
                }

                Map<String, String> fieldTypeMap = dbUtil.getFieldTypeMap(tableName);
                ManagedType<?> managedType = entityManager.getMetamodel().managedType(javaType);
                List<String> fieldSqls = managedType.getAttributes().stream()
                        .filter(a -> a.getJavaMember() instanceof Field)
                        .map(a -> {
                            Field field = (Field) a.getJavaMember();
                            ColumnComment columnComment = field.getAnnotation(ColumnComment.class);
                            if (columnComment != null) {
                                return generateColumnCommentSQL(tableName, field.getName(), fieldTypeMap.get(field.getName()), columnComment.value());
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                fieldSqls.forEach(sql->dbUtil.execute(sql));
            }
        }
    }

    private String generateTableCommentSQL(String tableName, String tableComment) {
        return String.format("ALTER TABLE `%s` COMMENT='%s'", tableName, tableComment);
    }

    private String generateColumnCommentSQL(String tableName, String columnName, String fieldType, String columnComment) {
        return String.format("ALTER TABLE `%s` MODIFY COLUMN `%s` %s COMMENT '%s'", tableName, columnName, fieldType, columnComment);
    }


}
