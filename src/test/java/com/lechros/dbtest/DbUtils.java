package com.lechros.dbtest;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

@TestComponent
public class DbUtils {

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, String> tableNameIdColumnNameMappings;

    @PostConstruct
    private void loadTableNames() {
        tableNameIdColumnNameMappings = entityManager.getMetamodel().getEntities().stream()
            .collect(Collectors.toMap(this::getTableName, this::getIdColumnName));
    }

    /**
     * 모든 엔터티 테이블의 데이터를 삭제하고, 각 테이블의 ID 컬럼을 1로 초기화합니다.
     */
    @Transactional
    public void resetAllTables() {
        entityManager.flush();
        executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");
        for (Map.Entry<String, String> entry : tableNameIdColumnNameMappings.entrySet()) {
            String tableName = entry.getKey();
            String idColumnName = entry.getValue();
            executeUpdate(String.format("TRUNCATE TABLE `%s`", tableName));
            executeUpdate(String.format("ALTER TABLE `%s` ALTER COLUMN `%s` RESTART WITH 1", tableName, idColumnName));
        }
        executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void executeUpdate(String sql) {
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    private String getTableName(EntityType<?> entity) {
        Table table = entity.getJavaType().getAnnotation(Table.class);
        if (nonNull(table) && hasText(table.name())) {
            return table.name();
        }
        return javaNameToSqlName(entity.getName());
    }

    private String getIdColumnName(EntityType<?> entity) {
        Field field = findIdField(entity).orElseThrow(() ->
            new RuntimeException(String.format("Entity '%s' has no @Id field", entity.getName())));

        Column column = field.getAnnotation(Column.class);
        if (nonNull(column) && hasText(column.name())) {
            return column.name();
        }
        return javaNameToSqlName(field.getName());
    }

    private Optional<Field> findIdField(EntityType<?> entity) {
        for (Field field : entity.getJavaType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    private String javaNameToSqlName(String javaName) {
        if (containsConsecutiveUppers(javaName)) {
            return javaName;
        }
        return camelCaseToSnakeCase(javaName);
    }

    private boolean containsConsecutiveUppers(String string) {
        return string.matches(".*?[A-Z]{2,}.*?");
    }

    private String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("(?<!^)[A-Z]", "_$0");
    }
}
