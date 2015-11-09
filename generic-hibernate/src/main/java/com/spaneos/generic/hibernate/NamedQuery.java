package com.spaneos.generic.hibernate;

import java.util.Map;

public class NamedQuery {
	
	private String queryName;
	private Map<Object, Object> params;
	
	public NamedQuery(String name, Map<Object, Object> params) {
		this.queryName = name;
		this.params = params;
	}
	
	

}
