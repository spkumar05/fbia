package com.hearst.fbia.app.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public class BaseEntity {
	@JsonIgnore
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@JsonIgnore
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@JsonIgnore
	@Column(name = "CREATED_BY")
	private Long createdBy;
	@JsonIgnore
	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

}