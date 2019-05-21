package query_where_filters;

import java.util.ArrayList;
import miscellaneous.Helpers;
import query.Parser;

public abstract class WhereFilter {

	protected String filter; // >,<,=,AND,OR

	public abstract boolean compare(final String[] attributeNames, final String[] attributeValues, final String[] attributeTypes);
	public abstract ArrayList<String> getAttributes();
	public abstract ArrayList<ComparatorFilter> getFilters();
	public String getFilter() {
		return filter;
	}
	public static boolean isFilterKeyword(String word) { return (ComparatorFilter.isComparator(word) || CompoundFilter.isCompound(word)); }

	public static WhereFilter createFilter(final String statement) {
		//parsing logic
		String inputStr = statement.toUpperCase();
		if (inputStr.contains(Parser.WHERE)) {
			inputStr = inputStr.replace(Parser.WHERE, "");
		}
		inputStr = inputStr.trim();

		while(Helpers.getContentsWithinOuterParentheses(inputStr).size() == 1
				&& Helpers.getContentsWithinOuterParentheses(inputStr).get(0).length() == inputStr.length()) {
			//remove unnecessary surrounding parentheses
			inputStr = inputStr.substring(1,inputStr.length()-1);
			inputStr = inputStr.trim();
		}

		if(inputStr.charAt(0) == '(') {
			String firstItem = Helpers.getContentsWithinOuterParentheses(inputStr).get(0);
			inputStr = inputStr.substring(firstItem.length());
			inputStr = inputStr.trim();

			if(inputStr.startsWith(Parser.AND)) {
				String secondItem = inputStr.substring(Parser.AND.length()).trim();
				return new CompoundFilter(Parser.AND, firstItem, secondItem, statement);
			}
			else if(inputStr.startsWith(Parser.OR)) {
				String secondItem = inputStr.substring(Parser.OR.length()).trim();
				return new CompoundFilter(Parser.OR, firstItem, secondItem, statement);
			}
			else if(inputStr.startsWith(Parser.GREATER_THAN)
					|| inputStr.startsWith(Parser.LESS_THAN)
					|| inputStr.startsWith(Parser.EQUALS)){
				String comparator = inputStr.substring(0, 1);
				String secondItem = inputStr.substring(1).trim();
				return new ComparatorFilter(comparator, firstItem, secondItem, statement);
			}
		}
		else {
			if(inputStr.contains(Parser.AND) || inputStr.contains(Parser.OR)) {
				int comparatorStart =
						Math.min(
						Math.min(Helpers.firstOccurrenceIndex(" "+Parser.OR+"(",inputStr),Helpers.firstOccurrenceIndex(" "+Parser.OR+" ",inputStr)),
						Math.min(Helpers.firstOccurrenceIndex(" "+Parser.AND+"(",inputStr),Helpers.firstOccurrenceIndex(" "+Parser.AND+" ",inputStr)));
				String firstItem = inputStr.substring(0, comparatorStart).trim();
				inputStr = inputStr.substring(comparatorStart).trim();
				if(inputStr.startsWith(Parser.AND)) {
					String secondItem = inputStr.substring(Parser.AND.length()).trim();
					return new CompoundFilter(Parser.AND, firstItem, secondItem, statement);
				}
				else if(inputStr.startsWith(Parser.OR)) {
					String secondItem = inputStr.substring(Parser.OR.length()).trim();
					return new CompoundFilter(Parser.OR, firstItem, secondItem, statement);
				}
			}
			else {
				int comparatorStart =
						Math.min(
						Math.min(Helpers.firstOccurrenceIndex(Parser.EQUALS, inputStr), Helpers.firstOccurrenceIndex(Parser.LESS_THAN, inputStr)),
						Helpers.firstOccurrenceIndex(Parser.GREATER_THAN, inputStr));
				String firstItem = inputStr.substring(0, comparatorStart).trim();
				inputStr = inputStr.substring(comparatorStart).trim();
				String comparator = inputStr.substring(0, 1);
				assert(ComparatorFilter.isComparator(comparator));
				String secondItem = inputStr.substring(1).trim();
				return new ComparatorFilter(comparator, firstItem, secondItem, statement);
			}
		}
		return null;
	}
}