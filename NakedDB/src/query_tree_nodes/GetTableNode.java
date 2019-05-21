package query_tree_nodes;

import java.util.ArrayList;
import java.util.Collections;
import query_where_filters.ComparatorFilter;
import query.Parser;
import relation.Attribute;
import relation.Table;
import relation.TableGolem;

//GetTable nodes are leaf nodes
public class GetTableNode extends QueryActionNode {

	private String tableName;

	public boolean processAction(){
		return true;
	}
	public void createEmptyResultTable() {}
	public int numberOfChildren() { return 0; }
	public ArrayList <String> getParentAttributes() { return parent.getParentAttributes(); }
	public ArrayList <String> getTables() { return new ArrayList<>(Collections.singletonList(tableName)); }
	public ArrayList <String> getSubtreeAttributes() { return new ArrayList<>(); }
	public ArrayList<ComparatorFilter> getSubtreeFilters() { return new ArrayList<>(); }
	public String getTableName() { return tableName; }

	public GetTableNode(final String nameOfTable) {

		//parsing logic
		int openParentheses = nameOfTable.indexOf('(');
		int closeParentheses = nameOfTable.indexOf(')', openParentheses+1);
		if(closeParentheses == -1) {
			closeParentheses = nameOfTable.length();
		}
		tableName = nameOfTable.substring(openParentheses+1, closeParentheses).replace("\"","").trim();
		if(tableName.contains(" ")){
			tableName = tableName.split(" ")[1];
		}

		this.resultTableID = TableGolem.getTableIDByName(tableName);
		if(resultTableID == -1) {
			System.err.println("ERROR: table " + tableName + " does not exist in TableGolem");
		}
		actionType = Parser.GET_TABLE_ACTION;
	}

	public ArrayList<String> getTableDotAttributes() {
		Table table = TableGolem.getTableByName(tableName);
		if(table == null) {
			return null;
		}
		ArrayList<String> tableDotAttributeList = new ArrayList<>();
		for(Attribute attr: table.getAttributes()) {
			tableDotAttributeList.add(table.getName()+"."+attr.getName());
		}
		return tableDotAttributeList;
	}
}