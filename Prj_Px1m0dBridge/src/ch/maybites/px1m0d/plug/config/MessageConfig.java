package ch.maybites.px1m0d.plug.config;

import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import de.cnc.expression.AbstractRuntimeEnvironment;
import de.cnc.expression.exceptions.ExpressionEvaluationException;
import de.cnc.expression.exceptions.ExpressionParseException;

public interface MessageConfig {

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException;

	public Message evaluate(AbstractRuntimeEnvironment runEnv) throws ExpressionEvaluationException, MessageCreationException;
	
	public void print();

}
