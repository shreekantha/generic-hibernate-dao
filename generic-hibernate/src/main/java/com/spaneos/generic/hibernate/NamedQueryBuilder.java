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
	private Map<Object, Object> params = new LinkedHashMap<Object, Object>();

	public NamedQueryBuilder(String queryName) {
		this.queryName = queryName;
	}

	public static NamedQueryBuilder query(String queryName) {
		NamedQueryBuilder queryBuilder = new NamedQueryBuilder(queryName);
		return queryBuilder;
	}

	public NamedQueryBuilder params(Object param, Object val) {
		this.params.put(param, val);
		return this;
	}

	public NamedQuery build() {
		Objects.requireNonNull(this.queryName, "Queryname must not be null");
		NamedQuery namedQuery = new NamedQuery(this.queryName, this.params);

		return namedQuery;
	}
}
