package ch.maybites.px1m0d.plug.config.exception;

public class MessageCreationException extends Exception{
	private static final long serialVersionUID = 1L;

	public MessageCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageCreationException(String message) {
		super(message, new Throwable());
	}

}
