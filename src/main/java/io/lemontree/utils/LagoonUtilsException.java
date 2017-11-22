package io.lemontree.utils;

public class LagoonUtilsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LagoonUtilsException(String message) {
		super(message);
	}

	public LagoonUtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public LagoonUtilsException(Throwable cause) {
		super(cause);
	}
	
	public <T> LagoonUtilsException(Throwable cause, ExceptionType type, String ...args) {
		super(createExceptionTypeMessage(type, args), cause);
	}
	
	private static String createExceptionTypeMessage(ExceptionType type, String ...args) {
		switch(type){
			case CLASS_NEW_INSTANCE: return "Class could not instantiate a new instance of class '"+args[0]+"'";
			case INVOKE_METHOD: return "Could not invoke method '"+args[0]+"' on object of type '"+args[1]+"'";
		}
		return null;
	}

	enum ExceptionType{
		CLASS_NEW_INSTANCE, INVOKE_METHOD
	}
}
