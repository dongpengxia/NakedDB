package relation;

import query.Parser;
import java.io.Serializable;

public class Attribute implements Serializable {

	public enum Type { Int, Long, Boolean, Char, Float, Double, Undeclared, NULL }

	private Type type;
	private String name;
	private int ID;
	private boolean nullable;
	private int containerTableID;

	public int getID() {
		return ID;
	}
	public String getName() {
		return name;
	}
	public int getContainerTableID() {return containerTableID;}
	public Type getType() {
		return type;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setName(final String newName) {
		this.name = newName;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public void setContainerTableID(int containerTableID) {
		this.containerTableID = containerTableID;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String toString() { return "name: " + name + "\ntype: " + type + "\nnullable: " + nullable + "\ncontainerTableID: " + containerTableID + "\nid: " + ID; }

	public Attribute(String name, Type type, int ID, boolean nullable, int parentID) {
		this.name = name;
		this.type = type;
		this.ID = ID;
		this.nullable = nullable;
		this.containerTableID = parentID;
	}

	public static Type stringToType(final String newType) {
		String input = newType.toUpperCase();
		if (input.startsWith(Parser.CHARACTER_TYPE)) {
			return Type.Char;
		}
		switch (input) {
			case Parser.INTEGER_TYPE:
				return Type.Int;
			case Parser.LONG_TYPE:
				return Type.Long;
			case Parser.BOOLEAN_TYPE:
			case Parser.BOOLEAN_TYPE_ABBV:
				return Type.Boolean;
			case Parser.FLOAT_TYPE:
				return Type.Float;
			case Parser.DOUBLE_TYPE:
				return Type.Double;
		}
		return Type.Undeclared;
	}
}