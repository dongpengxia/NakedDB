package query;

import java.util.ArrayList;
import query_tree_nodes.QueryActionNode;
import query_where_filters.ComparatorFilter;
import relation.TableGolem;

public class QueryTree {

	private QueryActionNode queryTreeRoot;

	public QueryActionNode getQueryTreeRoot() {
		return queryTreeRoot;
	}
	public ArrayList<String> getTables() { return queryTreeRoot.getTables(); }
	public ArrayList<String> getAttributes() { return queryTreeRoot.getSubtreeAttributes(); }
	public ArrayList<ComparatorFilter> getFilters() { return queryTreeRoot.getSubtreeFilters(); }
	public int getResultTableID(){
		return queryTreeRoot.getResultTableID();
	}
	public boolean processQueryTree(){ return queryTreeRoot.processAction(); }

	public QueryTree(QueryActionNode queryActionNode) {
		queryTreeRoot = queryActionNode;
	}

	public QueryTree(final String queryStatement) {
		queryTreeRoot = QueryActionNode.createQueryActionNode(queryStatement);
	}

	public void addResultTableIDsToNodes() {
		int availableTableID = TableGolem.getNextAvailableID();
		ArrayList<QueryActionNode> traversalOrder = queryTreeRoot.subtreePostOrder();
		for(QueryActionNode queryActionNode: traversalOrder) {
			if(!(queryActionNode.getActionType().equalsIgnoreCase(Parser.GET_TABLE_ACTION))) {
				queryActionNode.setResultTableID(availableTableID);
				availableTableID = TableGolem.getNextAvailableID();
			}
		}
	}
	
	public void createEmptyResultTables() {
		ArrayList<QueryActionNode> traversalOrder = queryTreeRoot.subtreePostOrder();
		for (QueryActionNode queryActionNode: traversalOrder) {
			queryActionNode.createEmptyResultTable();
		}
	}
}