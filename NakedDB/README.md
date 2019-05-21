Try these commands on command line:
source demoStartingScript.sql;
SELECT aaHundred.a, aaThousand.f FROM aaHundred JOIN aaThousand WHERE a = f;
SELECT a1Hundred.d, aaHundred.a FROM aaHundred JOIN a1Hundred WHERE a = d;
SELECT aaHundred.a, aaHundred.b FROM aaHundred JOIN aaHundred WHERE a = a;
SELECT a1hundred.c, a1hundred.d FROM a1hundred JOIN a1hundred WHERE c = c;
SELECT a1hundred.c, a1hundred.d FROM a1hundred JOIN a1hundred WHERE d = d;
SELECT aathousand.e, a1thousand.g FROM aathousand JOIN a1thousand WHERE e = g;
SELECT a1thousand.g, a1thousand.h FROM a1thousand JOIN a1thousand WHERE h = h;
CREATE TABLE table1 VALUES(key int PRIMARY KEY, v1 int, v2 int, v3 double NOT NULL, v4 char NULL);
INSERT INTO table1 VALUES(1, 2, 3, 4, 5);
INSERT INTO table1 VALUES(6, 7, 8, 9, 10);
INSERT INTO table1 VALUES(11, 12, 13, 14, potato);



Incomplete Features:
    1) Support for update statements (update = delete + insert)
    2) Using the indexes in select statements (dump index, create index, and drop index are finished)
    3) Select * parsing
    4) GroupBy, OrderBy, Aggregate operators, AS keyword
    5) Merge Join Optimization
    6) Clear results table option to clear intermediate tables <- partially implemented
    7) Foreign key support



Complete Features:
    1) Test main function to run existing commands.
    2) Check that tables are saved upon exit and can be reloaded.
    3) "SOURCE <filename>" to execute SQL from a file
    4) "COMMIT" to write tables to files
    5) "CLEAN" to drop query created tables
    6) Create starting tables in demoStartingScript.sql for demonstration
    7) Functional tests demonstrating create table, drop table, insert, select (w/ nested selects), join (w/ multiple), and/or/>/</= (w/ multiple)
    8) Functional tests demonstrating delete, create index, drop index
    9) Tests in DemonstrationTest demonstrating self joins
    10) Rule based optimization to push selects down query tree
    11) Dump, Dump Files, and Dump Index to print tables, names of table files, and names of index files
    12) Increase JVM memory