package com.spaneos.generic.hibernate;

import java.util.Map;

public class NamedQuery {
	
	private String queryName;
	private Map<String, Object> params;
	
	public NamedQuery(String name, Map<String, Object> params) {
		this.queryName = name;
		this.params = params;
	}

	public String getQueryName() {
		return queryName;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}

}
