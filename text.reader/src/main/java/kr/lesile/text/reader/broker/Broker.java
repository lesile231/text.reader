package kr.lesile.text.reader.broker;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.lesile.text.reader.producer.TextProducer;
import kr.lesile.text.reader.publisher.Publisher;

public class Broker implements Publisher {
	private final ConcurrentMap<String, LinkedBlockingQueue<String>> partitionMap;
	private final int messageQueueLimit = 100;
	
	public Broker() {
		this.partitionMap = new ConcurrentHashMap<>();
	}

	@Override
	public LinkedBlockingQueue<String> getMessageQueue(String partitionId) {
		if(this.isPartitionIdExist(partitionId)) {
			return this.partitionMap.get(partitionId);
		} else {
			LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(this.messageQueueLimit);
			this.partitionMap.put(partitionId, messageQueue);
			return messageQueue;
		}
	}
	
	@Override
	public String receiveMessage_Nullable(String partitionId) {
		TimeUnit timeUnit = TimeUnit.SECONDS;
		long timeout = 1;
		
		try {
			return this.getMessageQueue(partitionId).poll(timeout, timeUnit);
		} catch (InterruptedException e) {
			System.out.println("Read Message From Message Queue is Failed... [TIME-OUT]["+timeout+" "+timeUnit+"]");
			return null;
		}
	}
	
	@Override
	public void sendMessage(String partitionId, String message) {
		if(isMessageEnd(message)) {
			this.sendEOFMessageToAllConsumers(message);
			return;
		}
		
		this.putMessageToQueue(this.getMessageQueue(partitionId), message);
	}
	
	private boolean isMessageEnd(String message) {
		return message.equals(TextProducer.EOF);
	}
	
	private void sendEOFMessageToAllConsumers(String message) {
		Set<String> keySet = this.partitionMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			this.putMessageToQueue(this.partitionMap.get(it.next()), message);
		}
	}
	
	private boolean putMessageToQueue(LinkedBlockingQueue<String> messageQueue, String message) {
		TimeUnit timeUnit = TimeUnit.SECONDS;
		long timeout = 1;
		
		try {
			messageQueue.offer(message, 1, TimeUnit.SECONDS);
			return true;
		} catch (InterruptedException e) {
			System.out.println("Message Sending is Failed... [TIME-OUT]["+timeout+" "+timeUnit+"]");
			return false;
		}
	}
	
	private boolean isPartitionIdExist(String partitionId) {
		return this.partitionMap.containsKey(partitionId);
	}
}
