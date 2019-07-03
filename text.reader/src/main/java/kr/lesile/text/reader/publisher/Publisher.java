package kr.lesile.text.reader.publisher;

import java.util.Queue;

public interface Publisher {
	public Queue<String> getMessageQueue(String partitionId); 
	public String receiveMessage_Nullable(String partitionId);
	public void sendMessage(String partitionId, String message);
}

