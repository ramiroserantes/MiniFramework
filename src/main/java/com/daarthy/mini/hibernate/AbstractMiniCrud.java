package com.daarthy.mini.hibernate;

import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public abstract class AbstractMiniCrud<E, T> implements MiniCrud<E, T> {

    @Override
    public E save(E entity) {
        String table = getTableName(entity);
        List<String> columns = getColumnNames(entity);

        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner columnPlaceholders = new StringJoiner(", ");
        for (String column : columns) {
            columnNames.add(column);
            columnPlaceholders.add("?");
        }

        // Construir la consulta SQL INSERT INTO
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)",
                table, columnNames.toString(), columnPlaceholders.toString());

        try (Connection connection = datasource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            // Establecer los valores de los parámetros en la declaración preparada
            int parameterIndex = 1;
            for (String column : columns) {
                Field field = entity.getClass().getDeclaredField(column);
                field.setAccessible(true);
                Object value = field.get(entity);
                preparedStatement.setObject(parameterIndex++, value);
            }

            // Ejecutar la consulta INSERT
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting entity failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                E insertedEntity = (E) entity.getClass().getDeclaredConstructor().newInstance();
                for (String column : columns) {
                    Field field = entity.getClass().getDeclaredField(column);
                    field.setAccessible(true);
                    Object value = generatedKeys.getObject(column);
                    if (value != null) {
                        field.set(insertedEntity, value);
                    }
                }
                return insertedEntity;
            } else {
                throw new SQLException("Inserting entity failed, no generated keys obtained.");
            }

        } catch (SQLException | NoSuchFieldException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error saving entity: " + e.getMessage(), e);
        }
    }

    @Override
    public E findById(T entityId) {
        return null;
    }

    @Override
    public void update(E entity) {

    }

    @Override
    public void delete(T entityId) {

    }

    /*************************************************************
     * Internal Variables
     *************************************************************/
    private HikariDataSource datasource;

    protected AbstractMiniCrud(HikariDataSource datasource) {
        this.datasource = datasource;
    }

    /*************************************************************
     * Internal Methods
     *************************************************************/
    private String getTableName(E entity) {
        return entity.getClass().getSimpleName();
    }

    private List<String> getColumnNames(E entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
