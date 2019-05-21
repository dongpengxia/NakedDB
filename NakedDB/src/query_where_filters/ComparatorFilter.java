package query_where_filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import query.Parser;

public class ComparatorFilter extends WhereFilter {

	private String leftStatement;
	private String rightStatement;

	public ArrayList<ComparatorFilter> getFilters() { return new ArrayList<>(Collections.singletonList(this)); }

	public ArrayList<String> getAttributes() {
		ArrayList<String> attributes = new ArrayList<>();
		if (leftStatement != null) {
			String leftNoParentheses = leftStatement.replace("(", "").replace(")", "").trim();
			if(!leftNoParentheses.contains("\"") && !Pattern.matches(Parser.NUMBER_REGEX, leftNoParentheses)) {
				attributes.add(leftNoParentheses);
			}
		}
		if (rightStatement != null) {
			String rightNoParentheses = rightStatement.replace("(", "").replace(")", "").trim();
			if(!rightNoParentheses.contains("\"") && !Pattern.matches(Parser.NUMBER_REGEX, rightNoParentheses)) {
				attributes.add(rightNoParentheses);
			}
		}
		return attributes;
	}

	public ComparatorFilter(final String comparator, final String leftSide, final String rightSide, final String originalFilterStatement) {
		switch (comparator) {
			case Parser.LESS_THAN:
				filter = Parser.LESS_THAN;
				break;
			case Parser.GREATER_THAN:
				filter = Parser.GREATER_THAN;
				break;
			case Parser.EQUALS:
				filter = Parser.EQUALS;
				break;
		}
		leftStatement = leftSide;
		rightStatement = rightSide;
	}

	public static boolean isComparator(String comparator) {
		return (comparator.equals(Parser.EQUALS) || comparator.equals(Parser.GREATER_THAN) || comparator.equals(Parser.LESS_THAN));
	}

	//assumption: right side can be a constant, but left side must be an attribute
	public boolean compare(final String[] attributeNames, final String[] attributeValues, final String[] attributeTypes) {
		//parsing logic throughout function
		String leftNoParentheses = leftStatement.replace("(", "").replace(")", "").trim();
		String rightNoParentheses = rightStatement.replace("(", "").replace(")", "").trim();
		String leftAttributeName = Parser.removeTableNameFromAttributeName(leftNoParentheses);

		String leftValue = "";
		String leftType = "";
		for (int i = 0; i < attributeNames.length; i++) {
			if (leftAttributeName.equalsIgnoreCase(attributeNames[i])) {
				leftValue = attributeValues[i];
				leftType = attributeTypes[i];
			}
		}

		String rightValue = "";
		String rightType = "";
		if (rightNoParentheses.contains("\"")) {
			rightType = Parser.STRING_TYPE;
			rightValue = rightNoParentheses.substring(1, rightNoParentheses.length()-1); //remove quotes
		}
		else if (Pattern.matches(Parser.NUMBER_REGEX, rightNoParentheses)) {
			assert (leftType.equalsIgnoreCase(Parser.DOUBLE_TYPE) || leftType.equalsIgnoreCase(Parser.INTEGER_TYPE) || leftType.equalsIgnoreCase(Parser.FLOAT_TYPE));
			rightType = leftType;
			rightValue = rightNoParentheses;
		}
		else {
			String rightAttributeName = Parser.removeTableNameFromAttributeName(rightNoParentheses);
			for (int i = attributeNames.length-1; i >= 0; i--) { //search from right, useful for self joins
				if (rightAttributeName.equalsIgnoreCase(attributeNames[i])) {
					rightValue = attributeValues[i];
					rightType = attributeTypes[i];
				}
			}
		}

		assert (rightType.equalsIgnoreCase(leftType));
		if (rightType.equalsIgnoreCase(Parser.STRING_TYPE)) {
			rightValue = rightValue.replace("\"", "");
			switch (filter) {
				case Parser.EQUALS:
					return (leftValue.equals(rightValue));
				case Parser.LESS_THAN:
					return (leftValue.compareTo(rightValue) < 0);
				case Parser.GREATER_THAN:
					return (leftValue.compareTo(rightValue) > 0);
				default:
					return false;
			}
		}
		else if (rightType.equalsIgnoreCase(Parser.FLOAT_TYPE)) {
			switch (filter) {
				case Parser.EQUALS:
					return (Float.parseFloat(leftValue) == Float.parseFloat(rightValue));
				case Parser.LESS_THAN:
					return (Float.parseFloat(leftValue) < Float.parseFloat(rightValue));
				case Parser.GREATER_THAN:
					return (Float.parseFloat(leftValue) > Float.parseFloat(rightValue));
				default:
					return false;
			}
		}
		else if (rightType.equalsIgnoreCase(Parser.DOUBLE_TYPE)) {
			switch (filter) {
				case Parser.EQUALS:
					return (Double.parseDouble(leftValue) == Double.parseDouble(rightValue));
				case Parser.LESS_THAN:
					return (Double.parseDouble(leftValue) < Double.parseDouble(rightValue));
				case Parser.GREATER_THAN:
					return (Double.parseDouble(leftValue) > Double.parseDouble(rightValue));
				default:
					return false;
			}
		}
		else if (rightType.equalsIgnoreCase(Parser.INTEGER_TYPE)) {
			switch (filter) {
				case Parser.EQUALS:
					return (Integer.parseInt(leftValue) == Integer.parseInt(rightValue));
				case Parser.LESS_THAN:
					return (Integer.parseInt(leftValue) < Integer.parseInt(rightValue));
				case Parser.GREATER_THAN:
					return (Integer.parseInt(leftValue) > Integer.parseInt(rightValue));
				default:
					return false;
			}
		}
		return false;
	}
}