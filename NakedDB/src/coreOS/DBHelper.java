package coreOS;

import miscellaneous.Helpers;
import query.Parser;
import relation.Attribute;
import relation.Item;
import relation.Table;
import relation.TableGolem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class DBHelper {

    public DBHelper() {
    	try {
			File file = new File(Parser.MANIFEST_FILE);
			file.createNewFile(); //only creates if doesn't exist
			loadDatabase();
		} catch (Exception e) {
    		e.printStackTrace();
		}
    }

    private void loadDatabase() {
		try (BufferedReader br = new BufferedReader(new FileReader(Parser.MANIFEST_FILE))) {
			String line;
			int greatestOccupiedTableID = 0;
			while ((line = br.readLine()) != null) {
				Table table = new Table(line.trim());
				if(table.getID() != -1) {
					TableGolem.addTable(table);
					greatestOccupiedTableID = Math.max(greatestOccupiedTableID, table.getID());
				}
			}
			TableGolem.setNextAvailableID(greatestOccupiedTableID);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while reading Database Manifest file");
		}
	}

	public void writeDatabase() {
		TableGolem.writeTables();
	}

	public static String indexFileName(String tableName, String attributeName) { return tableName.toUpperCase() + "_" + attributeName.toUpperCase() + Parser.INDEX_FILE_SUFFIX; }

    public boolean createIndex(String createIndexStatement) {
    	createIndexStatement = createIndexStatement.toUpperCase().trim();
		int indexOfSpaceAfterOn = createIndexStatement.indexOf(Parser.ON_KEYWORD) + Parser.ON_KEYWORD.length();
		String tableDotAttribute = createIndexStatement.substring(indexOfSpaceAfterOn).trim();
		int indexOfDot = tableDotAttribute.indexOf(".");
		String tableName = tableDotAttribute.substring(0,indexOfDot).trim();
		String attributeName = tableDotAttribute.substring(indexOfDot+1).trim();
		String indexFileNm = indexFileName(tableName, attributeName);
		Table table = TableGolem.getTableByName(tableName);
		if(table == null) {
			return false;
		}
		ArrayList<Attribute> attributes = table.getAttributes();
		Attribute keyAttribute = attributes.get(table.getPrimaryKeyIndexInAttributesList());
		assert(!attributeName.equalsIgnoreCase(keyAttribute.getName())); //do not create index on primary key, as it is automatically indexed

		int indexInAttributesArrayListOfAttribute = -1;
		for (int i = 0; i < attributes.size(); i++) {
			if(attributes.get(i).getName().equalsIgnoreCase(attributeName)) {
				indexInAttributesArrayListOfAttribute = i;
			}
		}

		IndexGolem.openIndex(indexFileNm);
		TreeMap<String, ArrayList<Item>> localTable = table.getLocalTable();
		for(Map.Entry<String, ArrayList<Item>> entry: localTable.entrySet()) {
			String keyValue = entry.getKey();
			ArrayList<Item> record = entry.getValue();
			Item attrValue = record.get(indexInAttributesArrayListOfAttribute);
			String attributeValue = attrValue.getItemAsString();
			IndexGolem.insertIntoIndex(attributeValue, keyValue);
		}
		IndexGolem.closeIndex(indexFileNm);

		if(!table.addIndex(indexFileNm)) {
			return false;
		}
		table.addIndex(indexInAttributesArrayListOfAttribute);
        return true;
    }

    public boolean dropIndex(String dropIndexStatement) {
    	dropIndexStatement = dropIndexStatement.toUpperCase().trim();
		int spaceAfterOn = dropIndexStatement.indexOf(Parser.ON_KEYWORD) + Parser.ON_KEYWORD.length();
		String tableDotAttribute = dropIndexStatement.substring(spaceAfterOn).trim();
		int indexOfDot = tableDotAttribute.indexOf(".");
		String tableName = tableDotAttribute.substring(0,indexOfDot).trim();
		String attributeName = tableDotAttribute.substring(indexOfDot+1).trim();
		String indexFileNm = indexFileName(tableName, attributeName);
		File file = new File(indexFileNm);
		try{
			file.delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Table table = TableGolem.getTableByName(tableName);
		if(table == null) {
			return false;
		}
		ArrayList<Attribute> attributes = table.getAttributes();
		Attribute keyAttribute = attributes.get(table.getPrimaryKeyIndexInAttributesList());
		assert(!attributeName.equalsIgnoreCase(keyAttribute.getName()));

		int indexInAttributesArrayListOfAttributeToIndex = -1;
		for(int i = 0; i < attributes.size(); i++) {
			if(attributes.get(i).getName().equalsIgnoreCase(attributeName)) {
				indexInAttributesArrayListOfAttributeToIndex = i;
			}
		}
		table.removeIndex(indexFileNm);
		table.removeIndex(indexInAttributesArrayListOfAttributeToIndex);
		return true;
	}

    public boolean insertInto(String insertStatement) {
    	String[] tokens = insertStatement.split(" ");
    	String tableName;
    	if(tokens.length > 3) {
    		tableName = tokens[2].trim();
		}
    	else {
    		System.err.println("Error: " + insertStatement + " is not a valid command");
    		return false;
		}
    	Table insertTable = TableGolem.getTableByName(tableName);
    	if(insertTable == null ){
    		System.err.println("Error: could not find table " + tableName);
    		return false;
		}
    	ArrayList<String> parsedStatement = Helpers.getContentsWithinOuterParentheses(insertStatement);
    	if(parsedStatement.isEmpty()){
			System.err.println("Error: " + insertStatement + " is not a valid command");
			return false;
		}
    	else if(parsedStatement.size() == 1) {
    		String record = parsedStatement.get(0);
			return insertTable.addRecord(record.substring(1, record.length()-1).trim());
		}
    	else if(parsedStatement.size() == 2) {
    		String attributeValues = parsedStatement.get(1);
    		String attributeNames = parsedStatement.get(0);
    		return insertTable.addRecord(attributeValues.substring(1, attributeValues.length()-1).trim(), attributeNames.substring(1, attributeNames.length()-1).trim());
		}
    	System.err.println("Error: " + insertStatement + " is not a valid command");
    	return false;
    }

    public boolean createTable(final String createTableStatement) {
    	int spaceAfterTableKeyword = createTableStatement.toUpperCase().indexOf(Parser.TABLE_KEYWORD) + Parser.TABLE_KEYWORD.length();
    	String tableName = createTableStatement.substring(spaceAfterTableKeyword, createTableStatement.indexOf("(")).trim();
    	tableName = tableName.split(" ")[0].trim();
    	if (TableGolem.getTableByName(tableName) != null) {
    		System.err.println("Error: Table " + tableName + " already exists");
    		return false;
    	}
    	Table newTable = new Table(tableName, TableGolem.getNextAvailableID());

    	//each token represents an attribute declaration
		//i.e NAME TYPE [NOT NULL] [PRIMARY KEY];
    	StringTokenizer st = new StringTokenizer(createTableStatement.substring(createTableStatement.indexOf("(")+1, createTableStatement.indexOf(")")), ",");
		ArrayList<Attribute> attributeList = new ArrayList<>();
		Attribute primaryKey = null;
    	while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			String[] attributes = line.split(" ");
			if(attributes.length > 1) {
				String name = attributes[0].trim();
				Attribute.Type type = Attribute.stringToType(attributes[1].trim());
				if (type == Attribute.Type.Undeclared) {
					System.err.println("Error:" + attributes[1].trim() + " is not a valid type");
					return false;
				}
				else if(attributes.length == 2) {
					attributeList.add(new Attribute(name, type, newTable.getIncrementer(),false, newTable.getID()));
				}
				else if(attributes.length == 3) {
					if(attributes[2].trim().toUpperCase().equals(Parser.NULL_KEYWORD)) {
						attributeList.add(new Attribute(name, type, newTable.getIncrementer(), true, newTable.getID()));
					}
					else {
						System.err.println("Error: " + line + " is not valid");
						return false;
					}
				}
				else if(attributes.length == 4) {
					//see if it's PRIMARY KEY or NOT NULL
					if(attributes[2].trim().toUpperCase().equals(Parser.PRIMARY_KEYWORD)
							&& attributes[3].trim().toUpperCase().equals(Parser.KEY)) {
						primaryKey = new Attribute(name, type, newTable.getIncrementer(), false, newTable.getID());
						attributeList.add(primaryKey);
					}
					else if(attributes[2].trim().toUpperCase().equals(Parser.NOT_KEYWORD)
							&& attributes[3].trim().toUpperCase().equals(Parser.NULL_KEYWORD)) {
						attributeList.add(new Attribute(name, type, newTable.getIncrementer(), false, newTable.getID()));
					}
					else{
						System.err.println("Error: " + line + " is not valid");
						return false;
					}
				}
				else {
					System.err.println("Error: " + line + " is not valid");
					return false;
				}
			}
    	}

		if(!attributeList.isEmpty() && primaryKey != null){
			for(Attribute attribute: attributeList) {
				newTable.addAttribute(attribute);
			}
			if(!newTable.setKey(primaryKey)) {
				return false;
			}
			TableGolem.addTable(newTable);
			return true;
		}
		return false;
    }

    //assumption: there must be spaces separating table names
    public boolean handleDropTable(String dropTableStatement) {
		dropTableStatement = dropTableStatement.replace(",","");
    	String[] tokens = dropTableStatement.trim().split(" ");
		for(int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].trim();
		}

    	int firstTableIndex = 2;
		boolean dropIfExists = false;

 		if(tokens.length < 3) {
			System.err.println("Error: not enough tokens");
			return false;
		}
		else if(!tokens[0].equalsIgnoreCase(Parser.DROP_KEYWORD) || !tokens[1].equalsIgnoreCase(Parser.TABLE_KEYWORD)) {
			return false;
		}
		else if(tokens.length > 4) {
			if(tokens[2].equalsIgnoreCase(Parser.IF_KEYWORD) && tokens[3].equalsIgnoreCase(Parser.EXISTS_KEYWORD)){
				firstTableIndex = 4;
				dropIfExists = true;
			}
		}

		for(int i = firstTableIndex; i < tokens.length; i++) {
			String tableName = tokens[i];
			if(dropIfExists) {
				TableGolem.dropTable(tableName);
			} else if (!TableGolem.dropTable(tableName)) {
				System.err.println("Error: Could not find table " + tableName + " to drop");
				return false;
			}
		}
		return true;
	}

	public boolean clearTable(String tableName) {
    	tableName = tableName.replace("(","").replace(")","").trim();
    	Table table = TableGolem.getTableByName(tableName);
    	if(table != null) {
    		table.clearTable();
    		return true;
		}
    	return false;
	}
}