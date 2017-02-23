
import java.util.ArrayList;
import java.util.Properties;


public class main {

    public static void main(String[] argv){

        //  check paramter
        if(argv.length < 1) {
            System.out.println("Please provide table name in argument.");
            return;
        }

        boolean decomp = true;
//        if(argv.length >=3){
//            if(argv[2].equals("0"))
//                decomp = false;
//        }

        // parse input file
        String inputFile = argv[0];
        int idx = inputFile.indexOf("database=");
        if(idx >= 0)
            inputFile = inputFile.substring(idx + 9);
//        System.out.println(inputFile);
        ArrayList<Properties> tableProps = IOutils.parseInput(inputFile);

        // setup database connection
        DBwrapper db = new DBwrapper("129.7.243.243/cosc6340s17");
        db.setCredential("team01", "2DvtjSks");

        //create ouput files
        IOutils.initOutput();
        IOutils.outputNF("#Table\t\tBCNF\tFailed\tReason");

        // check each table
        for(Properties prop: tableProps)
        {
            // check table validation
            boolean tableExists = db.verityTable(prop);
            if(!tableExists){

                continue;
            }


            // check Normal Form
//            NF3 nf = new NF3();
            BcNF nf = new BcNF();
            nf.verify(prop, db, decomp);

//            IOutils.outputNF(prop.getProperty("tableName") + "\t\tY\t\t\t");
        }
    }
}
