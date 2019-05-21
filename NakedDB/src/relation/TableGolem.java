package relation;

import query.Parser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TableGolem {

	private static Map<Integer, Table> tableIDVsTable = new HashMap<>();
	private static Map<String, Integer> tableNameVsTableID = new HashMap<>();
	private static int nextAvailableID = 0;

	public static Collection<Table> getTables() { return tableIDVsTable.values(); }

	public static void addTable(Table table) {
		Integer id = table.getID();
		if(!tableIDVsTable.containsKey(id)){
			tableIDVsTable.put(table.getID(), table);
			tableNameVsTableID.put(table.getName().toUpperCase(), table.getID());
		}
		else {
			System.err.println("Error: id already taken");
		}
	}

	public static boolean dropTable(int tableID) {
		if(tableIDVsTable.containsKey(tableID)) {
			Table table = tableIDVsTable.get(tableID);
			String tableName = table.getName();
			tableIDVsTable.remove(tableID);
			tableNameVsTableID.remove(tableName);
			table.dropTable();
			return true;
		}
		return false;
	}

	public static boolean dropTable(String tableName) {
		tableName = tableName.trim().toUpperCase();
		if(tableNameVsTableID.containsKey(tableName)){
			int tableID = tableNameVsTableID.get(tableName);
			tableNameVsTableID.remove(tableName);
			Table table = tableIDVsTable.get(tableID);
			tableIDVsTable.remove(tableID);
			table.dropTable();
			return true;
		}
		return false;
	}

	public static Table getTable(int tableID) {
		return tableIDVsTable.get(tableID);
	}
	public static int getNextAvailableID() { return ++nextAvailableID; }
	public static int getHighestIDInUse() {
		return nextAvailableID;
	}
	public static void setNextAvailableID(int id) { nextAvailableID = id; }

	public static int getTableIDByName(String name) {
		name = name.toUpperCase();
		if(tableNameVsTableID.containsKey(name)) {
			return tableNameVsTableID.get(name);
		}
		return -1;
	}

	public static Table getTableByName(String name) {
		int tableID = getTableIDByName(name.toUpperCase());
		if(tableID == -1) {
			return null;
		}
		return getTable(tableID);
	}

	public static void writeTables() {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Parser.MANIFEST_FILE));
			for(Table table: tableIDVsTable.values()) {
				bufferedWriter.write(table.getFilename());
				bufferedWriter.write("\n");
				if(!table.write()) {
					System.err.println("Error writing table " + table.getFilename());
				}
			}
			bufferedWriter.close();
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public static String tostring() {
		StringBuilder sb = new StringBuilder();
		sb.append("ALL TABLES\n");
		sb.append("nextAvailableID: ").append(nextAvailableID);
		sb.append("\nnum tables: ").append(tableIDVsTable.size()).append("\n\n");
		for(Table table: tableIDVsTable.values()) {
			sb.append(table.toString());
		}
		return sb.toString();
	}

	public static String tableFileNames() {
		StringBuilder sb = new StringBuilder();
		for(Table table: tableIDVsTable.values()) {
			sb.append(table.getFilename()).append("\n");
		}
		return sb.toString();
	}

	public static void clearQueryCreatedTables() {
		for(int i = 0; i <= TableGolem.getHighestIDInUse(); i++) {
			Table table = TableGolem.getTable(i);
			if(table != null && table.getName().contains(Parser.QUERY_CREATED_TABLE)) {
				dropTable(table.getName());
			}
		}
	}
}