package com.spaneos.generic.exception;

/**
 * <code>CRUDQException</code> to handle exceptions that occur during CRUDQ
 * operations
 * 
 * @author Sreekantha
 *
 */
public class CRUDQException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public static final String HIBERNATE_ERROR = "Hibernate exception occured : %s";
	public static final String UNABLE_TO_EXECUTE_QUERY = "Unable to execute the query [ %s ] and the error is:\n Error : %s";
	public static final String RECORD_NOT_FOUND_TO_DELETE = "Record not found to delete with the Id: %s";
	public static final String UNKNOWN_ERROR = "Unknown exception has occured";
	public static final String UNKOWN_QUERY = "Unknown query [ %s ] ";

	private final String message;

	public CRUDQException() {
		this.message = "Hibernate CRUD Exception: " + super.getMessage();
	}

	public CRUDQException(String message) {
		super();
		this.message = message;
	}

	public CRUDQException(String message, Exception e) {
		this.message = message;
	}

	public CRUDQException(String message, Object... args) {
		this.message = String.format(message, args);
	}

	public CRUDQException(String message, Exception e, Object... args) {
		this.message = String.format(message, args, e.getStackTrace()
				.toString());
	}

	@Override
	public String getMessage() {
		return message;
	}

}
