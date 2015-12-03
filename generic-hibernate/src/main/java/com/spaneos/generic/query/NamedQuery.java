package com.spaneos.generic.query;

import java.util.Map;

public class NamedQuery {

	private String queryName;
	private int start;
	private int limit;

	private Map<String, Object> params;

	 NamedQuery(String name, int start, int limit,
			Map<String, Object> params) {
		this.queryName = name;
		this.start = start;
		this.limit = limit;
		this.params = params;

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

}
