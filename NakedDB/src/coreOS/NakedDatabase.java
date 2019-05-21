package coreOS;

import query.Checker;
import query.Optimizer;
import query.Parser;
import query.QueryTree;
import query_tree_nodes.FromNode;
import relation.Table;
import relation.TableGolem;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class NakedDatabase {
    
	private static DBHelper dbHelper = new DBHelper();
	private static boolean clearResultTables = false;

	public static void setClearResultTables(boolean b) {clearResultTables = b;}
	public static boolean getClearResultTables() {return clearResultTables;}
    
    public static String parseSQLStatementMaster(final String input) {
		String inputStatement = input.trim();
    	String[] splitBySpaces = inputStatement.split(" ");
		if(inputStatement.equalsIgnoreCase(Parser.DUMP)){
			return TableGolem.tostring();
		}
		else if (splitBySpaces.length == 2 && splitBySpaces[0].trim().equalsIgnoreCase(Parser.DUMP) && splitBySpaces[1].trim().equalsIgnoreCase(Parser.FILES_KEYWORD)) {
			return TableGolem.tableFileNames();
		}
		else if(splitBySpaces.length == 3 && splitBySpaces[0].trim().equalsIgnoreCase(Parser.DUMP) && splitBySpaces[1].trim().equalsIgnoreCase(Parser.INDEX_KEYWORD)){
			return IndexGolem.dumpIndex(splitBySpaces[2]);
		}
    	else if (splitBySpaces[0].replace("(","").equalsIgnoreCase(Parser.SELECTION)) {
    		QueryTree queryTree = new QueryTree(inputStatement);
			if(Checker.checkQuery(queryTree)) {
				Optimizer.insertSelectionsDownQueryTree(queryTree);
				if(Checker.checkQuery(queryTree)) {
					queryTree.addResultTableIDsToNodes();
					queryTree.createEmptyResultTables();
					if(queryTree.processQueryTree()) {
						int finalResultTableID = queryTree.getResultTableID();
						for (Table table : TableGolem.getTables()) {
							if (clearResultTables && table.getName().contains(Parser.QUERY_CREATED_TABLE) && table.getID() != finalResultTableID) {
								if(!table.dropTable()) {
									return "error dropping table";
								}
							}
						}
						return TableGolem.getTable(finalResultTableID).toString();
					}
					return "error processing query tree";
				}
				return "error: query tree check failed after optimization";
			}
			return "error: query tree check failed";
    	}
    	else if (splitBySpaces.length > 1 && splitBySpaces[0].trim().equalsIgnoreCase(Parser.CREATE_KEYWORD)) {
    		if (splitBySpaces[1].trim().equalsIgnoreCase(Parser.TABLE_KEYWORD)) {
    			if (dbHelper.createTable(inputStatement.trim())){
    				return "Table Successfully Created";
    			}
    			return "Error Creating Table";
    		}
    		else if (splitBySpaces[1].equalsIgnoreCase(Parser.INDEX_KEYWORD)){
    			if (dbHelper.createIndex(inputStatement)){
    				return "Index Successfully Created";
    			}
    			return "Error Creating Index";
    		}
    		return "Statement not Recognized";
    	}
    	else if (splitBySpaces[0].trim().equalsIgnoreCase(Parser.INSERT_KEYWORD)) {
    		if (dbHelper.insertInto(inputStatement)){
    			return "Record successfully inserted";
    		}
    		return "Error inserting record";
    	}
    	else if (splitBySpaces[0].trim().equalsIgnoreCase(Parser.DROP_KEYWORD)) {
    		if(splitBySpaces.length > 1) {
    			if (splitBySpaces[1].trim().equalsIgnoreCase(Parser.TABLE_KEYWORD)) {
    				if (dbHelper.handleDropTable(inputStatement)) {
    					return "Table(s) successfully dropped";
					}
    				return "";
				}
    			else if(splitBySpaces[1].trim().equalsIgnoreCase(Parser.INDEX_KEYWORD)){
    				if(dbHelper.dropIndex(inputStatement)) {
						return "Index dropped";
					}
    				return "Error dropping index";
				}
    			return "Error: could not understand drop statement";
			}
    		return "Not enough arguments in drop statement";
		}
    	else if (splitBySpaces[0].trim().equalsIgnoreCase(Parser.DELETE_KEYWORD)) {
    		if(splitBySpaces.length < 3) {
    			return "Not enough arguments in delete statement";
			}
    		else if (splitBySpaces[1].equalsIgnoreCase(Parser.FROM_KEYWORD)) {
    			if(inputStatement.toUpperCase().contains(Parser.WHERE) && splitBySpaces.length > 4){
					//minimum where command is at least 6 long: DELETE FROM TABLE WHERE x <>= y
					FromNode fromNode = new FromNode(inputStatement.toUpperCase());
					QueryTree queryTree = new QueryTree(fromNode);
					queryTree.addResultTableIDsToNodes();
					queryTree.createEmptyResultTables();
					if(!queryTree.processQueryTree()) {
						return "error processing query tree";
					}

					int FromNodeResultTableID = fromNode.getResultTableID();
					Table resultTableWithItemsToRemove = TableGolem.getTable(FromNodeResultTableID);
					if(resultTableWithItemsToRemove == null) {
						return "Error: " + Parser.QUERY_CREATED_TABLE + FromNodeResultTableID + " not found";
					}
					String sourceTable = splitBySpaces[2].toUpperCase().replace("(","").replace(")","").trim();
					if(TableGolem.getTableIDByName(sourceTable) != -1) {
						Table originalTable = TableGolem.getTableByName(sourceTable);
						if(originalTable != null) {
							originalTable.subtract(resultTableWithItemsToRemove);
							return "Deletion successful";
						}
						return "Error getting table " + sourceTable;
					}
					return "Error: delection unsuccessful";
				}
    			else if (splitBySpaces.length == 3) {
					if(dbHelper.clearTable(splitBySpaces[2])) {
						return splitBySpaces[2] + " was successfully cleared";
					}
					return "Could not find table: " + splitBySpaces[2] + "to delete from";
				}
    			return "Could not understand delete from statement";
			}
		}
    	else if(splitBySpaces[0].trim().equalsIgnoreCase(Parser.SOURCE_KEYWORD)) {
			if(splitBySpaces.length < 2) {
				return "Error: Not enough arguments in source statement";
			}
			String sqlScriptFilename = splitBySpaces[1].replace("(","").replace(")","").trim();
			processSQLScript(sqlScriptFilename);
			return("SQL commands in " + sqlScriptFilename + " executed successfully.");
		}
    	else if(splitBySpaces[0].trim().equalsIgnoreCase(Parser.COMMIT_KEYWORD)) {
    		TableGolem.writeTables();
    		return "Tables written";
		}
		else if(splitBySpaces[0].trim().equalsIgnoreCase(Parser.CLEAN_KEYWORD)) {
			TableGolem.clearQueryCreatedTables();
			return "Query created tables deleted";
		}
        return "Statement " + input + " not processed successfully";
    }

	//sqlFileName needs the extension, example: "demoStartingScript.sql"
	private static void processSQLScript(String sqlFileName) {
		try {
			File file = new File(sqlFileName);
			Scanner sc = new Scanner(file).useDelimiter(";");
			while (sc.hasNext()) {
				String semicolonEndingStatement = sc.next();
				parseSQLStatementMaster(semicolonEndingStatement);
			}
			sc.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] arguments){
		String input;
		Scanner sc = new Scanner(System.in).useDelimiter(";");
		while ((input = sc.next()) != null) {
			System.out.println(input);
			if (input.trim().equalsIgnoreCase(Parser.EXIT_KEYWORD)) {
				dbHelper.writeDatabase();
				System.exit(1);
			} else {
				System.out.println(parseSQLStatementMaster(input));
			}
		}
	}
}