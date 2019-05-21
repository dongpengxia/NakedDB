package relation;

import query.Parser;

import java.io.*;
import java.util.*;

public class Table implements Serializable {

	//attributes and information about special attributes (attributes that have been indexed, attribute that is the primary key)
	private ArrayList<Attribute> attributes;
	private int primaryKeyAttributeListIndex; //the index in ArrayList attributes corresponding to the primary key
	private ArrayList<Integer> indexedAttributesListIndices = new ArrayList<>(); //the array indices of attributes in ArrayList attributes that have been indexed

	///where the files are stored
	private String filename;
	private ArrayList<String> indexFileNames;

	//identification info
	private int ID;
	private String tableName;

	//the actual table
	private TreeMap<String, ArrayList<Item>> table;

	//to prevent saving if table dropped
	private boolean dropped = false;

	private int incrementer = 0;

	public TreeMap<String, ArrayList<Item>> getLocalTable() { return table; }
	public Collection<ArrayList<Item>> getValues() { return table.values(); }

	public Table(final String tablename, final int newID) {
		filename = tablename+Parser.DATABASE_FILE_EXTENSION;
		tableName = tablename;
		ID = newID;
		attributes = new ArrayList<>();
		indexFileNames = new ArrayList<>();
		table = new TreeMap<>();
	}

