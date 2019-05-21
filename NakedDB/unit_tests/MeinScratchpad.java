public class MeinScratchpad {

    public static void main (String[] args) {

        int numItems = 100;
        String create_aaHundred = "CREATE TABLE aaHundred (a int PRIMARY KEY, b int);";
        System.out.println(create_aaHundred);
        for(int i = 1; i <= numItems; i++){
            String s = "INSERT INTO aaHundred VALUES ("+ i + "," + i + ");";
            System.out.println(s);
        }

        String ajoina = "SELECT a, b FROM aaHundred join aaHundred WHERE(a = a);";
//        System.out.println(ajoina);





        String create_a1Hundred = "CREATE TABLE a1Hundred (c int PRIMARY KEY, d int);";
        System.out.println(create_a1Hundred);
        for(int i = 1; i <= numItems; i++) {
            String s = "INSERT INTO a1Hundred VALUES ("+ i + "," + 1 + ");";
            System.out.println(s);
        }

        String bjoinb = "SELECT c, d FROM a1Hundred join a1Hundred WHERE(d = d);";
//        System.out.println(bjoinb);





        numItems = 1000;
        String create_aaThousand = "CREATE TABLE aaThousand (e int PRIMARY KEY, f int);";
        System.out.println(create_aaThousand);
        for(int i = 1; i <= numItems; i++) {
            String s = "INSERT INTO aaThousand VALUES ("+ i + "," + i + ");";
            System.out.println(s);
        }

        String ajoina1000 = "SELECT e, f FROM aaThousand join aaThousand WHERE(e = e);";
//        System.out.println(ajoina1000);





        String create_a1Thousand = "CREATE TABLE a1Thousand (g int PRIMARY KEY, h int);";
        System.out.println(create_a1Thousand);
        for(int i = 1; i <= numItems; i++) {
            String s = "INSERT INTO a1Thousand VALUES ("+ i + "," + 1 + ");";
            System.out.println(s);
        }

        String ajoina1000_1 = "SELECT g, h FROM a1Thousand join a1Thousand WHERE(g = g);";
//        System.out.println(ajoina1000_1);

        String giantmerge = "SELECT g, h FROM a1Thousand join a1Thousand WHERE(h = h);";
//        System.out.println(giantmerge);
    }
}