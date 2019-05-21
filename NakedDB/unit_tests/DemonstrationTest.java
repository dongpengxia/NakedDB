import coreOS.NakedDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import relation.TableGolem;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class DemonstrationTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void createInsertSelectSelfJoinWhereTest() {

        int size = 10;
        String createA10 = "CREATE TABLE a10test (a int PRIMARY KEY, b int)";
        ArrayList<String> loadA10 = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            loadA10.add("INSERT INTO a10test VALUES ("+ i + "," + i + ")");
        }
        NakedDatabase.parseSQLStatementMaster(createA10);
        for(int i = 0; i < size; i++){
            NakedDatabase.parseSQLStatementMaster(loadA10.get(i));
        }

        String ajoina = "SELECT a, b FROM a10test join a10test WHERE(a = a)";
        NakedDatabase.parseSQLStatementMaster(ajoina);
        String resultTableajoina = "Table Name: QUERY_CREATED_TABLE6\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE6.table\n" +
                "id:6\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "A\tB\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "1\t1\t\n" +
                "2\t2\t\n" +
                "3\t3\t\n" +
                "4\t4\t\n" +
                "5\t5\t\n" +
                "6\t6\t\n" +
                "7\t7\t\n" +
                "8\t8\t\n" +
                "9\t9\t\n" +
                "10\t10\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(resultTableajoina, TableGolem.getTable(6).toString());



        String createA10_1 = "CREATE TABLE a10_1test (a int PRIMARY KEY, b int)";
        ArrayList<String> loadA10_1 = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            loadA10_1.add("INSERT INTO a10_1test VALUES ("+ i + "," + 1 + ")");
        }
        NakedDatabase.parseSQLStatementMaster(createA10_1);
        for(int i = 0; i < size; i++) {
            NakedDatabase.parseSQLStatementMaster(loadA10_1.get(i));
        }

        String a10_1load = "Table Name: a10_1test\n" +
                "key index:0\n" +
                "saved to:a10_1test.table\n" +
                "id:8\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "a\tb\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "1\t1\t\n" +
                "2\t1\t\n" +
                "3\t1\t\n" +
                "4\t1\t\n" +
                "5\t1\t\n" +
                "6\t1\t\n" +
                "7\t1\t\n" +
                "8\t1\t\n" +
                "9\t1\t\n" +
                "10\t1\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(a10_1load, TableGolem.getTable(8).toString());



        String bjoinb = "SELECT a, b FROM a10_1test join a10_1test WHERE(b = b)";
        NakedDatabase.parseSQLStatementMaster(bjoinb);
        String bjoinbresult = "Table Name: QUERY_CREATED_TABLE13\n" +
                "key index:0\n" +
                "saved to:QUERY_CREATED_TABLE13.table\n" +
                "id:13\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "A\tB\t\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "1\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "2\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "3\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "4\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "5\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "6\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "7\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "8\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "9\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "10\t1\t\n" +
                "----------------------------------------------------------------------------------------------------\n";
        assertEquals(bjoinbresult, TableGolem.getTable(13).toString());



        size = 1000;
        String createA1000 = "CREATE TABLE a1000test (a int PRIMARY KEY, b int)";
        ArrayList<String> loadA1000 = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            loadA1000.add("INSERT INTO a1000test VALUES ("+ i + "," + i + ")");
        }

        NakedDatabase.parseSQLStatementMaster(createA1000);
        for(int i = 0; i < size; i++) {
            NakedDatabase.parseSQLStatementMaster(loadA1000.get(i));
        }
        String ajoina1000 = "SELECT a, b FROM a1000test join a1000test WHERE(a = a)";
        NakedDatabase.parseSQLStatementMaster(ajoina1000);
        assertEquals(Test1000String.test1000Result, TableGolem.getTable(20).toString());



        String createA1000_1 = "CREATE TABLE a1000_1test (a int PRIMARY KEY, b int)";
        ArrayList<String> loadA1000_1 = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            loadA1000_1.add("INSERT INTO a1000_1test VALUES ("+ i + "," + 1 + ")");
        }
        NakedDatabase.parseSQLStatementMaster(createA1000_1);
        for(int i = 0; i < size; i++) {
            NakedDatabase.parseSQLStatementMaster(loadA1000_1.get(i));
        }
        String ajoina1000_1 = "SELECT a, b FROM a1000_1test join a1000_1test WHERE(a = a)";
        NakedDatabase.parseSQLStatementMaster(ajoina1000_1);
        assertEquals(Test1000_1String.Test1000_1_1000String, TableGolem.getTable(27).toString());



        String giantjoin = "SELECT a, b FROM a1000_1test join a1000_1test WHERE(b = b)";
        NakedDatabase.parseSQLStatementMaster(giantjoin);
        assertEquals(size*size, TableGolem.getTable(33).size());
    }
}