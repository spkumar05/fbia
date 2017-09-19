package com.hearst.fbia.app.service;

import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

public interface SubscriptionService {

	Response getSubscriptionPayload(String subscription);

	AdminDao getAdminDao();

}