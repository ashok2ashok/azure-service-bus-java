package com.microsoft.azure.servicebus.rules;

import java.util.Map;

/**
 * Represents a correlation filter expression. A correlation filter is an efficient shortcut for declarations of filters that deal only with correlation equality of message properties.
 * A correlation filter can be created to match one or more of standard or custom properties of a message. In the service, correlation filters are implemented as hashtable lookups. 
 * So they are much faster at run time than other types of filters.
 * @since 1.0
 *
 */
public class CorrelationFilter extends Filter {
	private String correlationId;
	private String messageId;
	private String to;
	private String replyTo;
	private String label;
	private String sessionId;
	private String replyToSessionId;
	private String contentType;
	private Map<String, Object> properties;
	
	/**
	 * Creates a correlation filter with null values for all fields.
	 */
	public CorrelationFilter()
	{		
	}

	/**
	 * Gets the value of correlationId property of a message this filter matches.
	 * @return correlation id this filter matches
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Gets the value of CorrelationId property of a message this filter matches.
	 * @param correlationId correlation id this filter matches
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * Gets the value of MessageId property of a message this filter matches.
	 * @return message id this filter matches
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Sets the value of MessageId property of a message this filter matches.
	 * @param messageId message id this filter matches
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * Gets the value of To property of a message this filter matches. 
	 * @return To property value this filter matches
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Sets the value of To property of a message this filter matches.
	 * @param to To property value this filter matches
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * Gets the value of ReplyTo property of a message this filter matches.
	 * @return ReplyTo property value this filter matches
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * Sets the value of ReplyTo property of a message this filter matches.
	 * @param replyTo ReplyTo property value this filter matches
	 */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * Gets the value of Label property of a message this filter matches.
	 * @return Label property value this filter matches
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the value of Label property of a message this filter matches.
	 * @param label Label property value this filter matches
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the value of SessionId property of a message this filter matches.
	 * @return SessionId property value this filter matches
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the value of SessionId property of a message this filter matches.
	 * @param sessionId SessionId property value this filter matches
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Gets the value of ReplyToSessionId property of a message this filter matches.
	 * @return ReplyToSessionId property value this filter matches
	 */
	public String getReplyToSessionId() {
		return replyToSessionId;
	}

	/**
	 * Sets the value of ReplyToSessionId property of a message this filter matches.
	 * @param replyToSessionId ReplyToSessionId property value this filter matches
	 */
	public void setReplyToSessionId(String replyToSessionId) {
		this.replyToSessionId = replyToSessionId;
	}

	/**
	 * Gets the value of ContentType property of a message this filter matches.
	 * @return ContentType property value this filter matches
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the value of ContentType property of a message this filter matches.
	 * @param contentType ContentType property value this filter matches
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets a name to value map of custom properties on a message this filter matches.
	 * @return map of custom properties this filter matches
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets a name to value map of custom properties on a message this filter matches.
	 * @param properties map of custom properties this filter matches
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}	
}
