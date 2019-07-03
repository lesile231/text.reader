package kr.lesile.text.reader.consumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.lesile.text.reader.producer.TextProducer;
import kr.lesile.text.reader.publisher.Publisher;

public class TextConsumer implements Runnable {
	private final String partitionId;
	private final Publisher broker;
	private final String resultDirectoryPath;
	private final String ext = ".txt";
	
	public TextConsumer(Publisher broker, String partitionId, String resultDirectoryPath) {
		this.broker = broker;
		this.partitionId = partitionId;
		
		if(this.isLastCharSlash(resultDirectoryPath))
			resultDirectoryPath = resultDirectoryPath+"/";
		
		this.resultDirectoryPath = resultDirectoryPath;
	}

	@Override
	public void run() {
		try {
			while(true) {
				String receivedMessage = this.broker.receiveMessage_Nullable(partitionId);
				if(receivedMessage == null) {
					Thread.sleep(10);
					continue;
				}
				
				if(receivedMessage.equals(TextProducer.EOF)) {
					Thread.currentThread().interrupt();
					break;
				}
				
				String resultFilePath = this.makeResultFilePath(receivedMessage);
				try (FileWriter fw = new FileWriter(new File(resultFilePath), true)) {
					 fw.write(receivedMessage+System.lineSeparator());
				} catch (IOException e) {
					System.out.println("[TextConsumer] file write failed...");
				}
				
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			System.out.println("["+this.partitionId+"] Consumer Thread Shut Down");
		}
	}
	
	private String makeResultFilePath(String message) {
		return this.resultDirectoryPath+"/"+message.substring(0, 1).toLowerCase()+this.ext;
	}
	
	private boolean isLastCharSlash(String path) {
		return (path.charAt(path.length()-1) != '/');
	}
}
