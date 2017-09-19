package com.hearst.fbia.frm.service.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDaoImpl implements AdminDao {

	// @Autowired
	// SessionFactory sessionFactory;

	HibernateTemplate hibernateTemplate;

	@Autowired
	public void setHibernateTemplate(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public <T> T get(Class<T> clazz, Serializable pk) {
		return hibernateTemplate.get(clazz, pk);
	}

	public <T> List<T> getAllEntities(Class<T> clazz) {
		return hibernateTemplate.loadAll(clazz);
	}

	public <T> void save(T record) {
		hibernateTemplate.save(record);
	}

	public <T> void delete(T entity) {
		hibernateTemplate.delete(entity);
	}

	public <T> void saveOrUpdate(T record) {
		hibernateTemplate.saveOrUpdate(record);
	}

	public <T> void deleteCollection(Collection<T> entities) {
		hibernateTemplate.deleteAll(entities);
	}

	public int bulkUpdate(String queryString) throws SQLException {
		return hibernateTemplate.bulkUpdate(queryString);
	}

	public SqlRowSet sqlQuery(String query) {
		return jdbcTemplate.queryForRowSet(query);
	}

	public int sqlQueryUpdate(String query) {
		return jdbcTemplate.update(query);
	}

	public Long getCount(String query) {
		return jdbcTemplate.queryForObject(query, Long.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T uniqueResult(String query, Class<T> clazz, Object... argument) {
		List<T> result;
		if (argument == null || argument.length == 0) {
			result = (List<T>) hibernateTemplate.find(query);
		} else if (argument.length == 1) {
			result = (List<T>) hibernateTemplate.find(query, argument[0]);
		} else {
			result = (List<T>) hibernateTemplate.find(query, argument[0], argument[1]);
		}
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String query, Class<T> clazz, Object... argument) {
		List<T> result;
		if (argument == null || argument.length == 0) {
			result = (List<T>) hibernateTemplate.find(query);
		} else if (argument.length == 1) {
			result = (List<T>) hibernateTemplate.find(query, argument[0]);
		} else if (argument.length == 2) {
			result = (List<T>) hibernateTemplate.find(query, argument[0], argument[1]);
		} else if (argument.length == 3) {
			result = (List<T>) hibernateTemplate.find(query, argument[0], argument[1], argument[2]);
		} else {
			result = (List<T>) hibernateTemplate.find(query, argument);
		}
		return result;
	}

}