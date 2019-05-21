import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import relation.Attribute;
import relation.Item;
import relation.Table;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class TableTest {

    private Table table;

    @Before
    public void setUp() {
        table = new Table("Relation", 5);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void addAttributeAddRecordWriteClearTest() {
        Attribute key = new Attribute("key", Attribute.Type.Int, 13, true, 5);
        Attribute v1 = new Attribute("v1", Attribute.Type.Int, 10, true, 5);
        Attribute v2 = new Attribute("v2", Attribute.Type.Int, 11, false, 5);
        Attribute v3 = new Attribute("v3", Attribute.Type.Int, 12, false, 5);
        table.addAttribute(key);
        table.addAttribute(v1);
        table.addAttribute(v2);
        table.addAttribute(v3);
        table.addAttribute("v4", Attribute.Type.Double, 14, false);
        assertEquals(5, table.getAttributes().size());
        assertTrue(table.hasAttributeWithName("key"));
        assertTrue(table.hasAttributeWithName("v1"));
        assertTrue(table.hasAttributeWithName("v2"));
        assertTrue(table.hasAttributeWithName("v3"));
        assertTrue(table.hasAttributeWithName("v4"));

        table.setKey(key);
        assertEquals(0, table.getPrimaryKeyIndexInAttributesList());
        assertFalse(table.getAttributeByName("key").isNullable());
        assertTrue(table.getAttributeByName("v1").isNullable());
        assertFalse(table.getAttributeByName("v2").isNullable());
        assertFalse(table.getAttributeByName("v3").isNullable());
        assertFalse(table.getAttributeByName("v4").isNullable());
        assertEquals(table.getAttributeByName("Relation.key").getID(), key.getID());
        assertEquals(table.getAttributeByName("Relation.v1").getID(), v1.getID());
        assertEquals(table.getAttributeByName("Relation.v2").getID(), v2.getID());
        assertEquals(14, table.getAttributeByName("V4").getID());

        assertFalse(table.addRecord("17, 18, 19", "V2, V3, V4"));
        assertFalse(table.addRecord("17, 18, 19, 20 ", "V1, V2, V3, V4"));
        assertFalse(table.addRecord(" 17, 18, 19 ", "Key, V2, V3, V4)"));
        assertTrue(table.addRecord(" 17, 18, 19, 20 ", "Key, V2, V3, V4"));

        table.addRecord( "5, 6, 7, 8, 9");
        table.addRecord( "9, 10, 11, 12, 13");
        table.addRecord( "13, 14, 15, 16, 14");
        String tableString = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "9\t10\t11\t12\t13.0\t\n" +
                "13\t14\t15\t16\t14.0\t\n" +
                "17\tN/A\t18\t19\t20.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(table.toString(), tableString);
        assertTrue(table.containsKey(5));
        assertTrue(table.containsKey(9));
        assertTrue(table.containsKey(13));
        assertFalse(table.containsKey(20));

        ArrayList<Item> items = table.getRecord(5);
        assertEquals(5, items.size());
        assertEquals(5, (int) items.get(0).get());
        assertEquals(6, (int) items.get(1).get());
        assertEquals(7, (int) items.get(2).get());
        assertEquals(8, (int) items.get(3).get());
        assertEquals(9, (Double) items.get(4).get(), 0.0);

        table.write();
        Table newTable = new Table(table.getFilename());
        assertEquals(5, newTable.getAttributes().size());
        assertTrue(newTable.hasAttributeWithName("key"));
        assertTrue(newTable.hasAttributeWithName("v1"));
        assertTrue(newTable.hasAttributeWithName("v2"));
        assertTrue(newTable.hasAttributeWithName("v3"));
        assertTrue(newTable.hasAttributeWithName("v4"));

        assertEquals(0, newTable.getPrimaryKeyIndexInAttributesList());
        assertFalse(newTable.getAttributeByName("key").isNullable());
        assertTrue(newTable.getAttributeByName("v1").isNullable());
        assertFalse(newTable.getAttributeByName("v2").isNullable());
        assertFalse(newTable.getAttributeByName("v3").isNullable());
        assertFalse(newTable.getAttributeByName("v4").isNullable());
        assertEquals(newTable.getAttributeByName("Relation.key").getID(), key.getID());
        assertEquals(newTable.getAttributeByName("Relation.v1").getID(), v1.getID());
        assertEquals(newTable.getAttributeByName("Relation.v2").getID(), v2.getID());
        assertEquals(14, newTable.getAttributeByName("V4").getID());
        assertEquals(newTable.toString(), tableString);
        assertTrue(newTable.containsKey(5));
        assertTrue(newTable.containsKey(9));
        assertTrue(newTable.containsKey(13));
        assertFalse(newTable.containsKey(20));
        items.clear();
        items = newTable.getRecord(5);
        assertEquals(items.size(), 5);
        assertEquals(5, (int) items.get(0).get());
        assertEquals(6, (int) items.get(1).get());
        assertEquals(7, (int) items.get(2).get());
        assertEquals(8, (int) items.get(3).get());
        assertEquals(9, (Double) items.get(4).get(), 0.0);


        assertFalse(newTable.addRecord("17, 18, 19 ", "V2, V3, V4"));
        assertFalse(newTable.addRecord(" 17, 18, 19, 20 ", "V1, V2, V3, V4"));
        assertFalse(newTable.addRecord(" 17, 18, 19 ", "Key, V2, V3, V4"));
        assertTrue(newTable.addRecord(" 21, 18, 19, 20 ", "Key, V2, V3, V4"));
        newTable.addRecord("22, 23, 24, 25, 26");
        newTable.addRecord("27, 28, 29, 30, 31");
        assertEquals("Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "9\t10\t11\t12\t13.0\t\n" +
                "13\t14\t15\t16\t14.0\t\n" +
                "17\tN/A\t18\t19\t20.0\t\n" +
                "21\tN/A\t18\t19\t20.0\t\n" +
                "22\t23\t24\t25\t26.0\t\n" +
                "27\t28\t29\t30\t31.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n"
                , newTable.toString());

        newTable.write();
        Table table3 = new Table(newTable.getFilename());
        String tableDump3 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "9\t10\t11\t12\t13.0\t\n" +
                "13\t14\t15\t16\t14.0\t\n" +
                "17\tN/A\t18\t19\t20.0\t\n" +
                "21\tN/A\t18\t19\t20.0\t\n" +
                "22\t23\t24\t25\t26.0\t\n" +
                "27\t28\t29\t30\t31.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(tableDump3, table3.toString());
        table3.deleteRow(13);
        table3.deleteRow(17);
        table3.deleteRow(21);
        assertEquals("Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "9\t10\t11\t12\t13.0\t\n" +
                "22\t23\t24\t25\t26.0\t\n" +
                "27\t28\t29\t30\t31.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n",
                table3.toString());
        table3.write();

        Table table4 = new Table(table3.getFilename());
        String tableDump4 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "9\t10\t11\t12\t13.0\t\n" +
                "22\t23\t24\t25\t26.0\t\n" +
                "27\t28\t29\t30\t31.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(tableDump4, table4.toString());

        table4.clearTable();
        tableDump4 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:5\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(tableDump4, table4.toString());
        table4.dropTable();
        System.out.println("BELOW ERROR IS EXPECTED: Error loading table from file: Relation.table");
        Table table5 = new Table(table4.getFilename());
        assertEquals(table5.getName(), "");
        assertEquals(table5.getID(), -1);
        assertNull(table5.getAttributes());

        Table table6 = new Table("testNew", 50);
        table6.addAttribute(new Attribute("v1", Attribute.Type.Int, 1, true, 50));
        table6.addAttribute(new Attribute("v2", Attribute.Type.Int, 1, true, 50));
        Attribute aprime = new Attribute("key", Attribute.Type.Int, 1, true, 50);
        table6.addAttribute(aprime);
        table6.setKey(aprime);
        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(new Item(1));
        items1.add(new Item(2));
        items1.add(new Item(3));
        ArrayList<Item> items12 = new ArrayList<>();
        items12.add(new Item(4));
        items12.add(new Item(5));
        items12.add(new Item(6));
        ArrayList<Item> items123 = new ArrayList<>();
        items123.add(new Item(7));
        items123.add(new Item(8));
        items123.add(new Item(9));
        table6.putRecord("1", items1);
        table6.putRecord("4", items12);
        table6.putRecord("7", items123);
    }
}