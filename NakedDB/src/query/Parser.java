package query;

import miscellaneous.Helpers;
import java.util.ArrayList;

public class Parser {

	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String EQUALS = "=";
	public static final String GREATER_THAN = ">";
	public static final String LESS_THAN = "<";
	public static final String JOIN_KEYWORD = "JOIN";
	public static final String SELECTION = "SELECT";
	public static final int SELECT_ATTRIBUTES_INDEX = 0;
	public static final int SELECT_FROM_INDEX = 1;
	public static final String FROM_KEYWORD = "FROM";
	public static final String WHERE = "WHERE";
	public static final int FROM_FROM_INDEX = 0;
	public static final int FROM_WHERE_INDEX = 1;
	public static final String GET_TABLE_ACTION = "GET_TABLE_ACTION";
	public static final String ON_KEYWORD = " ON ";
	public static final String DROP_KEYWORD = "DROP";
	public static final String TABLE_KEYWORD = "TABLE";
	public static final String IF_KEYWORD = "IF";
	public static final String EXISTS_KEYWORD = "EXISTS";
	public static final String NULL_KEYWORD = "NULL";
	public static final String PRIMARY_KEYWORD = "PRIMARY";
	public static final String KEY = "KEY";
	public static final String NOT_KEYWORD = "NOT";
	public static final String INDEX_FILE_SUFFIX = ".idx";
	public static final String DUMP = "DUMP";
	public static final String FILES_KEYWORD = "FILES";
	public static final String INDEX_KEYWORD = "INDEX";
	public static final String CREATE_KEYWORD = "CREATE";
	public static final String INSERT_KEYWORD = "INSERT";
	public static final String DELETE_KEYWORD = "DELETE";
	public static final String SOURCE_KEYWORD = "SOURCE";
	public static final String COMMIT_KEYWORD = "COMMIT";
	public static final String CLEAN_KEYWORD = "CLEAN";
	public static final String EXIT_KEYWORD = "EXIT";
	public static final String STRING_TYPE = "STRING";
	public static final String FLOAT_TYPE = "FLOAT";
	public static final String DOUBLE_TYPE = "DOUBLE";
	public static final String INTEGER_TYPE = "INT";
	public static final String LONG_TYPE = "LONG";
	public static final String CHARACTER_TYPE = "CHAR";
	public static final String BOOLEAN_TYPE = "BOOLEAN";
	public static final String BOOLEAN_TYPE_ABBV = "BOOL";
	public static final String TRUE_KEYWORD = "TRUE";
	public static final String NO_VALUE = "N/A";
	public static final String NUMBER_REGEX = "\\d*\\.?\\d*";
	public static final String QUERY_CREATED_TABLE = "QUERY_CREATED_TABLE";
	public static final String DATABASE_FILE_EXTENSION = ".table";
	public static final String MANIFEST_FILE = "manifest.txt";

	public static ArrayList<String> parseQueryAttributes(String queryStatement) {
		//parsing logic
		queryStatement = queryStatement.replace("(", "").replace(")", "");
		ArrayList<String> attributeNames = new ArrayList<>();
		for (String attributeStr: queryStatement.split(",")) {
			attributeStr = attributeStr.trim().toUpperCase().replace("\"","");
			attributeNames.add(attributeStr);
		}
		return attributeNames;
	}

	//sample input: A JOIN B JOIN C
	//sample output: [A, B, C]
	public static ArrayList<String> parseTableNames(final String queryStatement) {
		ArrayList<String> tableNames = new ArrayList<>();
		String inputStr = queryStatement.toUpperCase().trim();
		assert(inputStr.contains(Parser.JOIN_KEYWORD));
		inputStr = inputStr.replace("(","").replace(")","").replace("\"","");
		for(String tableName: inputStr.split(" ")) {
			if(!tableName.equalsIgnoreCase(Parser.JOIN_KEYWORD)) {
				tableNames.add(tableName.trim());
			}
		}
		return tableNames;
	}

	//sample input: "SELECT (ATTR2, ATTR1, ATTR3, ATTR4) FROM (Something)"
	//sample output: ["(ATTR2, ATTR1, ATTR3, ATTR4)", "FROM (Something)"]
	public static ArrayList<String> parseSelectFromStatement(final String statement) {
		ArrayList<String> attributesAndSource = new ArrayList<>();
		String inputStr = statement.toUpperCase();
		assert(inputStr.contains(Parser.SELECTION));
		assert(inputStr.contains(Parser.FROM_KEYWORD));
		int indexOfSpaceAfterSelect = inputStr.indexOf(Parser.SELECTION)+Parser.SELECTION.length();
		int indexOfFromKeyword = inputStr.indexOf(Parser.FROM_KEYWORD);
		assert(indexOfSpaceAfterSelect < indexOfFromKeyword);
		String attributesComponent = inputStr.substring(indexOfSpaceAfterSelect, indexOfFromKeyword);
		attributesAndSource.add(attributesComponent);
		String fromComponent = inputStr.substring(indexOfFromKeyword);
		attributesAndSource.add(fromComponent);
		return attributesAndSource;
	}

	//sample input:	FROM (X) WHERE (Y)
	//sample output: ["(X)", "WHERE (Y)"]
	public static ArrayList<String> parseFromWhereStatement(final String statement) {
		String inputStr = statement.toUpperCase();
		assert(inputStr.contains(Parser.FROM_KEYWORD));
		int indexOfSpaceAfterFrom = inputStr.indexOf(Parser.FROM_KEYWORD) + FROM_KEYWORD.length();
		String wordsAfterFrom = inputStr.substring(indexOfSpaceAfterFrom).trim();
		ArrayList<String> sourceAndCondition = new ArrayList<>();
		if(wordsAfterFrom.charAt(0) == '(') {
			//FROM (SOURCE)
			ArrayList<String> closedParenthesized = Helpers.getContentsWithinOuterParentheses(wordsAfterFrom);
			if(closedParenthesized.size() > 0) {
				String source = closedParenthesized.get(0);
				sourceAndCondition.add(source.trim());
				int indexOfSpaceAfterSource = wordsAfterFrom.indexOf(source) + source.length();
				String wordsAfterSource = wordsAfterFrom.substring(indexOfSpaceAfterSource);
				if (wordsAfterSource.contains(Parser.WHERE)) {
					String whereComponent = wordsAfterSource.substring(wordsAfterSource.indexOf(Parser.WHERE));
					sourceAndCondition.add(whereComponent.trim());
				}
			}
		}
		else {
			//FROM SOURCE
			if(wordsAfterFrom.contains(Parser.WHERE)) {
				int whereIndex = wordsAfterFrom.indexOf(Parser.WHERE);
				int sourceEndIndex = whereIndex;
				while(wordsAfterFrom.charAt(sourceEndIndex-1) == '(') {
					sourceEndIndex--;
				}
				String source = wordsAfterFrom.substring(0, sourceEndIndex);
				sourceAndCondition.add(source.trim());
				String whereComponent = wordsAfterFrom.substring(whereIndex);
				sourceAndCondition.add(whereComponent.trim());
			}
			else {
				sourceAndCondition.add(wordsAfterFrom.trim());
			}
		}
		return sourceAndCondition;
	}

	public static String removeTableNameFromAttributeName(String attributeName) {
		if (attributeName.contains(".")) {
			return attributeName.split("\\.")[1];
		}
		return attributeName;
	}
}