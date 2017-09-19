package com.hearst.fbia.frm.config.audit;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.hearst.fbia.app.domain.BaseEntity;

public class AuditInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
			throws CallbackException {
		if (entity instanceof BaseEntity) {
			setValue(state, propertyNames, "createdDate", new Date());
			setValue(state, propertyNames, "updatedDate", new Date());
			setValue(state, propertyNames, "createdBy", "FBIA_ADMIN");
			setValue(state, propertyNames, "updatedBy", "FBIA_ADMIN");
		}
		return true;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) throws CallbackException {
		if (entity instanceof BaseEntity) {
			setValue(currentState, propertyNames, "updatedDate", new Date());
			setValue(currentState, propertyNames, "updatedBy", "FBIA_ADMIN");
		}
		return true;
	}

	private void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value) {
		int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
		if (index >= 0) {
			currentState[index] = value;
		}
	}
}