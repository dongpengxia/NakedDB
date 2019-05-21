package coreOS;

import java.io.*;
import java.util.*;

public class IndexGolem {

    private static TreeMap<String, Set<String>> attributeValueToKey = new TreeMap<>();

    public static void openIndex (final String indexFileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(indexFileName)));
            attributeValueToKey = (TreeMap<String, Set<String>>) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            closeIndex(indexFileName);
        } catch(IOException | ClassNotFoundException iox) {
            iox.printStackTrace();
        }
    }

    public static void closeIndex(final String indexFileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(indexFileName)));
            out.writeObject(attributeValueToKey);
            out.close();
            attributeValueToKey.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoIndex(Object attributeValue, Object key) {
        insertIntoIndex(Objects.toString(attributeValue, null), Objects.toString(key));
    }

    public static void insertIntoIndex(String attributeValue, String key) {
        Set<String> keys;
        if(attributeValueToKey.containsKey(attributeValue)) {
            keys = attributeValueToKey.get(attributeValue);
        }
        else {
            keys = new HashSet<>();
        }
        keys.add(key);
        attributeValueToKey.put(attributeValue, keys);
    }

    public static Optional<Set<String>> getKeys(Object attributeValue) {
        return getKeys(Objects.toString(attributeValue, null));
    }

    public static Optional<Set<String>> getKeys(String attributeValue) {
        if(attributeValueToKey.containsKey(attributeValue)) {
            return Optional.of(attributeValueToKey.get(attributeValue));
        }
        return Optional.empty();
    }

    public static String dumpIndex(String dumpStatement) {
        String[] tokens = dumpStatement.split("\\.");
        if(tokens.length != 2) {
            return "";
        }
        String filename = DBHelper.indexFileName(tokens[0], tokens[1]);
        openIndex(filename);
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Set<String>> entry: attributeValueToKey.entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue().toString()).append("\n");
        }
        closeIndex(filename);
        return sb.toString();
    }
}