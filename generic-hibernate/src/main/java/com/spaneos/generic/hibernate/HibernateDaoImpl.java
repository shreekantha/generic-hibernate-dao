/**
 * 
 */
package com.spaneos.generic.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.spaneos.generic.hibernate.exception.CRUDException;

/**
 * {@link HibernateDaoImpl} for implementation of generic CRUD methods
 * 
 * @author Shreekantha
 *
 */
public class HibernateDaoImpl<T, I extends Serializable> implements
		GenericDao<T, I> {

	private static final Logger LOG = Logger.getLogger(HibernateDaoImpl.class);

	private static final String HIBERNATE_ERROR = "Hibernate exception: ";
	private static final String EXCEPTION = "Exception: ";
	private static final String UNCHECKED = "unchecked";

	// Repository class
	private Class<T> persistenceClass;

	private SessionFactory sessionFactory;

	// Set the persistence class on which CRUD operations being applied
	@SuppressWarnings("unchecked")
	public HibernateDaoImpl() {
		this.persistenceClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Inject
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	// get the persistence class
	public Class<T> getPersistenceClass() {
		return persistenceClass;
	}

	@Override
	public T persist(T entity) throws CRUDException {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			session.saveOrUpdate(entity);
			transaction.commit();

			return entity;
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(CRUDException.HIBERNATE_ERROR,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public void delete(T entity) throws CRUDException {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			session.delete(entity);
			transaction.commit();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(CRUDException.HIBERNATE_ERROR,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public T findById(I id, boolean lock) throws CRUDException {
		Session session = null;
		T entity;

		try {
			session = sessionFactory.openSession();

			if (lock) {
				entity = (T) session.get(getPersistenceClass(), id,
						LockOptions.UPGRADE);
			} else {
				entity = (T) session.get(getPersistenceClass(), id);
			}

			return entity;
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(String.format(
					CRUDException.HIBERNATE_ERROR,
					hibernateException.getMessage()), hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findAll(Integer start, Integer limit) throws CRUDException {
		Session session = null;
		Criteria criteria = null;

		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(getPersistenceClass());

			if (start != null) {
				criteria.setFirstResult(start);
			}

			if (limit != null) {
				criteria.setMaxResults(limit);
			}

			return criteria.list();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(String.format(
					CRUDException.HIBERNATE_ERROR,
					hibernateException.getMessage()), hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	@Deprecated
	public List<T> queryObjects(String queryName, Integer start, Integer limit,
			Object... params) throws CRUDException {
		Session session = sessionFactory.openSession();

		try {
			Query query = session.getNamedQuery(queryName);
			int index = 0;

			for (Object param : params) {
				query.setParameter(index++, param);
			}

			if (start != null) {
				query.setFirstResult(start);
			}

			if (limit != null) {
				query.setMaxResults(limit);
			}

			return query.list();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(CRUDException.QUERY_CAN_NOT_BE_EXECUTED,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

	@Override
	public void batchPersist(List<T> entities, int batchSize)
			throws CRUDException {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			int recordCount = 0;
			for (T entity : entities) {
				session.saveOrUpdate(entity);
				if (recordCount % batchSize == 0) {
					session.clear();
					session.flush();
				}
			}

			transaction.commit();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(String.format(
					CRUDException.HIBERNATE_ERROR,
					hibernateException.getMessage()), hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			if (transaction != null) {
				transaction.rollback();
			}

			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public T uniqueResult(String queryName, Object... params)
			throws CRUDException {
		Session session = sessionFactory.openSession();

		try {
			Query query = session.getNamedQuery(queryName);
			int index = 0;

			for (Object param : params) {
				query.setParameter(index++, param);
			}

			return (T) query.uniqueResult();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(CRUDException.QUERY_CAN_NOT_BE_EXECUTED,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

	@Override
	@Deprecated
	public void queryForDelete(String queryName, Object... params)
			throws CRUDException {
		Session session = sessionFactory.openSession();

		try {
			Query query = session.getNamedQuery(queryName);
			int index = 0;

			for (Object param : params) {
				query.setParameter(index++, param);
			}

			query.executeUpdate();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(CRUDException.QUERY_CAN_NOT_BE_EXECUTED,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> find(NamedQuery namedQuery) throws CRUDException {

		Session session = null;
		Query query = null;

		try {
			session = sessionFactory.openSession();
			if (namedQuery != null) {
				query = session.getNamedQuery(namedQuery.getQueryName());
				Map<String, Object> maps = namedQuery.getParams();

				for (Map.Entry<String, Object> param : maps.entrySet())
					query.setParameter(param.getKey(), param.getValue());
				
				query.setFirstResult(namedQuery.getStart());
				
				int limit = namedQuery.getLimit();
				if (limit > 0)
					query.setMaxResults(limit);
				
				return query.list();
			}
			throw new CRUDException(CRUDException.UNKOWN_QUERY, namedQuery);
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(CRUDException.QUERY_CAN_NOT_BE_EXECUTED,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}

	@Override
	public void deleteOrUpdate(NamedQuery namedQuery) throws CRUDException {

		Session session = null;
		Query query = null;

		try {
			session = sessionFactory.openSession();
			if (namedQuery != null) {
				query = session.getNamedQuery(namedQuery.getQueryName());
				Map<String, Object> maps = namedQuery.getParams();

				for (Map.Entry<String, Object> param : maps.entrySet())
					query.setParameter(param.getKey(), param.getValue());
				query.executeUpdate();
			}
			throw new CRUDException(CRUDException.UNKOWN_QUERY, namedQuery);
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDException(CRUDException.QUERY_CAN_NOT_BE_EXECUTED,
					hibernateException);
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDException(CRUDException.UNKNOWN_ERROR, exception);
		} finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
	}
}
