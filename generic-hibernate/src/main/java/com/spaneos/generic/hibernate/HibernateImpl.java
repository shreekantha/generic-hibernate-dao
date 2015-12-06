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
import org.hibernate.criterion.Projections;

import com.spaneos.generic.GenericRepository;
import com.spaneos.generic.exception.CRUDQException;
import com.spaneos.generic.query.NamedQuery;

/**
 * {@link HibernateImpl} for implementation of generic CRUD methods
 * 
 * @author Shreekantha
 *
 */
public class HibernateImpl<T, I extends Serializable> implements
		GenericRepository<T, I> {

	// logger
	private static final Logger LOG = Logger.getLogger(HibernateImpl.class);

	private static final String HIBERNATE_ERROR = "Hibernate exception: ";
	private static final String EXCEPTION = "Exception: ";
	private static final String UNCHECKED = "unchecked";

	// Repository class
	private Class<T> persistenceClass;

	// SessionFactory
	@Inject
	private SessionFactory sessionFactory;

	// Set the persistence class on which CRUD operations being applied
	@SuppressWarnings("unchecked")
	public HibernateImpl() {
		this.persistenceClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	// get the persistence class
	public Class<T> getPersistenceClass() {
		return persistenceClass;
	}

	@Override
	public T persist(T entity) throws CRUDQException {
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

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void delete(T entity) throws CRUDQException {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			session.delete(entity);
			transaction.commit();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public T findById(I id, boolean lock) throws CRUDQException {
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

			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());

		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() throws CRUDQException {
		Session session = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(getPersistenceClass());
			return criteria.list();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findAll(Integer start, Integer limit) throws CRUDQException {
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
			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findAll(NamedQuery namedQuery) throws CRUDQException {

		Session session = null;
		Query query = null;

		try {
			session = sessionFactory.openSession();
			if (namedQuery != null) {
				query = prepareQuery(namedQuery, session);
				query.setProperties(namedQuery.getParams());
				query.setFirstResult(namedQuery.getStart());

				int limit = namedQuery.getLimit();
				if (limit > 0)
					query.setMaxResults(limit);

				return query.list();
			}
			throw new CRUDQException(CRUDQException.UNKOWN_QUERY, namedQuery);
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDQException(CRUDQException.UNABLE_TO_EXECUTE_QUERY,namedQuery,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	@Deprecated
	public List<T> queryObjects(String queryName, Integer start, Integer limit,
			Object... params) throws CRUDQException {
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
			throw new CRUDQException(CRUDQException.UNABLE_TO_EXECUTE_QUERY,queryName,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void batchPersist(List<T> entities, int batchSize)
			throws CRUDQException {
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

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);

			rollBack(transaction);

			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public T uniqueResult(String queryName, Object... params)
			throws CRUDQException {
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
			throw new CRUDQException(CRUDQException.UNABLE_TO_EXECUTE_QUERY,queryName,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@Override
	@Deprecated
	public void queryForDelete(String queryName, Object... params)
			throws CRUDQException {
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
			throw new CRUDQException(CRUDQException.UNABLE_TO_EXECUTE_QUERY,queryName,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void deleteOrUpdate(NamedQuery namedQuery) throws CRUDQException {

		Session session = null;
		Query query = null;

		try {
			session = sessionFactory.openSession();
			if (namedQuery != null) {
				query = prepareQuery(namedQuery, session);
				query.executeUpdate();
			}
			throw new CRUDQException(CRUDQException.UNKOWN_QUERY, namedQuery);
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDQException(CRUDQException.UNABLE_TO_EXECUTE_QUERY,namedQuery,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	// Prepares the Query using NamedQuery and its parameters
	private Query prepareQuery(NamedQuery namedQuery, Session session) {
		Query query=session.getNamedQuery(namedQuery.getQueryName());
		Map<String, Object> params = namedQuery.getParams();
		// query.setProperties(params);
		for (Map.Entry<String, Object> param : params.entrySet())
			query.setParameter(param.getKey(), param.getValue());
		return query;
	}

	@Override
	public int count() {
		Session session = null;
		Criteria criteria = null;

		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(getPersistenceClass());
			criteria.setProjection(Projections.rowCount());
			return (int) criteria.uniqueResult();
		} catch (HibernateException hibernateException) {
			LOG.error(HIBERNATE_ERROR, hibernateException);
			throw new CRUDQException(CRUDQException.HIBERNATE_ERROR,
					hibernateException.getMessage());
		} catch (Exception exception) {
			LOG.error(EXCEPTION, exception);
			throw new CRUDQException(CRUDQException.UNKNOWN_ERROR,
					exception.getMessage());
		} finally {
			closeSession(session);
		}
	}

	// used roll back the uncommitted persisted data
	private void rollBack(Transaction transaction) {
		if (transaction != null) {
			transaction.rollback();
			transaction = null;
		}
	}

	// used to close the session
	private void closeSession(Session session) {
		if (session != null) {
			session.close();
			session = null;
		}
	}
}
