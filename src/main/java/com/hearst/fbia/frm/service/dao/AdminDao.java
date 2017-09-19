package com.hearst.fbia.frm.service.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public interface AdminDao {

	public JdbcTemplate getJdbcTemplate();

	public <T> T get(Class<T> clazz, Serializable pk);

	public <T> List<T> getAllEntities(Class<T> clazz);

	public <T> void save(T record);

	public <T> void delete(T entity);

	public <T> void saveOrUpdate(T record);

	public <T> T uniqueResult(String query, Class<T> clazz, Object... argument);

	public <T> List<T> query(String query, Class<T> clazz, Object... argument);
}