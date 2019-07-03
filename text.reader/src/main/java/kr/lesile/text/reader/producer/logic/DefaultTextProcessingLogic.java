package kr.lesile.text.reader.producer.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultTextProcessingLogic implements TextProcessingLogic {

	@Override
	public boolean isValidText(String text) {
		if(text != null && text.length() > 0) {
			Pattern pattern = Pattern.compile("(^[a-z])");
			Matcher m = pattern.matcher(text.toLowerCase());
			return m.find();
		}
		
		return false;
	}
	
}
