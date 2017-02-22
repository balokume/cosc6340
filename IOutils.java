
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class IOutils {

    public static ArrayList<Properties> parseInput(String filePath){
        ArrayList<Properties> tableProps = new ArrayList<Properties>();

        try{
            File file = new File(filePath);
            String line;
            if (file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                while((line = bufferedReader.readLine()) != null ) {
                    //System.out.println("The Next line is : " + nextLine);
                    Properties prop = parseInputTable(line);
                    tableProps.add(prop);
                }

                read.close();
            }
            else
            {
                System.out.println("Input file is not found");
            }
        }
        catch(Exception ex)
        {
            System.out.println("error trying to read the file");
            ex.printStackTrace();
        }
        return tableProps;
    }

    public static Properties parseInputTable(String tableSchema)
    {
        Properties tableProps = new Properties();

        ArrayList<String> keyAttrs = new ArrayList<String>();
        ArrayList<String> allAttrs = new ArrayList<String>();
        ArrayList<String> nonKeyAttrs = new ArrayList<String>();

        int idx = tableSchema.indexOf("(");
        tableProps.put("tableName", SQLwrapper.PUBLIC_SCHEMA + tableSchema.substring(0,idx));
        String attributes = tableSchema.substring(idx+1,tableSchema.length() - 1);


        String[] attrArray = attributes.split(",");
        for(String attr : attrArray){
            idx = attr.indexOf("(k)");
            if(idx != -1){
                String key = attr.substring(0, idx);
                keyAttrs.add(key);
                allAttrs.add(key);
            }
            else{
                nonKeyAttrs.add(attr);
                allAttrs.add(attr);
            }
        }

        tableProps.put("keyAttrs", keyAttrs);
        tableProps.put("allAttrs", allAttrs);
        tableProps.put("nonKeyAttrs", nonKeyAttrs);

        return tableProps;
    }

    public static void initOutput(){
        try{
            File file = new File("NF.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            file = new File("Decomposition.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            file = new File("NF.sql");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void outputNF(String content){
        try {
            File file = new File("NF.txt");
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(content + "\n");
            bufferWriter.close();
            //System.out.println("Write File Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void outputDecomposition(String content){
        try {
            File file = new File("Decomposition.txt");
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(content + "\n");
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void outputSQL(String content){
        try {
            File file = new File("NF.sql");
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(content + "\n\n");
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
