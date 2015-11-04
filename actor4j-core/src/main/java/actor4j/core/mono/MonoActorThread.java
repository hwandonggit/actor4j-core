/*
 * Copyright (c) 2015, David A. Bauer
 */
package actor4j.core.mono;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jctools.queues.MpscArrayQueue;

import actor4j.core.ActorSystemImpl;
import actor4j.core.ActorThread;
import actor4j.core.messages.ActorMessage;

public class MonoActorThread extends ActorThread {
	public Queue<ActorMessage<?>> directiveQueue;
	public Queue<ActorMessage<?>> innerQueue;
	public Queue<ActorMessage<?>> outerQueueL2;
	public Queue<ActorMessage<?>> outerQueueL1;
	public Queue<ActorMessage<?>> serverQueueL2;
	public Queue<ActorMessage<?>> serverQueueL1;
	
	public MonoActorThread(ActorSystemImpl system) {
		super(system);
		
		directiveQueue = new MpscArrayQueue<>(system.getQueueSize());
		serverQueueL2  = new MpscArrayQueue<>(system.getQueueSize());
		serverQueueL1  = new LinkedList<>();
		outerQueueL2   = new MpscArrayQueue<>(system.getQueueSize());
		outerQueueL1   = new LinkedList<>();
		innerQueue     = new CircularFifoQueue<>(system.getQueueSize());
	}
	
	@Override
	public void onRun() {
		boolean hasNextDirective = false;
		boolean hasNextServer 	 = false;
		boolean hasNextOuter     = false;
		boolean hasNextInner     = false;
		
		while (!isInterrupted()) {
			while (poll(directiveQueue)) 
				hasNextDirective=true;
			
			if (system.isClientMode()) {
				hasNextServer = poll(serverQueueL1);
				if (!hasNextServer && serverQueueL2.peek()!=null) {
					ActorMessage<?> message = null;
					for (int j=0; (message=serverQueueL2.poll())!=null && j<system.getBufferQueueSize(); j++)
						serverQueueL1.offer(message);
				
					hasNextServer = poll(serverQueueL1);
				}
			}
			
			hasNextOuter = poll(outerQueueL1);
			if (!hasNextOuter && outerQueueL2.peek()!=null) {
				ActorMessage<?> message = null;
				for (int j=0; (message=outerQueueL2.poll())!=null && j<system.getBufferQueueSize(); j++)
					outerQueueL1.offer(message);
				
				hasNextOuter = poll(outerQueueL1);
			}
			
			hasNextInner = poll(innerQueue);
			if ((!hasNextInner && !hasNextOuter && !hasNextServer && !hasNextDirective))
				if (!system.isSoftMode())
					yield();
				else {
					try {
						sleep(system.getSoftSleep());
					} catch (InterruptedException e) {
						interrupt();
					}
				}
		}		
	}
	
	public Queue<ActorMessage<?>> getInnerQueue() {
		return innerQueue;
	}
	
	public Queue<ActorMessage<?>> getOuterQueue() {
		return outerQueueL2;
	}
}
