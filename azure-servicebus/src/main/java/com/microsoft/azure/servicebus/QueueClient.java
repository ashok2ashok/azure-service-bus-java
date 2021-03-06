package com.microsoft.azure.servicebus;

import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.MessagingFactory;
import com.microsoft.azure.servicebus.primitives.MiscRequestResponseOperationHandler;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.microsoft.azure.servicebus.primitives.StringUtil;

public final class QueueClient extends InitializableEntity implements IQueueClient
{
    private static final Logger TRACE_LOGGER = LoggerFactory.getLogger(QueueClient.class);
	private final ReceiveMode receiveMode;
	private MessagingFactory factory;
	private IMessageSender sender;	
	private MessageAndSessionPump messageAndSessionPump;
	private SessionBrowser sessionBrowser;
	private MiscRequestResponseOperationHandler miscRequestResponseHandler;
	
	private QueueClient(ReceiveMode receiveMode)
	{
		super(StringUtil.getShortRandomString(), null);
		this.receiveMode = receiveMode;
	}
	
	public QueueClient(ConnectionStringBuilder amqpConnectionStringBuilder, ReceiveMode receiveMode) throws InterruptedException, ServiceBusException
	{
		this(receiveMode);		
		CompletableFuture<MessagingFactory> factoryFuture = MessagingFactory.createFromConnectionStringBuilderAsync(amqpConnectionStringBuilder);		
		Utils.completeFuture(factoryFuture.thenComposeAsync((f) -> this.createInternals(f, amqpConnectionStringBuilder.getEntityPath(), receiveMode)));
		if(TRACE_LOGGER.isInfoEnabled())
		{
		    TRACE_LOGGER.info("Created queue client to connection string '{}'", amqpConnectionStringBuilder.toLoggableString());
		}
	}
	
	QueueClient(MessagingFactory factory, String queuePath, ReceiveMode receiveMode) throws InterruptedException, ServiceBusException
	{
		this(receiveMode);
		Utils.completeFuture(this.createInternals(factory, queuePath, receiveMode));
		if(TRACE_LOGGER.isInfoEnabled())
        {
            TRACE_LOGGER.info("Created queue client to queue '{}'", queuePath);
        }
	}
	
	private CompletableFuture<Void> createInternals(MessagingFactory factory, String queuePath, ReceiveMode receiveMode)
	{
	    this.factory = factory;
		CompletableFuture<IMessageSender> senderFuture = ClientFactory.createMessageSenderFromEntityPathAsync(factory, queuePath);
		CompletableFuture<Void> postSenderFuture = senderFuture.thenAcceptAsync((s) -> {
			this.sender = s;			
		});		
		
		CompletableFuture<Void> postSessionBrowserFuture = MiscRequestResponseOperationHandler.create(factory, queuePath).thenAcceptAsync((msoh) -> {
			this.miscRequestResponseHandler = msoh;
			this.sessionBrowser = new SessionBrowser(factory, queuePath, msoh);
		});		
		
		this.messageAndSessionPump = new MessageAndSessionPump(factory, queuePath, receiveMode);
		CompletableFuture<Void> messagePumpInitFuture = this.messageAndSessionPump.initializeAsync();
		
		return CompletableFuture.allOf(postSenderFuture, postSessionBrowserFuture, messagePumpInitFuture);
	}	
	
	@Override
	public ReceiveMode getReceiveMode() {
		return this.receiveMode;
	}	

	@Override
	public void send(IMessage message) throws InterruptedException, ServiceBusException {
		this.sender.send(message);		
	}

	@Override
	public void sendBatch(Collection<? extends IMessage> messages) throws InterruptedException, ServiceBusException {
		this.sender.sendBatch(messages);		
	}

	@Override
	public CompletableFuture<Void> sendAsync(IMessage message) {
		return this.sender.sendAsync(message);
	}

	@Override
	public CompletableFuture<Void> sendBatchAsync(Collection<? extends IMessage> messages) {
		return this.sender.sendBatchAsync(messages);
	}

	@Override
	public CompletableFuture<Long> scheduleMessageAsync(IMessage message, Instant scheduledEnqueueTimeUtc) {
		return this.sender.scheduleMessageAsync(message, scheduledEnqueueTimeUtc);
	}

	@Override
	public CompletableFuture<Void> cancelScheduledMessageAsync(long sequenceNumber) {
		return this.sender.cancelScheduledMessageAsync(sequenceNumber);
	}

	@Override
	public long scheduleMessage(IMessage message, Instant scheduledEnqueueTimeUtc) throws InterruptedException, ServiceBusException {
		return this.sender.scheduleMessage(message, scheduledEnqueueTimeUtc);
	}

	@Override
	public void cancelScheduledMessage(long sequenceNumber) throws InterruptedException, ServiceBusException {
		this.sender.cancelScheduledMessage(sequenceNumber);	
	}

	@Override
	public String getEntityPath() {
		return this.sender.getEntityPath();
	}

