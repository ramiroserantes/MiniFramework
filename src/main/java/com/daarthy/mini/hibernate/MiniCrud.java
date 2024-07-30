package com.daarthy.mini.hibernate;

public interface MiniCrud <E, T> {
    E save(E entity);
    E findById(T entityId);
    void update(E entity);
    void delete(T entityId);
}
