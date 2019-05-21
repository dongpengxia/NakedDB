package query_tree_nodes;

import java.util.ArrayList;
import query.Parser;
import query_where_filters.ComparatorFilter;

public abstract class QueryActionNode {

	protected QueryActionNode parent;
	protected QueryActionNode leftChildQueryActionNode;
	protected QueryActionNode rightChildQueryActionNode;
	protected String actionType;
	protected int resultTableID;

	public boolean processAction(){ return true; }
	public abstract void createEmptyResultTable();
	public abstract ArrayList <String> getTableDotAttributes();
	public abstract int numberOfChildren();
	public abstract ArrayList <String> getParentAttributes();
	public abstract ArrayList <String> getTables();
	public abstract ArrayList <String> getSubtreeAttributes();
	public abstract ArrayList <ComparatorFilter> getSubtreeFilters();

	public static QueryActionNode createQueryActionNode(String queryAction) {
		//parsing logic
		queryAction = queryAction.toUpperCase();
		String[] splitBySpaces = queryAction.replace("(", " ").trim().split(" ");
		String openingWord = splitBySpaces[0];

		if(openingWord.equalsIgnoreCase(Parser.SELECTION)) {
			return new SelectionNode(queryAction);
		}
		else if(openingWord.equalsIgnoreCase(Parser.FROM_KEYWORD)) {
			return new FromNode(queryAction);
		}
        else if(queryAction.toUpperCase().contains(Parser.JOIN_KEYWORD)) {
            return new CrossProductNode(queryAction);
        }
        else {
			return new GetTableNode(queryAction);
		}
	}

	public ArrayList<QueryActionNode> subtreePostOrder() {
		ArrayList<QueryActionNode> postorderTraversal = new ArrayList<>();
		if (leftChildQueryActionNode != null) {
			postorderTraversal.addAll(leftChildQueryActionNode.subtreePostOrder());
		}
		if (rightChildQueryActionNode != null) {
			postorderTraversal.addAll(rightChildQueryActionNode.subtreePostOrder());
		}
		postorderTraversal.add(this);
		return postorderTraversal;
	}

	public QueryActionNode getLeftChildQueryActionNode() {
		return leftChildQueryActionNode;
	}
	public QueryActionNode getRightChildQueryActionNode() {
		return rightChildQueryActionNode;
	}
	public QueryActionNode getParent() {
		return parent;
	}
	public int getResultTableID() {
		return resultTableID;
	}
	public String getActionType() {
		return actionType;
	}
	public void setLeftChildQueryActionNode(final QueryActionNode queryActionNode) {
		this.leftChildQueryActionNode = queryActionNode;
	}
	public void setRightChildQueryActionNode(final QueryActionNode queryActionNode) {
		this.rightChildQueryActionNode = queryActionNode;
	}
	public void setParent(final QueryActionNode parent) {
		this.parent = parent;
	}
	public void setResultTableID(final int resultTableID) {
		this.resultTableID = resultTableID;
	}
	public void setActionType(final String type) {
		this.actionType = type;
	}
}