	public Table(final String tablefilename) {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tablefilename)));
			Table tableReadFromFile = (Table)in.readObject();
			in.close();
			attributes = tableReadFromFile.attributes;
			primaryKeyAttributeListIndex = tableReadFromFile.primaryKeyAttributeListIndex;
			indexedAttributesListIndices = tableReadFromFile.indexedAttributesListIndices;
			filename = tableReadFromFile.filename;
			indexFileNames = tableReadFromFile.indexFileNames;
			ID = tableReadFromFile.ID;
			tableName = tableReadFromFile.tableName;
			table = tableReadFromFile.table;
		} catch (Exception e) {
			System.err.println("Error loading table from file: " + tablefilename);
			this.tableName = "";
			this.ID = -1;
		}
	}

	//sample input: "5, 6, 7, 8, 9"
	public boolean addRecord (String record) {
    	String[] attributeValues = record.trim().split(",");
    	if(attributeValues.length != attributes.size()) {
			return false;
		}

		String primaryKey = "";
  		ArrayList<Item> newRecord = new ArrayList<>();
    	for (int i = 0; i < attributes.size(); i++) {
			Item newItem = Item.createItem(attributeValues[i].trim(), attributes.get(i).getType());
			newRecord.add(newItem);
			if(i == primaryKeyAttributeListIndex && newItem != null) {
				primaryKey = newItem.getItemAsString();
			}
    	}
		if(!primaryKey.isEmpty() && !table.containsKey(primaryKey)) {
			table.put(primaryKey, newRecord);
			return true;
		}
		return false;
	}

	//sample input: "17, 18, 19", "attr1, attr2, attr3"
	//prerequisite: the order of attributenames in names is the same as the order of attributes in ArrayList attributes
	public boolean addRecord(final String attributevalues, final String attributenames) {
		String[] attributeValues = attributevalues.trim().split(",");
    	String[] attributeNames = attributenames.trim().split(",");
    	if(attributeValues.length != attributeNames.length) {
    		return false;
		}

		boolean[] hasValueToInsert = new boolean[attributes.size()]; //defaults to false
    	for (String attributeName: attributeNames) {
    		Attribute attribute = getAttributeByName(attributeName.trim());
    		if(attribute == null) {
    			System.err.println("Error: could not find attribute" + attributeName.trim());
    			return false;
			}
			hasValueToInsert[indexOfAttributeInAttributesArrayList(attribute.getID())] = true;
    	}

		String primaryKey = "";
		ArrayList<Item> newRecord = new ArrayList<>();
		int insertAttributeIndex = 0;
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			if(hasValueToInsert[i]) {
				Item newItem = Item.createItem(attributeValues[insertAttributeIndex++].trim(), attribute.getType());
				newRecord.add(newItem);
				if(i == primaryKeyAttributeListIndex && newItem != null){
					primaryKey = newItem.getItemAsString();
				}
			}
			else if(attribute.isNullable()) {
				newRecord.add(new Item());
			}
			else {
				System.err.println("Warning: no value provided for nonnullable attribute " + attribute.getName());
				return false;
			}
		}

		if(!primaryKey.isEmpty() && !table.containsKey(primaryKey)) {
			table.put(primaryKey, newRecord);
			return true;
		}
		return false;
	}

	private boolean hasIndexFile(final String filename) {
		for(String indexfilename: indexFileNames) {
			if (indexfilename.equalsIgnoreCase(filename)) {
				return true;
			}
		}
		return false;
	}

	public void addIndex(int indexInAttributesArrayListOfAttributeThatWasIndexed){
		indexedAttributesListIndices.add(indexInAttributesArrayListOfAttributeThatWasIndexed);
	}

	public boolean addIndex(final String filename) {
		if (!hasIndexFile(filename)) {
			indexFileNames.add(filename);
			return true;
		}
		return false;
	}

	public void removeIndex(final String filename) {
		indexFileNames.remove(filename);
	}

	public void removeIndex(int indexInAttributesArrayListOfAttributeThatWasUnindexed) {
		indexedAttributesListIndices.remove(Integer.valueOf(indexInAttributesArrayListOfAttributeThatWasUnindexed));
	}

	public ArrayList<String> getIndexFileNames() { return indexFileNames; }
	public ArrayList<Integer> getIndexedAttributesListIndices() { return indexedAttributesListIndices; }


	public Attribute getAttributeByName(String name){
		name = Parser.removeTableNameFromAttributeName(name);
		if(name == null) {
			return null;
		}
		for(Attribute attribute: attributes) {
			String attributeNameWithoutTable = Parser.removeTableNameFromAttributeName(attribute.getName());
			if(attributeNameWithoutTable.equalsIgnoreCase(name)){
				return attribute;
			}
		}
		return null;
	}

	public boolean hasAttributeWithName(final String attributeName) {
		return getAttributeByName(attributeName) != null;
	}

	public ArrayList<Attribute> getAttributes() { return attributes; }
	public void setAttributes(ArrayList<Attribute> attributes) { this.attributes = attributes; }
	public void addAttribute(Attribute attribute) { attributes.add(attribute); }

	public Attribute addAttribute(String name, Attribute.Type type, int newID, boolean nullable) {
		Attribute attribute = new Attribute(name, type, newID, nullable, ID);
		attribute.setContainerTableID(ID);
		attributes.add(attribute);
		return attribute;
	}

	public int getAttributeIndexInAttributesArrayListByAttributeName(String name) {
		name = Parser.removeTableNameFromAttributeName(name);
		if(name == null) {
			return -1;
		}
		for (int i = 0; i < attributes.size(); i++) {
			String attributeName = Parser.removeTableNameFromAttributeName(attributes.get(i).getName());
			if (name.equalsIgnoreCase(attributeName)) {
				return i;
			}
		}
		return -1;
	}

	private int indexOfAttributeInAttributesArrayList(int attributeID) {
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			if (attribute.getID() == attributeID) {
				return i;
			}
		}
		System.err.println("Error: Table doesn't have an attribute with ID " + attributeID);
		return -1;
	}

	public boolean putRecord(Object primaryKey, ArrayList<Item> record) {
		if(record.size() != attributes.size()) {
			System.err.println("Error: cannot put row of wrong size");
			return false;
		}

		String keyStr = Objects.toString(primaryKey);
		Item tmp = Item.createItem(keyStr, attributes.get(primaryKeyAttributeListIndex).getType());
		String key = tmp.getItemAsString();
		if(!table.containsKey(key)){
			table.put(key, record);
			return true;
		}
		System.err.println("Error: table already contains key: " + primaryKey);
		return false;
	}

	public Set<String> keySet() { return table.keySet(); }
	public int getIncrementer() { return ++incrementer; }
	public String getFilename() { return filename; }
	public int getID() { return ID; }
	public int getPrimaryKeyIndexInAttributesList() { return primaryKeyAttributeListIndex; }
	public String getName(){ return tableName; }
	public int size() { return table.size(); }
	public void clearTable() { table.clear(); }

	public boolean setKey(Attribute attribute) {
		int indexOfAttributeInAttributesArrayList = attributes.indexOf(attribute);
		if(indexOfAttributeInAttributesArrayList == -1) {
			return false;
		}
		primaryKeyAttributeListIndex = indexOfAttributeInAttributesArrayList;
		attribute.setNullable(false);
		return true;
	}

	public boolean containsKey(Object primaryKey) {
		String keyStr = Objects.toString(primaryKey);
		Item tmp = Item.createItem(keyStr, attributes.get(primaryKeyAttributeListIndex).getType());
		return table.containsKey(tmp.getItemAsString());
	}

	public ArrayList<Item> getRecord(Object primaryKey) {
		String keyStr = Objects.toString(primaryKey);
		Item tmp = Item.createItem(keyStr, attributes.get(primaryKeyAttributeListIndex).getType());
		return table.get(tmp.getItemAsString());
	}

	public boolean deleteRow(Object primaryKey) {
		String keyStr = Objects.toString(primaryKey);
		Item tmp = Item.createItem(keyStr, attributes.get(primaryKeyAttributeListIndex).getType());
		if(containsKey(tmp.getItemAsString())){
			table.remove(tmp.getItemAsString());
			return true;
		}
		return false;
	}

	public boolean dropTable(){
		dropped = true;
		clearTable();
		File file = new File(filename);
		try {
			file.delete();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void subtract(Table tableToSubtract) {
		String myPrimaryKey = attributes.get(primaryKeyAttributeListIndex).getName();

		int primaryKeyIndex = -1;
		ArrayList<Attribute> tableToSubtractAttributes = tableToSubtract.attributes;
		for(int i = 0; i < tableToSubtractAttributes.size(); i++) {
			if(tableToSubtractAttributes.get(i).getName().equalsIgnoreCase(myPrimaryKey)) {
				primaryKeyIndex = i;
			}
		}

		for(ArrayList<Item> recordToSubtract: tableToSubtract.getValues()) {
			Item keyToRemove = recordToSubtract.get(primaryKeyIndex);
			table.remove(keyToRemove.getItemAsString());
		}
	}

	public boolean write() {
		if(!dropped) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
				out.writeObject(this);
				out.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String toString() {
		final int TABLE_WIDTH = 100;
		StringBuilder sb = new StringBuilder("Table Name: ");
		sb.append(tableName);
		sb.append("\nkey index:").append(primaryKeyAttributeListIndex);
		sb.append("\nsaved to:").append(filename);
		sb.append("\nid:").append(ID);
		sb.append("\n").append("-".repeat(TABLE_WIDTH)).append("\n");
		for(Attribute attribute: attributes) {
			sb.append(attribute.getName()).append("\t");
		}
		sb.append("\n").append("-".repeat(TABLE_WIDTH)).append("\n");
		for(ArrayList<Item> record: table.values()) {
			for(Item item: record) {
				sb.append(item).append("\t");
			}
			sb.append("\n");
		}
		sb.append("-".repeat(TABLE_WIDTH)).append("\n");
		return sb.toString();
	}
}