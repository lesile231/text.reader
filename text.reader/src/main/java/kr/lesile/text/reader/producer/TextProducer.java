package kr.lesile.text.reader.producer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import kr.lesile.text.reader.broker.Broker;
import kr.lesile.text.reader.producer.logic.TextProcessingLogic;

public class TextProducer {
	public static String EOF = "EOF";
	private final Broker broker;
	private final TextProcessingLogic logic;
	private final int partitionCount;
		
	public TextProducer(Broker broker, int partitionCount, TextProcessingLogic logic) {
		this.logic = logic;
		this.partitionCount = partitionCount;
		this.broker = broker;
	}
	
	public void readFile(String readFilePath) {
		File file = new File(readFilePath);
		try (FileReader fileReader = new FileReader(file);
			 BufferedReader br = new BufferedReader(fileReader)) 
		{
			String message = "";
			while((message = br.readLine()) != null) {
				if(this.logic.isValidText(message))
					this.sendMessage(this.getPartitionId(message), message);
			}
			
			this.sendEOFMessage();
		} catch (FileNotFoundException e) {
			System.out.println("There is file not found...");
		} catch (IOException ioe) {
			System.out.println("File IO failed...");
		}
	}
	
	public void sendMessage(String partitionId, String message) {
		this.broker.sendMessage(partitionId, message);
	}
	
	private String getPartitionId(String message) {
		return String.valueOf(message.toLowerCase().charAt(0) % this.partitionCount);
	}
	
	private void sendEOFMessage() {
		this.sendMessage("EOF", TextProducer.EOF);
	}
}
