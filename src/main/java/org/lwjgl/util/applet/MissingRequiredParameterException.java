package org.lwjgl.util.applet;

public class MissingRequiredParameterException extends RuntimeException {

	private static final long serialVersionUID = 2778688787200694198L;
	
	private final String parameter;

	public String getParameter() {
		return parameter;
	}

	public MissingRequiredParameterException(String parameter) {
		this.parameter = parameter;
	}
	
	public MissingRequiredParameterException(String parameter, String message) {
		super(message);
		this.parameter = parameter;
	}

	public MissingRequiredParameterException(String parameter, String message, Throwable cause) {
		super(message, cause);
		this.parameter = parameter;
	}

	public MissingRequiredParameterException(String parameter, Throwable cause) {
		super(cause);
		this.parameter = parameter;
	}

}