	@Override
	public void registerMessageHandler(IMessageHandler handler) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.registerMessageHandler(handler);		
	}

	@Override
	public void registerMessageHandler(IMessageHandler handler, MessageHandlerOptions handlerOptions) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.registerMessageHandler(handler, handlerOptions);		
	}

	@Override
	public void registerSessionHandler(ISessionHandler handler) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.registerSessionHandler(handler);		
	}

	@Override
	public void registerSessionHandler(ISessionHandler handler, SessionHandlerOptions handlerOptions) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.registerSessionHandler(handler, handlerOptions);		
	}

	// No op now
	@Override
	CompletableFuture<Void> initializeAsync(){
		return CompletableFuture.completedFuture(null);
	}

	@Override
	protected CompletableFuture<Void> onClose() {
		return this.messageAndSessionPump.closeAsync().thenCompose((v) -> this.sender.closeAsync().thenCompose((u) -> this.miscRequestResponseHandler.closeAsync().thenCompose((w) -> this.factory.closeAsync())));
	}

//	@Override
	Collection<IMessageSession> getMessageSessions() throws InterruptedException, ServiceBusException {
		return Utils.completeFuture(this.getMessageSessionsAsync());
	}

//	@Override
	Collection<IMessageSession> getMessageSessions(Instant lastUpdatedTime) throws InterruptedException, ServiceBusException {
		return Utils.completeFuture(this.getMessageSessionsAsync(lastUpdatedTime));
	}

//	@Override
	CompletableFuture<Collection<IMessageSession>> getMessageSessionsAsync() {
		return this.sessionBrowser.getMessageSessionsAsync();
	}

//	@Override
	CompletableFuture<Collection<IMessageSession>> getMessageSessionsAsync(Instant lastUpdatedTime) {
		return this.sessionBrowser.getMessageSessionsAsync(Date.from(lastUpdatedTime));
	}

	@Override
	public void abandon(UUID lockToken) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.abandon(lockToken);
	}

	@Override
	public void abandon(UUID lockToken, Map<String, Object> propertiesToModify)	throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.abandon(lockToken, propertiesToModify);		
	}

	@Override
	public CompletableFuture<Void> abandonAsync(UUID lockToken) {
		return this.messageAndSessionPump.abandonAsync(lockToken);
	}

	@Override
	public CompletableFuture<Void> abandonAsync(UUID lockToken, Map<String, Object> propertiesToModify) {
		return this.messageAndSessionPump.abandonAsync(lockToken, propertiesToModify);
	}

	@Override
	public void complete(UUID lockToken) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.complete(lockToken);		
	}

	@Override
	public CompletableFuture<Void> completeAsync(UUID lockToken) {
		return this.messageAndSessionPump.completeAsync(lockToken);
	}

//	@Override
	void defer(UUID lockToken) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.defer(lockToken);		
	}

//	@Override
	void defer(UUID lockToken, Map<String, Object> propertiesToModify) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.defer(lockToken, propertiesToModify);		
	}

	@Override
	public CompletableFuture<Void> deferAsync(UUID lockToken) {
		return this.messageAndSessionPump.deferAsync(lockToken);
	}

	@Override
	public CompletableFuture<Void> deferAsync(UUID lockToken, Map<String, Object> propertiesToModify) {
		return this.messageAndSessionPump.deferAsync(lockToken, propertiesToModify);
	}

	@Override
	public void deadLetter(UUID lockToken) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.deadLetter(lockToken);		
	}

	@Override
	public void deadLetter(UUID lockToken, Map<String, Object> propertiesToModify) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.deadLetter(lockToken, propertiesToModify);		
	}

	@Override
	public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.deadLetter(lockToken, deadLetterReason, deadLetterErrorDescription);		
	}

	@Override
	public void deadLetter(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription, Map<String, Object> propertiesToModify) throws InterruptedException, ServiceBusException {
		this.messageAndSessionPump.deadLetter(lockToken, deadLetterReason, deadLetterErrorDescription, propertiesToModify);		
	}

	@Override
	public CompletableFuture<Void> deadLetterAsync(UUID lockToken) {
		return this.messageAndSessionPump.deadLetterAsync(lockToken);
	}

	@Override
	public CompletableFuture<Void> deadLetterAsync(UUID lockToken, Map<String, Object> propertiesToModify) {
		return this.messageAndSessionPump.deadLetterAsync(lockToken, propertiesToModify);
	}

	@Override
	public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason, String deadLetterErrorDescription) {
		return this.messageAndSessionPump.deadLetterAsync(lockToken, deadLetterReason, deadLetterErrorDescription);
	}

	@Override
	public CompletableFuture<Void> deadLetterAsync(UUID lockToken, String deadLetterReason,	String deadLetterErrorDescription, Map<String, Object> propertiesToModify) {
		return this.messageAndSessionPump.deadLetterAsync(lockToken, deadLetterReason, deadLetterErrorDescription, propertiesToModify);
	}

    @Override
    public int getPrefetchCount() {
        return this.messageAndSessionPump.getPrefetchCount();
    }

    @Override
    public void setPrefetchCount(int prefetchCount) throws ServiceBusException {
        this.messageAndSessionPump.setPrefetchCount(prefetchCount);
    }
    
    @Override
    public String getQueueName()
    {
        return this.getEntityPath();
    }
}
