package com.spaneos.generic.hibernate.exception;

import org.apache.log4j.Logger;

/**
 * <code>CRUDException</code> is an exception class that is used when any
 * problem related to Hibernate CRUD operations have occurred.
 * 
 * @author Sreekantha
 *
 */
public class CRUDException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CRUDException.class);

	public static final String HIBERNATE_ERROR = "Hibernate exception occured : %s";
	public static final String RECORD_NOT_FOUND_TO_UPDATE = "RNFTU";
	public static final String RECORD_NOT_UPDATED = "RNU";
	public static final String DUPLICATE_KEY_FOUND = "DKF";
	public static final String OBJECTS_NOT_FOUND = "OSNF";
	public static final String OBJECT_CAN_NOT_BE_NULL = "OCNBN";
	public static final String CLASS_CAST_EXCEPTION = "CCE";
	public static final String RECORD_NOT_DELETED = "RND";
	public static final String QUERY_CAN_NOT_BE_NULL = "QCNBN";
	public static final String QUERY_CAN_NOT_BE_EXECUTED = "QCNBE";
	public static final String ID_AND_CLASS_CAN_NOT_BE_NULL = "IOCCNBN";
	public static final String RUNTIME_EXCEPTION = "RTE";
	public static final String RECORD_NOT_FOUND = "RNF";
	public static final String RECORD_NOT_FOUND_TO_DELETE = "Record not found to delete with the Id: {0}";
	public static final String UNKNOWN_ERROR = "Unknown exception has occured";

	public static final String UNKOWN_QUERY = "Unknown query (query:%s)";

	private final String message;

	public CRUDException() {
		this.message = "Hibernate CRUD Exception: ";
	}

	public CRUDException(String message) {
		super();
		this.message = message;
	}

	/**
	 * @param message
	 * @param id
	 */
	public CRUDException(String message, Object... args) {
		this.message = String.format(message, args);
	}

	public CRUDException(String message, Exception e) {
		this.message = message;

		LOGGER.error(message, e);
	}

	@Override
	public String getMessage() {
		return message;
	}

}
