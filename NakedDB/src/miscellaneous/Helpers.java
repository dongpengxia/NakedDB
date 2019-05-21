package miscellaneous;

import java.util.ArrayList;

public class Helpers {

	//"something(abcdef(gh(kj)i))(jkl(mn))" -> ["(abcdef(gh(kj)i))", "(jkl(mn))"]
	public static ArrayList<String> getContentsWithinOuterParentheses(String str) {
		ArrayList<String> parenthesizedParts = new ArrayList<>();
		int numOpenParentheses = 0;
		int numCloseParentheses = 0;
		StringBuilder substring = new StringBuilder();
		for (char letter: str.toCharArray()) {
			if(letter == '(' || numOpenParentheses > 0) {
				substring.append(letter);
			}
			if(letter == '(') {
				numOpenParentheses++;
			}
			else if(letter == ')') {
				numCloseParentheses++;
				if(numOpenParentheses == numCloseParentheses) {
					parenthesizedParts.add(substring.toString());
					substring = new StringBuilder();
					numOpenParentheses = 0;
					numCloseParentheses = 0;
				}
			}
		}
		return parenthesizedParts;
	}

	public static int firstOccurrenceIndex(String search, String str) {
		int index = str.indexOf(search);
		if(index == -1) {
			return str.length();
		}
		return index;
	}
}