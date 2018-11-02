package com.reed.integration.ignite.model;

import java.util.Date;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Person extends BaseObj {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5120195203027504283L;

	@QuerySqlField(index = true)
	private Long id;

	@QuerySqlField(index = true)
	private String name;

	@QuerySqlField(index = true)
	private Boolean male;

	@QuerySqlField(index = true)
	private Date birthdate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getMale() {
		return male;
	}

	public void setMale(Boolean male) {
		this.male = male;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

}
