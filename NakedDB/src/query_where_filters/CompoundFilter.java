package query_where_filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import query.Parser;

public class CompoundFilter extends WhereFilter {

	private WhereFilter leftSideFilter;
	private WhereFilter rightSideFilter;

	public CompoundFilter(final String comparator, final String leftSide, final String rightSide, final String originalFilterStatement) {
		filter = comparator;
		leftSideFilter = WhereFilter.createFilter(leftSide);
		rightSideFilter = WhereFilter.createFilter(rightSide);
	}

	public static boolean isCompound(String comparator) {
		comparator = comparator.toUpperCase();
		return (comparator.equals(Parser.AND) || comparator.equals(Parser.OR));
	}

	public boolean compare(final String[] attributeNames, final String[] attributeValues, final String[] attributeTypes) {
		boolean leftHandEvaluation = leftSideFilter.compare(attributeNames, attributeValues, attributeTypes);
		boolean rightHandEvaluation = rightSideFilter.compare(attributeNames, attributeValues, attributeTypes);
		if (filter.equalsIgnoreCase(Parser.AND)) {
			return (leftHandEvaluation && rightHandEvaluation);
		}
		else if (filter.equalsIgnoreCase(Parser.OR)) {
			return (leftHandEvaluation || rightHandEvaluation);
		}
		return false;
	}

	public ArrayList<String> getAttributes() {
		Set<String> attributes = new HashSet<>();
		attributes.addAll(leftSideFilter.getAttributes());
		attributes.addAll(rightSideFilter.getAttributes());
		return new ArrayList<>(attributes);
	}

	public ArrayList<ComparatorFilter> getFilters() {
		Set<ComparatorFilter> comparatorFilters = new HashSet<>();
		comparatorFilters.addAll(leftSideFilter.getFilters());
		comparatorFilters.addAll(rightSideFilter.getFilters());
		return new ArrayList<>(comparatorFilters);
	}
}