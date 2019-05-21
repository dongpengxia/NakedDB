package query_tree_nodes;

import query.Parser;
import query_where_filters.ComparatorFilter;
import query_where_filters.WhereFilter;
import relation.Item;
import relation.Table;
import relation.Attribute;
import relation.TableGolem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FromNode extends QueryActionNode {
	
	private WhereFilter whereFilter;

	public int numberOfChildren() { return 1 + leftChildQueryActionNode.numberOfChildren(); }
	public ArrayList<String> getTableDotAttributes(){
		return whereFilter.getAttributes();
	}

	public FromNode(final String fromStatement) {
		//parsing logic
		ArrayList<String> fromParts = Parser.parseFromWhereStatement(fromStatement);

		String sourceTableStatement = fromParts.get(Parser.FROM_FROM_INDEX);
		if(fromParts.size() > 1) {
			String filterStatement = fromParts.get(Parser.FROM_WHERE_INDEX);
			whereFilter = WhereFilter.createFilter(filterStatement);
		}
		setLeftChildQueryActionNode(QueryActionNode.createQueryActionNode(sourceTableStatement));
		leftChildQueryActionNode.setParent(this);
		setActionType(Parser.FROM_KEYWORD);
	}

	//todo: IDs without table names are used, fix to use table names
	//todo: check if merging with self and if so then print can't merge with self and return false;
	//todo: check if leftChildQueryActionNode is action join and if so see if we need merge join
	public boolean processAction() {
		if (!leftChildQueryActionNode.processAction()) {
			return false;
		}
		else {
			Table sourceTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
			ArrayList<Attribute> sourceTableAttributes = sourceTable.getAttributes();
			ArrayList<String> sourceTableAttributeTypes = new ArrayList<>();
			ArrayList<String> sourceTableAttributeNames = new ArrayList<>();
			for(Attribute sourceTableAttribute: sourceTableAttributes) {
				sourceTableAttributeTypes.add(sourceTableAttribute.getType().name());
				sourceTableAttributeNames.add(Parser.removeTableNameFromAttributeName(sourceTableAttribute.getName()));
			}
			String[] attributeTypes = sourceTableAttributeTypes.toArray((new String[0]));
			String[] attributeNames = sourceTableAttributeNames.toArray((new String[0]));

			Table resultTable = TableGolem.getTable(resultTableID);
			int key = 0;
			for(ArrayList<Item> sourceTableRow: sourceTable.getValues()) {
				String[] sourceTableNextRecord = new String[sourceTableRow.size()];
				for(int i = 0; i < sourceTableNextRecord.length; i++) {
					sourceTableNextRecord[i] = sourceTableRow.get(i).toString();
				}
				if (whereFilter == null || whereFilter.compare(attributeNames, sourceTableNextRecord, attributeTypes)) {
					resultTable.putRecord(key++, sourceTableRow);
				}
			}
			return true;
		}
	}

	public void createEmptyResultTable() {
		Table sourceTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
		Table resultTable = new Table(Parser.QUERY_CREATED_TABLE+resultTableID, resultTableID);
		for(Attribute sourceTableAttribute: sourceTable.getAttributes()) {
			resultTable.addAttribute(sourceTableAttribute);
		}
		TableGolem.addTable(resultTable);
	}

	public ArrayList<String> getTables() {
		if (leftChildQueryActionNode.getActionType().equalsIgnoreCase(Parser.GET_TABLE_ACTION)) {
			return new ArrayList<>(Collections.singletonList(((GetTableNode) leftChildQueryActionNode).getTableName()));
		}
		return leftChildQueryActionNode.getTables();
	}

	public ArrayList<String> getSubtreeAttributes() {
		ArrayList<String> subtreeAttributes = new ArrayList<>();
		if(whereFilter != null) {
			subtreeAttributes.addAll(whereFilter.getAttributes());
		}
		subtreeAttributes.addAll(leftChildQueryActionNode.getSubtreeAttributes());
		return subtreeAttributes;
	}

	public ArrayList<ComparatorFilter> getSubtreeFilters() {
		ArrayList <ComparatorFilter> subtreeFilters = new ArrayList<>();
		if (whereFilter != null) {
			subtreeFilters.addAll(whereFilter.getFilters());
		}
		subtreeFilters.addAll(leftChildQueryActionNode.getSubtreeFilters());
		return subtreeFilters;
	}

	public ArrayList<String> getParentAttributes() {
		Set<String> parentAttributes = new HashSet<>();
		if(whereFilter != null) {
			parentAttributes.addAll(whereFilter.getAttributes());
		}
		if(parent != null) {
			parentAttributes.addAll(parent.getParentAttributes());
		}
		return new ArrayList<>(parentAttributes);
	}
}