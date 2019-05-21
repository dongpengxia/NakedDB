import coreOS.NakedDatabase;
import query.Optimizer;
import query.QueryTree;
import relation.Table;
import relation.TableGolem;
import java.util.Objects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionalTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void FunctionalTests() {



        //CREATE TABLE TEST



        String createTableInput1 =  "CREATE TABLE Relation (key int PRIMARY KEY, v1 int NULL, v2 int, v3 int, v4 double);";
        NakedDatabase.parseSQLStatementMaster(createTableInput1);
        Table table1 = TableGolem.getTableByName("Relation");
        String str1 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:1\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertNotNull(table1);
        assertEquals(str1, table1.toString());
        assertTrue(table1.hasAttributeWithName("key"));
        assertTrue(table1.hasAttributeWithName("v1"));
        assertTrue(table1.hasAttributeWithName("v2"));
        assertTrue(table1.hasAttributeWithName("v3"));
        assertTrue(table1.hasAttributeWithName("v4"));
        assertFalse(table1.getAttributeByName("key").isNullable());
        assertTrue(table1.getAttributeByName("v1").isNullable());
        assertFalse(table1.getAttributeByName("v2").isNullable());
        assertFalse(table1.getAttributeByName("v3").isNullable());
        assertFalse(table1.getAttributeByName("v4").isNullable());
        assertTrue(table1.getAttributeByName("Relation.key").getID() != -1);
        assertTrue(table1.getAttributeByName("Relation.v1").getID() !=  -1);
        assertTrue(table1.getAttributeByName("Relation.v2").getID() != -1);



        //INSERT RECORD TEST



        String insertInput1 = "INSERT INTO Relation (5, 6, 7, 8, 9)";
        String insertInput2 = "INSERT INTO Relation VALUES (10, 11, 12, 13, 14)";
        String insertInput3 = "INSERT INTO Relation VALUES (15, 16, 17, 18, 19)";
        String insertInput4 = "INSERT INTO Relation VALUES (20, 21, 22, 23, 24)";
        NakedDatabase.parseSQLStatementMaster(insertInput1);
        NakedDatabase.parseSQLStatementMaster(insertInput2);
        NakedDatabase.parseSQLStatementMaster(insertInput3);
        NakedDatabase.parseSQLStatementMaster(insertInput4);

        String expectedOutputTable1 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:1\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "10\t11\t12\t13\t14.0\t\n" +
                "15\t16\t17\t18\t19.0\t\n" +
                "20\t21\t22\t23\t24.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expectedOutputTable1, table1.toString());
        String expectedOutputGolem1 = "ALL TABLES\n" +
                "nextAvailableID: 1\n" +
                "num tables: 1\n" +
                "\n" +
                "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:1\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "10\t11\t12\t13\t14.0\t\n" +
                "15\t16\t17\t18\t19.0\t\n" +
                "20\t21\t22\t23\t24.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expectedOutputGolem1, TableGolem.tostring());

        String insertInput5 = "INSERT INTO Relation (V2, V3, V4) VALUES ( 100, 118, 119 )";
        String insertInput6 = "INSERT INTO Relation (Key, V2, V3, V4) VALUES ( 25, 26, 27, 28, 29 )";
        String insertInput7 = "INSERT INTO Relation (Key, V2, V3, V4) VALUES ( 30, 31, 32, 33 )";
        String insertInput8 = "INSERT INTO Relation (Key, V2, V3, V4) VALUES ( 25, 26, 27, 28 )";
        assertEquals("Error inserting record", NakedDatabase.parseSQLStatementMaster(insertInput5));
        assertEquals("Error inserting record", NakedDatabase.parseSQLStatementMaster(insertInput6));
        assertEquals("Record successfully inserted", NakedDatabase.parseSQLStatementMaster(insertInput7));
        assertEquals("Record successfully inserted", NakedDatabase.parseSQLStatementMaster(insertInput8));

        String expectedOutputTable2 = "Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:1\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "5\t6\t7\t8\t9.0\t\n" +
                "10\t11\t12\t13\t14.0\t\n" +
                "15\t16\t17\t18\t19.0\t\n" +
                "20\t21\t22\t23\t24.0\t\n" +
                "25\tN/A\t26\t27\t28.0\t\n" +
                "30\tN/A\t31\t32\t33.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expectedOutputTable2, table1.toString());

        String createTable1 = "CREATE TABLE tbl1 (attr1 int PRIMARY KEY, attr2 int)";
        String insert1 = "INSERT INTO tbl1 VALUES(1, 2)";
        String insert2 = "INSERT INTO tbl1 VALUES(5, 6)";
        String insert3 = "INSERT INTO tbl1 VALUES(8,9)";
        NakedDatabase.parseSQLStatementMaster(createTable1);
        NakedDatabase.parseSQLStatementMaster(insert1);
        NakedDatabase.parseSQLStatementMaster(insert2);
        NakedDatabase.parseSQLStatementMaster(insert3);
        Table tbl1 = TableGolem.getTableByName("tbl1");
        assertNotNull(tbl1);
        String selectInput = "select attr2, attr1 from tbl1";
        String resultTable = NakedDatabase.parseSQLStatementMaster(selectInput);
        String expectedResult1 = "Table Name: QUERY_CREATED_TABLE4\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE4.table\n" +
                "id:4\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "ATTR2\tATTR1\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "2\t1\t\n" +
                "6\t5\t\n" +
                "9\t8\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expectedResult1, resultTable);
        String expectedResult2 = FunctionalTestConstants.tc1;
        assertEquals(expectedResult2, TableGolem.tostring());

        String createTable2 = "CREATE TABLE tbl2 (attr3 int PRIMARY KEY, attr4 int)";
        String insert4 = "INSERT INTO tbl2 VALUES (12, 3)";
        String insert5 = "INSERT INTO tbl2 VALUES (55, 7)";
        String insert6 = "INSERT INTO tbl2 VALUES (9, 94)";
        NakedDatabase.parseSQLStatementMaster(createTable2);
        NakedDatabase.parseSQLStatementMaster(insert4);
        NakedDatabase.parseSQLStatementMaster(insert5);
        NakedDatabase.parseSQLStatementMaster(insert6);
        String expectedOutput3 = FunctionalTestConstants.tc2;
        assertEquals(expectedOutput3, TableGolem.tostring());



        //SELECT TEST & JOIN TEST



        String joinInput = "select attr2, attr1, attr3, attr4 from tbl1 join tbl2";
        NakedDatabase.parseSQLStatementMaster(joinInput);
        String expectedOutput4 = FunctionalTestConstants.tc3;
        assertEquals(expectedOutput4, TableGolem.tostring());



        //SELECT WHERE TEST



        String whereInput = "select attr2, attr1, attr3, attr4 from QUERY_CREATED_TABLE9 WHERE attr2 = attr3";
        NakedDatabase.parseSQLStatementMaster(whereInput);
        String expected10 = "Table Name: QUERY_CREATED_TABLE14\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE14.table\n" +
                "id:14\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "ATTR2\tATTR1\tATTR3\tATTR4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "9\t8\t9\t94\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expected10, Objects.requireNonNull(TableGolem.getTableByName("QUERY_CREATED_TABLE14")).toString());
        String expected11 = FunctionalTestConstants.tc4;
        assertEquals(expected11, TableGolem.tostring());



        //SELECT WHERE AND MULTIPLE COMPARATORS TEST



        String andInput = "select attr2, attr1, attr3, attr4 from QUERY_CREATED_TABLE9 WHERE ((attr2 < attr3) AND (attr4 > attr1))";
        NakedDatabase.parseSQLStatementMaster(andInput);
        String expected12 = "Table Name: QUERY_CREATED_TABLE17\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE17.table\n" +
                "id:17\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "ATTR2\tATTR1\tATTR3\tATTR4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "2\t1\t9\t94\t\n" +
                "2\t1\t12\t3\t\n" +
                "2\t1\t55\t7\t\n" +
                "6\t5\t9\t94\t\n" +
                "6\t5\t55\t7\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(expected12, TableGolem.getTable(17).toString());

        QueryTree queryTree = new QueryTree(joinInput);
        Optimizer.insertSelectionsDownQueryTree(queryTree);
        queryTree.addResultTableIDsToNodes();
        queryTree.createEmptyResultTables();
        queryTree.processQueryTree();
        Table table17 = TableGolem.getTableByName("QUERY_CREATED_TABLE23");
        String expected13 = "Table Name: QUERY_CREATED_TABLE23\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE23.table\n" +
                "id:23\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "ATTR2\tATTR1\tATTR3\tATTR4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "2\t1\t9\t94\t\n" +
                "2\t1\t12\t3\t\n" +
                "2\t1\t55\t7\t\n" +
                "6\t5\t9\t94\t\n" +
                "6\t5\t12\t3\t\n" +
                "6\t5\t55\t7\t\n" +
                "9\t8\t9\t94\t\n" +
                "9\t8\t12\t3\t\n" +
                "9\t8\t55\t7\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertNotNull(table17);
        assertEquals(expected13, table17.toString());



        //NESTED SELECT & MULTIPLE WHERE & MULTIPLE COMPARATORS & MULTIPLE COMPOUND FILTERS TEST



        String superInput = "select attr2, attr1, attr3 from (select attr1, attr2, attr3, attr4 from (select attr4, attr3, attr2, attr1 from tbl1 join tbl2 WHERE attr3 < attr4)) WHERE (attr2 < attr3 AND attr4 > attr1) OR attr4 = 3 OR attr4 = 94";
        NakedDatabase.parseSQLStatementMaster(superInput);
        String expectedNews = FunctionalTestConstants.tc5;
        assertEquals(expectedNews, TableGolem.tostring());



        //MULTIPLE JOIN TEST



        String joinInput2 = "select attr1, attr4, v1 from tbl1 join tbl2 join Relation";
        NakedDatabase.parseSQLStatementMaster(joinInput2);
        String expectedNew2 = FunctionalTestConstants.tc6;
        assertEquals(expectedNew2, Objects.requireNonNull(TableGolem.getTableByName("QUERY_CREATED_TABLE42")).toString());



        //CREATE & DROP & DUMP INDEX TEST



        String indexMake = "CREATE INDEX ON tbl1.attr2";
        String result = NakedDatabase.parseSQLStatementMaster(indexMake);
        assertEquals("Index Successfully Created", result);

        assertEquals("00000002 : [00000001]\n" +
                "00000006 : [00000005]\n" +
                "00000009 : [00000008]\n"
                , NakedDatabase.parseSQLStatementMaster("DUMP INDEX tbl1.attr2"));

        String indexDelete = "DROP INDEX ON tbl1.attr2";
        result = NakedDatabase.parseSQLStatementMaster(indexDelete);
        assertEquals("Index dropped", result);

        String tableGolemFinalResult = FunctionalTestConstants.tc7;
        assertEquals(tableGolemFinalResult, TableGolem.tostring());



        //DROP TABLE TEST & DROP TABLE IF EXISTS TEST



        String drop1 = "DROP TABLE QUERY_CREATED_TABLE42";
        NakedDatabase.parseSQLStatementMaster(drop1);
        assertNull(TableGolem.getTable(42));
        String tableGolemdropped42 = FunctionalTestConstants.tc8;
        assertEquals(tableGolemdropped42, TableGolem.tostring());

        String dropifExists = "DROP TABLE IF EXISTS tabledoesnotexist";
        assertEquals("Table(s) successfully dropped", NakedDatabase.parseSQLStatementMaster(dropifExists));
        assertEquals(tableGolemdropped42, TableGolem.tostring());

        String dropTables = "DROP TABLE QUERY_CREATED_TABLE41, QUERY_CREATED_TABLE40, QUERY_CREATED_TABLE39";
        assertEquals("Table(s) successfully dropped", NakedDatabase.parseSQLStatementMaster(dropTables));
        String drop3Results = FunctionalTestConstants.tc9;
        assertEquals(drop3Results, TableGolem.tostring());

        String dropTablesIfExists = "DROP TABLE IF EXISTS doesnotexist, QUERY_CREATED_TABLE38, QUERY_CREATED_TABLE37, notthisone, QUERY_CREATED_TABLE36";
        assertEquals("Table(s) successfully dropped", NakedDatabase.parseSQLStatementMaster(dropTablesIfExists));
        String dropTablesIfExistsOutput = FunctionalTestConstants.tc10;
        assertEquals(dropTablesIfExistsOutput, TableGolem.tostring());



        //DELETE FROM TEST



        String query = "DELETE FROM Relation WHERE v2 < 20 or v3 > 25";
        NakedDatabase.parseSQLStatementMaster(query);
        assertEquals("Table Name: Relation\n" +
                "key index:0\n" +
                "saved to:Relation.table\n" +
                "id:1\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "key\tv1\tv2\tv3\tv4\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "20\t21\t22\t23\t24.0\t\n" +
                "----------------------------------------------------------------------------------------------------\n",
                Objects.requireNonNull(TableGolem.getTableByName("Relation")).toString());
        String deleteAllEntriesquery = "DELETE FROM RELation";
        NakedDatabase.parseSQLStatementMaster(deleteAllEntriesquery);
        assertEquals("Table Name: Relation\n" +
                        "key index:0\n" +
                        "saved to:Relation.table\n" +
                        "id:1\n" +
                        "----------------------------------------------------------------------------------------------------\n" +
                        "key\tv1\tv2\tv3\tv4\t\n" +
                        "----------------------------------------------------------------------------------------------------\n" +
                        "----------------------------------------------------------------------------------------------------\n",
                Objects.requireNonNull(TableGolem.getTableByName("Relation")).toString());
        NakedDatabase.parseSQLStatementMaster(insertInput1);
        NakedDatabase.parseSQLStatementMaster(insertInput2);
        NakedDatabase.parseSQLStatementMaster(insertInput3);
        NakedDatabase.parseSQLStatementMaster(insertInput4);
        NakedDatabase.parseSQLStatementMaster(insertInput7);
        NakedDatabase.parseSQLStatementMaster(insertInput8);
        assertEquals("Table Name: Relation\n" +
                        "key index:0\n" +
                        "saved to:Relation.table\n" +
                        "id:1\n" +
                        "----------------------------------------------------------------------------------------------------\n" +
                        "key\tv1\tv2\tv3\tv4\t\n" +
                        "----------------------------------------------------------------------------------------------------\n" +
                        "5\t6\t7\t8\t9.0\t\n" +
                        "10\t11\t12\t13\t14.0\t\n" +
                        "15\t16\t17\t18\t19.0\t\n" +
                        "20\t21\t22\t23\t24.0\t\n" +
                        "25\tN/A\t26\t27\t28.0\t\n" +
                        "30\tN/A\t31\t32\t33.0\t\n" +
                        "----------------------------------------------------------------------------------------------------\n",
                Objects.requireNonNull(TableGolem.getTableByName("Relation")).toString());



        //DROP TABLE & WIPE FILES TEST



        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        String comma = "";
        for(int i = 0; i < TableGolem.getHighestIDInUse(); i++) {
            Table t = TableGolem.getTable(i);
            if(t != null) {
                sb.append(comma);
                sb.append(t.getName());
                comma = ", ";
            }
        }
        String dropEverything = sb.toString();

        //MANUALLY TEST THAT TABLES ARE PROPERLY WRITTEN AND DELETED
        TableGolem.writeTables();
        assertEquals("Table(s) successfully dropped", NakedDatabase.parseSQLStatementMaster(dropEverything));
        assertEquals("ALL TABLES\n" +
                "nextAvailableID: 45\n" +
                "num tables: 0\n\n", TableGolem.tostring());
        TableGolem.writeTables();
    }
}