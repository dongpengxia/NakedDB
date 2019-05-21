package query;

import java.util.ArrayList;
import query_tree_nodes.SelectionNode;
import query_tree_nodes.QueryActionNode;

public class Optimizer {

	public static void insertSelectionsDownQueryTree(final QueryTree queryTree) {
		ArrayList<QueryActionNode> subtreePostOrder = queryTree.getQueryTreeRoot().subtreePostOrder();
		for(QueryActionNode queryActionNode: subtreePostOrder) {
			QueryActionNode parentNode = queryActionNode.getParent();
			if(parentNode != null
					&& !parentNode.getActionType().equalsIgnoreCase(Parser.FROM_KEYWORD)
					&& !parentNode.getActionType().equalsIgnoreCase(Parser.SELECTION)
					&& !queryActionNode.getActionType().equalsIgnoreCase(Parser.SELECTION)) {

				ArrayList<String> currentNodeAttributes = queryActionNode.getTableDotAttributes();
				ArrayList<String> parentNodeAttributes = parentNode.getParentAttributes();
				ArrayList<String> attributesForSelectionBetweenCurrentAndParent = new ArrayList<>();

				for(String currentNodeAttribute: currentNodeAttributes) {
					for(String parentNodeAttribute: parentNodeAttributes) {
						if(currentNodeAttribute.equalsIgnoreCase(parentNodeAttribute)) {
							attributesForSelectionBetweenCurrentAndParent.add(currentNodeAttribute);
						}
						else if(currentNodeAttribute.contains(".")) {
							String currentNodeAttributeOnly = currentNodeAttribute.substring(currentNodeAttribute.indexOf('.') + 1);
							if(currentNodeAttributeOnly.equalsIgnoreCase(parentNodeAttribute)) {
								attributesForSelectionBetweenCurrentAndParent.add(currentNodeAttribute);
							}
						}
					}
				}

				SelectionNode insertSelectionNode = new SelectionNode();
				insertSelectionNode.setRequestedAttributesNames(attributesForSelectionBetweenCurrentAndParent);
				insertSelectionNode.setParent(parentNode);
				insertSelectionNode.setLeftChildQueryActionNode(queryActionNode);
				queryActionNode.setParent(insertSelectionNode);
				if (parentNode.getLeftChildQueryActionNode() == queryActionNode) {
					parentNode.setLeftChildQueryActionNode(insertSelectionNode);
				}
				else if (parentNode.getRightChildQueryActionNode() == queryActionNode) {
					parentNode.setRightChildQueryActionNode(insertSelectionNode);
				}
			}
		}
	}
}