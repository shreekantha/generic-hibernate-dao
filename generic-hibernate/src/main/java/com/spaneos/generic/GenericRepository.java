/**
 * 
 */
package com.spaneos.generic;

import java.io.Serializable;
import java.util.List;

import com.spaneos.generic.exception.CRUDQException;
import com.spaneos.generic.query.NamedQuery;

/**
 * @author Shreekantha
 *
 */
public interface GenericRepository<T, I extends Serializable> {

	/**
	 * Makes the transient object as persistent object
	 * 
	 * @param entity
	 * @return
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	T persist(T entity) throws CRUDQException;

	/**
	 * Moves the object from persistent state to removed state
	 * 
	 * @param entity
	 *            entity to remove from the persistent
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	void delete(T entity) throws CRUDQException;

	/**
	 * Retrieves the persistent object from database based on id
	 * 
	 * @param id
	 * @param lockOption
	 * @return entity
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	T findById(I id, boolean lockOption) throws CRUDQException;

	/**
	 * Retrieves all the records from the database using persistent entity
	 * 
	 * @return list of persistent objects
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	List<T> findAll(Integer start, Integer limit) throws CRUDQException;

	/**
	 * Retrieves all the records from the database using persistent entity
	 * 
	 * @return list of persistent objects
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	List<T> findAll() throws CRUDQException;

	/**
	 * Retrieves all the records from the database using named query
	 * 
	 * @param namedQuery
	 *            : contains the namedQuery name and named parameters for that
	 *            query
	 * @return list of objects
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	List<T> findAll(NamedQuery namedQuery) throws CRUDQException;

	/**
	 * Persists the list of objects into database using batch processing<br>
	 * <strong> Prerequisite :Batch persisting should be enabled</strong>.
	 * 
	 * @param entities
	 * @param batchSize
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	void batchPersist(List<T> entities, int batchSize) throws CRUDQException;

	/**
	 * Executes the custom query to get the resultset (Since it is deprecated ,
	 * use findAll(NamedQuery namedQuery) method
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @return returns the list of objects
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	@Deprecated
	List<T> queryObjects(String queryName, Integer start, Integer limit, Object... params) throws CRUDQException;

	/**
	 * Executes the custom query to get a single object<br>
	 * <Strong> Since it is deprecated, use uniqueResult(NamedQuery
	 * namedQuery)</strong>
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @return returns the unique record
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	@Deprecated
	T uniqueResult(String queryName, Object... params) throws CRUDQException;

	/**
	 * Executes the custom query to get a single object
	 * 
	 * @param namedQuery
	 *            : contains the namedQuery name and named parameters for that
	 *            query
	 * @return returns the unique record
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	T uniqueResult(NamedQuery namedQuery) throws CRUDQException;

	/**
	 * Executes the custom query for delete operation<br>
	 * <Strong> Since it is deprecated, use deleteOrUpdate(NamedQuery namedQuery)</strong>
	 * 
	 * @param queryName
	 *            name of the query to be executed
	 * @param params
	 *            parameters to be set to execute the query
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	@Deprecated
	void queryForDelete(String queryName, Object... params) throws CRUDQException;

	/**
	 * Executes the custom query for delete / update operation
	 * 
	 * @param namedQuery
	 * @throws CRUDQException
	 * @author Shreekantha
	 */
	void deleteOrUpdate(NamedQuery namedQuery) throws CRUDQException;

	/**
	 * Method to count the number records present in the database
	 * 
	 * @return number of records
	 * @author Shreekantha
	 */
	int count();

	/**
	 * Method to execute the query that includes aggregate functions<br>
	 * <ul>
	 * <li>SUM</li>
	 * <li>AVG</li>
	 * <li>COUNT</li>
	 * <li>FIRST</li>
	 * <li>LAST</li>
	 * <li>MAX</li>
	 * <li>MIN</li>
	 * </ul>
	 * 
	 * @param namedQuery
	 * @return aggregated result
	 */
	// int aggregation(NamedQuery namedQuery);

}
