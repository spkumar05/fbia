package com.hearst.fbia.app.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
public class BaseEntity {

	@JsonIgnore
	@CreatedDate
	@Column(name = "created_date")
	private Date createdDate;

	@JsonIgnore
	@LastModifiedDate
	@Column(name = "updated_date")
	private Date updatedDate;

	@JsonIgnore
	@CreatedBy
	@Column(name = "created_by", length = 100)
	private String createdBy;

	@JsonIgnore
	@LastModifiedBy
	@Column(name = "updated_by", length = 100)
	private String updatedBy;

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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}