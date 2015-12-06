package com.spaneos.generic.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builder to build named query along with named parameters
 * 
 * @author Shreekantha
 *
 *
 */
public final class NamedQueryBuilder {

	private String queryName;
	private int start;
	private int limit;
	private Map<String, Object> params = new LinkedHashMap<String, Object>();

	 NamedQueryBuilder(String queryName) {
		this.queryName = queryName;
	}

	
	public NamedQueryBuilder start(int start) {
		this.start = start;
		return this;
	}

	public NamedQueryBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	public NamedQueryBuilder param(String namedParam, Object value) {
		this.params.put(namedParam, value);
		return this;
	}

	public NamedQuery build() {
		Objects.requireNonNull(this.queryName, "Query name must not be null");
		NamedQuery namedQuery = new NamedQuery(this.queryName, this.start,
				this.limit, this.params);
		return namedQuery;
	}
}
