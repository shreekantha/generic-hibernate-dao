/**
 * 
 */
package com.spaneos.generic.hibernate;

import java.io.Serializable;
import java.util.List;

import com.spaneos.generic.hibernate.exception.CRUDException;

/**
 * @author Shreekantha
 *
 */
public interface GenericDao<T, I extends Serializable> {

	/**
	 * Makes the transient object as persistent object
	 * 
	 * @param entity
	 * @return
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	T persist(T entity) throws CRUDException;

	/**
	 * Moves the object from persistent state to removed state
	 * 
	 * @param entity
	 *            entity to remove from the persistent
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	void delete(T entity) throws CRUDException;

	/**
	 * Retrieves the persistent object from database based on id
	 * 
	 * @param id
	 * @param lockOption
	 * @return entity
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	T findById(I id, boolean lockOption) throws CRUDException;

	/**
	 * Retrieves the all records from the database using persistent entity
	 * 
	 * @return list of persistent objects
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	List<T> findAll(Integer start, Integer limit) throws CRUDException;

	/**
	 * Persists the list of objects into database using hibernate batch
	 * processing
	 * 
	 * @param entities
	 * @param batchSize
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	void batchPersist(List<T> entities, int batchSize) throws CRUDException;

	/**
	 * Executes the custom query to get the resultset
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @return returns the list of objects
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	@Deprecated
	List<T> queryObjects(String queryName, Integer start, Integer limit,
			Object... params) throws CRUDException;

	/**
	 * Executes the custom query to get a single object
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @return returns the list of objects
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	T uniqueResult(String queryName, Object... params) throws CRUDException;

	/**
	 * Executes the custom query for delete operation
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	@Deprecated
	void queryForDelete(String queryName, Object... params)
			throws CRUDException;

	/**
	 * 
	 * 
	 * @param namedQuery
	 *            : contains the namedQuery name and named parameters for that
	 *            query
	 * @return list of objects
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	List<T> find(NamedQuery namedQuery) throws CRUDException;

	/**
	 * @param namedQuery
	 * @throws CRUDException
	 * @author Shreekantha
	 */
	void deleteOrUpdate(NamedQuery namedQuery) throws CRUDException;

}
