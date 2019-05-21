package query_tree_nodes;

import java.util.ArrayList;
import query.Parser;
import query_where_filters.ComparatorFilter;
import relation.Item;
import relation.Table;
import relation.Attribute;
import relation.TableGolem;

public class CrossProductNode extends QueryActionNode {

	public int numberOfChildren() { return 2 + leftChildQueryActionNode.numberOfChildren() + rightChildQueryActionNode.numberOfChildren(); }
	public ArrayList <String> getParentAttributes() {
		return parent.getParentAttributes();
	}

	//sample input: JOIN B, C, D
	//sample output:
	//					JOIN
	//					/   \
	//				   B    JOIN
	//						/  \
	//					   C   D
	public CrossProductNode(final String queryStatement) {
		//parsing logic
		ArrayList<String>tableList = Parser.parseTableNames(queryStatement);

		if (tableList.size() > 2) {
			//multiple crosses needed, right child is another cross
			String nextCrossProduct = String.join(" "+Parser.JOIN_KEYWORD+" ", tableList.subList(1, tableList.size()));
			rightChildQueryActionNode = QueryActionNode.createQueryActionNode(nextCrossProduct);
		}
		else {
			//only two tables to cross, right child is a table
			rightChildQueryActionNode = new GetTableNode(tableList.get(1));
		}

		leftChildQueryActionNode = QueryActionNode.createQueryActionNode(tableList.get(0));
		leftChildQueryActionNode.setParent(this);
		rightChildQueryActionNode.setParent(this);
		setActionType(Parser.JOIN_KEYWORD);
	}

	public boolean processAction() {
		if(!leftChildQueryActionNode.processAction() || !rightChildQueryActionNode.processAction()){
			return false; //one of the children failed, return immediately
		}
		else {
			//cross two tables
			Table leftTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
			Table rightTable = TableGolem.getTable(rightChildQueryActionNode.getResultTableID());
			Table resultTable = TableGolem.getTable(this.resultTableID);
			int key = 0;
			for(ArrayList<Item> leftRecord: leftTable.getValues()) {
				for(ArrayList<Item> rightRecord: rightTable.getValues()) {
					ArrayList<Item> crossProductRecord = new ArrayList<>();
					crossProductRecord.addAll(leftRecord);
					crossProductRecord.addAll(rightRecord);
					resultTable.putRecord(key++, crossProductRecord);
				}
			}
			return true;
		}
	}

	public void createEmptyResultTable() {
		Table leftTable = TableGolem.getTable(leftChildQueryActionNode.getResultTableID());
		Table rightTable = TableGolem.getTable(rightChildQueryActionNode.getResultTableID());
		Table resultTable = new Table(Parser.QUERY_CREATED_TABLE+resultTableID, resultTableID);

		ArrayList<Attribute> childTableAttributes = new ArrayList<>();
		childTableAttributes.addAll(leftTable.getAttributes());
		childTableAttributes.addAll(rightTable.getAttributes());

		int id = 0;
		for(Attribute attribute: childTableAttributes) {
			String tableDotAttribute = attribute.getName();
			if(!tableDotAttribute.contains(".")) {
				String parentName = TableGolem.getTable(attribute.getContainerTableID()).getName();
				tableDotAttribute =  parentName + "." + tableDotAttribute;
			}
			Attribute newAttribute = new Attribute(tableDotAttribute, attribute.getType(), id++, true, attribute.getContainerTableID());
			resultTable.addAttribute(newAttribute);
		}
		TableGolem.addTable(resultTable);
	}

	public ArrayList<String> getTableDotAttributes(){
		ArrayList<String> attributes = new ArrayList<>();
		attributes.addAll(leftChildQueryActionNode.getTableDotAttributes());
		attributes.addAll(rightChildQueryActionNode.getTableDotAttributes());
		return attributes;
	}

	public ArrayList<String> getTables() {
		ArrayList<String> tables = new ArrayList<>();
		tables.addAll(leftChildQueryActionNode.getTables());
		tables.addAll(rightChildQueryActionNode.getTables());
		return tables;
	}

	public ArrayList<String> getSubtreeAttributes() {
		ArrayList<String> subtreeAttributes = new ArrayList<>();
		subtreeAttributes.addAll(leftChildQueryActionNode.getSubtreeAttributes());
		subtreeAttributes.addAll(rightChildQueryActionNode.getSubtreeAttributes());
		return subtreeAttributes;
	}

	public ArrayList <ComparatorFilter> getSubtreeFilters() {
		ArrayList<ComparatorFilter> filters = new ArrayList<>();
		filters.addAll(leftChildQueryActionNode.getSubtreeFilters());
		filters.addAll(rightChildQueryActionNode.getSubtreeFilters());
		return filters;
	}
}