package com.spaneos.generic.hibernate;

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
public class NamedQueryBuilder {

	private String queryName;
	private int start;
	private int limit;
	private Map<String, Object> params = new LinkedHashMap<String, Object>();

	public NamedQueryBuilder(String queryName) {
		this.queryName = queryName;
	}

	public static NamedQueryBuilder query(String queryName) {
		NamedQueryBuilder queryBuilder = new NamedQueryBuilder(queryName);
		return queryBuilder;
	}

	public NamedQueryBuilder start(int start) {
		this.start = start;
		return this;
	}

	public NamedQueryBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	public NamedQueryBuilder params(String param, Object val) {
		this.params.put(param, val);
		return this;
	}

	public NamedQuery build() {
		Objects.requireNonNull(this.queryName, "Queryname must not be null");
		NamedQuery namedQuery = new NamedQuery(this.queryName, this.start,
				this.limit, this.params);

		return namedQuery;
	}
}
