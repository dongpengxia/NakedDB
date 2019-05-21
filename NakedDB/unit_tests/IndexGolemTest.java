import coreOS.IndexGolem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.Assert.assertEquals;

public class IndexGolemTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void IndexGolemTests() {
        Set<String> attr1Blocks = new HashSet<>();
        Set<String> attr2Blocks = new HashSet<>();
        Set<String> temp = new HashSet<>();
        String indexFileName = "tmpIndexFile.idx";
        IndexGolem.openIndex(indexFileName);
        IndexGolem.insertIntoIndex("attrValue1", "0");
        attr1Blocks.add("0");
        IndexGolem.insertIntoIndex("attrValue2", "5");
        attr2Blocks.add("5");
        IndexGolem.insertIntoIndex("attrValue3", "2");
        IndexGolem.insertIntoIndex("attrValue2", "3");
        attr2Blocks.add("3");
        IndexGolem.insertIntoIndex("attrValue2", "4");
        attr2Blocks.add("4");
        IndexGolem.insertIntoIndex((long) 5, 4);
        IndexGolem.insertIntoIndex((long) 5, 6);
        temp.add("4");
        temp.add("6");
        assertEquals(IndexGolem.getKeys((long) 5), Optional.of(temp));
        assertEquals(IndexGolem.getKeys("attrValue2"), Optional.of(attr2Blocks));
        assertEquals(IndexGolem.getKeys("attrValue1"), Optional.of(attr1Blocks));
        IndexGolem.closeIndex(indexFileName);
        IndexGolem.openIndex(indexFileName);
        assertEquals(IndexGolem.getKeys("attrValue2"), Optional.of(attr2Blocks));
        assertEquals(IndexGolem.getKeys("attrValue1"), Optional.of(attr1Blocks));
        IndexGolem.insertIntoIndex("attrValue1", 1);
        attr1Blocks.add("1");
        assertEquals(IndexGolem.getKeys("attrValue2"), Optional.of(attr2Blocks));
        assertEquals(IndexGolem.getKeys("attrValue1"), Optional.of(attr1Blocks));
        IndexGolem.closeIndex(indexFileName);
        IndexGolem.openIndex(indexFileName);
        assertEquals(IndexGolem.getKeys("attrValue2"), Optional.of(attr2Blocks));
        assertEquals(IndexGolem.getKeys("attrValue1"), Optional.of(attr1Blocks));
        IndexGolem.closeIndex(indexFileName);
        IndexGolem.closeIndex(indexFileName); //empty the file
    }
}