package com.microsoft.azure.servicebus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.azure.servicebus.primitives.ServiceBusException;

public class QueueClientTests {
	private QueueClient queueClient;
	private QueueClient sessionfulQueueClient;
	
	@Before
	public void setUp()
	{		
		
	}
	
	@After
	public void tearDown() throws ServiceBusException, InterruptedException
	{		
		if(this.queueClient != null)
		{
			this.queueClient.close();
			TestCommons.drainAllMessages(TestUtils.getNonPartitionedQueueConnectionStringBuilder());
		}
			
		if(this.sessionfulQueueClient != null)
		{
			this.sessionfulQueueClient.close();
			TestCommons.drainAllSessions(TestUtils.getNonPartitionedSessionfulQueueConnectionStringBuilder(), true);	
		}			
	}
	
	private void createQueueClient() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient(ReceiveMode.PEEKLOCK);
	}
	
	private void createSessionfulQueueClient() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient(ReceiveMode.PEEKLOCK);
	}
	
	private void createQueueClient(ReceiveMode receiveMode) throws InterruptedException, ServiceBusException
	{
		this.queueClient = new QueueClient(TestUtils.getNonPartitionedQueueConnectionStringBuilder(), receiveMode);
	}
	
	private void createSessionfulQueueClient(ReceiveMode receiveMode) throws InterruptedException, ServiceBusException
	{
		this.sessionfulQueueClient = new QueueClient(TestUtils.getNonPartitionedSessionfulQueueConnectionStringBuilder(), receiveMode);
	}
	
	@Test
	public void testMessagePumpAutoComplete() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient();
		MessageAndSessionPumpTests.testMessagePumpAutoComplete(this.queueClient, this.queueClient);		
	}
	
	@Test
	public void testReceiveAndDeleteMessagePump() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient(ReceiveMode.RECEIVEANDDELETE);
		MessageAndSessionPumpTests.testMessagePumpAutoComplete(this.queueClient, this.queueClient);		
	}
	
	@Test
	public void testMessagePumpClientComplete() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient();
		MessageAndSessionPumpTests.testMessagePumpClientComplete(this.queueClient, this.queueClient);
	}
	
	@Test
	public void testMessagePumpAbandonOnException() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient();
		MessageAndSessionPumpTests.testMessagePumpAbandonOnException(this.queueClient, this.queueClient);
	}
	
	@Test
	public void testMessagePumpRenewLock() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient();
		MessageAndSessionPumpTests.testMessagePumpRenewLock(this.queueClient, this.queueClient);
	}
	
	@Test
	public void testRegisterAnotherHandlerAfterMessageHandler() throws InterruptedException, ServiceBusException
	{
		this.createQueueClient();
		MessageAndSessionPumpTests.testRegisterAnotherHandlerAfterMessageHandler(this.queueClient);
	}
	
	@Test
	public void testRegisterAnotherHandlerAfterSessionHandler() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testRegisterAnotherHandlerAfterSessionHandler(this.sessionfulQueueClient);
	}
	
	@Test
	public void testGetMessageSessions() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		TestCommons.testGetMessageSessions(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testSessionPumpAutoCompleteWithOneConcurrentCallPerSession() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testSessionPumpAutoCompleteWithOneConcurrentCallPerSession(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testReceiveAndDeleteSessionPump() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient(ReceiveMode.RECEIVEANDDELETE);
		MessageAndSessionPumpTests.testSessionPumpAutoCompleteWithOneConcurrentCallPerSession(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testSessionPumpAutoCompleteWithMultipleConcurrentCallsPerSession() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testSessionPumpAutoCompleteWithMultipleConcurrentCallsPerSession(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testSessionPumpClientComplete() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testSessionPumpClientComplete(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testSessionPumpAbandonOnException() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testSessionPumpAbandonOnException(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}
	
	@Test
	public void testSessionPumpRenewLock() throws InterruptedException, ServiceBusException
	{
		this.createSessionfulQueueClient();
		MessageAndSessionPumpTests.testSessionPumpRenewLock(this.sessionfulQueueClient, this.sessionfulQueueClient);
	}	
}
