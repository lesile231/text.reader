package kr.lesile.text.reader.main;

import kr.lesile.text.reader.broker.Broker;
import kr.lesile.text.reader.consumer.TextConsumer;
import kr.lesile.text.reader.producer.TextProducer;
import kr.lesile.text.reader.producer.logic.DefaultTextProcessingLogic;

public class BootStrap {
	public static void main(String[] args) {
		try {
			String processingFilePath = args[0];
			String resultDirectoryPath = args[1];
			int partitionCount = Integer.parseInt(args[2]);
			
			Broker broker = new Broker();
			BootStrap.initConsumer(partitionCount, broker, resultDirectoryPath);
			TextProducer producer = new TextProducer(broker, partitionCount, new DefaultTextProcessingLogic());
			producer.readFile(processingFilePath);
		} catch(ArrayIndexOutOfBoundsException oob) {
			System.out.println("Please insert 3 arguments.. - [Processing File Path][Result File Path][Partition Count]");
		} catch(NumberFormatException nfe) {
			System.out.println("Please insert third argument to number.. - [Partition Count]");
		}
	}
	
	public static void initConsumer(int partitionCount, Broker broker, String resultDirectoryPath) {
		for(int i=0; i<partitionCount; i++) {
			Thread th = new Thread(new TextConsumer(broker, String.valueOf(i), resultDirectoryPath));
			th.start();
		}
	}
}
