package relation;

import query.Parser;
import java.io.Serializable;

public class Item implements Serializable {

    private int anInt;
    private long aLong;
    private boolean aBoolean;
    private String aString;
    private float aFloat;
    private double aDouble;

    private Attribute.Type type;

    public Item(){
        this.type = Attribute.Type.NULL;
    }
    public Item(int i) {
        anInt = i;
        type = Attribute.Type.Int;
    }
    public Item(long l) {
        aLong = l;
        type = Attribute.Type.Long;
    }
    public Item(boolean b) {
        aBoolean = b;
        type = Attribute.Type.Boolean;
    }
    public Item(float f) {
        aFloat = f;
        type = Attribute.Type.Float;
    }
    public Item(double d) {
        aDouble = d;
        type = Attribute.Type.Double;
    }
    public Item(String s) {
        aString = s;
        type = Attribute.Type.Char;
    }

    public Object get() {
        switch(type) {
            case Char:
                return aString;
            case Int:
                return anInt;
            case Long:
                return aLong;
            case Boolean:
                return aBoolean;
            case Float:
                return aFloat;
            case Double:
                return aDouble;
        }
        return null;
    }

    public static Item createItem(String str, Attribute.Type type) {
        switch(type) {
            case Char:
                return new Item(str);
            case Int:
                return new Item(Integer.parseInt(str));
            case Long:
                return new Item(Long.parseLong(str));
            case Float:
                return new Item(Float.parseFloat(str));
            case Double:
                return new Item(Double.parseDouble(str));
            case Boolean:
                if(str.equalsIgnoreCase(Parser.TRUE_KEYWORD)) {
                    return new Item(true);
                }
                else {
                    return new Item(false);
                }
        }
        return null;
    }

    public String toString() {
        switch(type) {
            case Char:
                return aString;
            case Int:
                return String.valueOf(anInt);
            case Long:
                return String.valueOf(aLong);
            case Boolean:
                return String.valueOf(aBoolean);
            case Float:
                return String.valueOf(aFloat);
            case Double:
                return String.valueOf(aDouble);
        }
        return Parser.NO_VALUE;
    }

    public boolean isNumeric() {
        return this.type == Attribute.Type.Double ||
                this.type == Attribute.Type.Int ||
                this.type == Attribute.Type.Long ||
                this.type == Attribute.Type.Float;
    }

    //preserves ordering of numbers as strings
    public String getItemAsString() {
        switch(type) {
            case Char:
                return aString;
            case Int:
                return String.format("%08d", anInt);
            case Long:
                return String.format("%08d", aLong);
            case Boolean:
                return Parser.NO_VALUE;
            case Float:
                return String.format("%07.1f", aFloat);
            case Double:
                return String.format("%07.1f", aDouble);
        }
        return Parser.NO_VALUE;
    }
}