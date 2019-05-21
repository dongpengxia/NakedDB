package query;

import java.util.ArrayList;
import relation.Table;
import relation.TableGolem;

public class Checker {

	public static boolean checkQuery(final QueryTree queryTree) {
		return checkTables(queryTree) && checkAttributes(queryTree);
	}

	private static boolean checkAttributes(final QueryTree queryTree) {
		ArrayList<String> tableNames = queryTree.getTables();
		ArrayList<String> attributeNames = queryTree.getAttributes();

		ArrayList<Table> tables = new ArrayList<>();
		for (String tableName: tableNames) {
			tables.add(TableGolem.getTableByName(tableName));
		}

		for (String attributeName: attributeNames) {
			ArrayList<Table> tablesContainingAttribute = new ArrayList<>();
			for (Table table: tables) {
				if (table.hasAttributeWithName(attributeName)) {
					tablesContainingAttribute.add(table);
				}
			}
			if(tablesContainingAttribute.size() == 0) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkTables(final QueryTree queryTree) {
		ArrayList<String> tableNames = queryTree.getTables();
		for(String tableName: tableNames) {
			Table table = TableGolem.getTableByName(tableName);
			if (table == null) {
				return false;
			}
		}
		return true;
	}
}