package com.spaneos.generic.query;

import java.util.Map;

public final class NamedQuery {

	private String queryName;
	private int start;
	private int limit;

	private Map<String, Object> params;

	NamedQuery(String name, int start, int limit, Map<String, Object> params) {
		this.queryName = name;
		this.start = start;
		this.limit = limit;
		this.params = params;

	}

	public static NamedQueryBuilder queryName(String namedQueryName) {
		return new NamedQueryBuilder(namedQueryName);
	}

	public String getQueryName() {
		return queryName;
	}

	public int getStart() {
		return start;
	}

	public int getLimit() {
		return limit;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return String.format("NamedQuery [queryName=%s]", queryName);
	}
	
	

}
