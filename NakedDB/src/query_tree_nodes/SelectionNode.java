package query_tree_nodes;

import query.Parser;
import query_where_filters.ComparatorFilter;
import relation.Item;
import relation.Table;
import relation.Attribute;
import relation.TableGolem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelectionNode extends QueryActionNode {

	private ArrayList<String> requestedAttributesNames;

	public SelectionNode() {
		setActionType(Parser.SELECTION);
	}

	//TODO: fix requestedAttributeNames to have TABLE.ATTRIBUTE format
	public ArrayList<String> getTableDotAttributes() { return requestedAttributesNames; }

	public int numberOfChildren() { return 1 + leftChildQueryActionNode.numberOfChildren(); }
	public ArrayList <ComparatorFilter> getSubtreeFilters() { return leftChildQueryActionNode.getSubtreeFilters(); }
	public void setRequestedAttributesNames(final ArrayList <String> newAttributes) { requestedAttributesNames = newAttributes; }

	public SelectionNode(final String queryStatement) {
		//parsing logic
		ArrayList<String> parts = Parser.parseSelectFromStatement(queryStatement);
		requestedAttributesNames = Parser.parseQueryAttributes(parts.get(Parser.SELECT_ATTRIBUTES_INDEX));
		leftChildQueryActionNode = QueryActionNode.createQueryActionNode(parts.get(Parser.SELECT_FROM_INDEX));
		leftChildQueryActionNode.setParent(this);
		setActionType(Parser.SELECTION);
	}

	//TODO: fz78 see what the children are
	public boolean processAction() {
		if(!leftChildQueryActionNode.processAction()) {
			return false;
		}
		Table sourceTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
		if (sourceTable == null) {
			return false;
		}

		ArrayList<String> resultTableDotAttributeNames = new ArrayList<>();
		for(String requestedAttributeName: requestedAttributesNames) {
			resultTableDotAttributeNames.add(sourceTable.getAttributeByName(requestedAttributeName).getName());
		}

		Table resultTable = TableGolem.getTable(resultTableID);
		if (resultTable == null) {
			return false;
		}

		int i = 0;
		for(ArrayList<Item> sourceTableRecord: sourceTable.getValues()) {
			ArrayList<Item> resultTableRecord = new ArrayList<>();
			for (String resultTableDotAttributeName: resultTableDotAttributeNames){
				int sourceTableIndex = sourceTable.getAttributeIndexInAttributesArrayListByAttributeName(resultTableDotAttributeName);
				resultTableRecord.add(sourceTableRecord.get(sourceTableIndex));
			}
			if(!resultTable.putRecord(i++, resultTableRecord)) {
				return false;
			}
		}
		return true;
	}

	public void createEmptyResultTable() {
		Table resultTable = new Table(Parser.QUERY_CREATED_TABLE+resultTableID, resultTableID);
		Table sourceTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
		int id = 0;
		for (String requestedAttributeName: requestedAttributesNames) {
			Attribute requestedAttribute = sourceTable.getAttributeByName(requestedAttributeName);
			resultTable.addAttribute(new Attribute(requestedAttributeName, requestedAttribute.getType(), id++, true, resultTableID));
		}
		TableGolem.addTable(resultTable);
	}

	public ArrayList<String> getParentAttributes() {
		if(parent == null) {
			return requestedAttributesNames;
		}
		else {
			Set<String> parentAttributes = new HashSet<>();
			parentAttributes.addAll(parent.getParentAttributes());
			parentAttributes.addAll(requestedAttributesNames);
			return new ArrayList<>(parentAttributes);
		}
	}

	public ArrayList <String> getTables() {
		if (leftChildQueryActionNode.getActionType().equalsIgnoreCase(Parser.GET_TABLE_ACTION)) {
			return new ArrayList<>(Collections.singletonList(((GetTableNode) leftChildQueryActionNode).getTableName()));
		}
		return leftChildQueryActionNode.getTables();
	}

	public ArrayList < String > getSubtreeAttributes() {
		ArrayList<String> subtreeAttributes = new ArrayList<>();
		subtreeAttributes.addAll(leftChildQueryActionNode.getSubtreeAttributes());
		subtreeAttributes.addAll(requestedAttributesNames);
		return subtreeAttributes;
	}
}