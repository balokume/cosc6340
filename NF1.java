/**
 * Created by Yuting on 2/19/2017.
 */

import java.util.Properties;
import java.util.ArrayList;

public class NF1 implements NormalForm{

    public boolean verify(Properties props, DBwrapper db, boolean decomp){
        String tableName = props.getProperty("tableName");
        ArrayList<String> keyAttrs = (ArrayList<String>)props.get("keyAttrs");

        boolean success = true;
        ArrayList<String> reasons = new ArrayList<String>();

        try{
            for(String key : keyAttrs){
                String sqlNull = SQLwrapper.checkAttrNull(tableName, key);
                int count = db.queryCount(sqlNull);
                if(count > 0){
                    success = false;
                    reasons.add(key + " has Null value");
                }

            }

            String sqlDuplicate = SQLwrapper.checkAttrDuplicate(tableName, keyAttrs);
            int count = db.queryCount(sqlDuplicate);
            if(count > 0){
                success = false;
                reasons.add("kyes have duplicate values");
            }
        }
        catch(Exception e){}

        if(!success) {
            String error = String.join(",", reasons);
            IOutils.outputNF(tableName + "\t\tN\t1NF\t\t" + error);
        }

        System.out.println("1nf");

        return success;
    }

    public void decomposite(Properties props, DBwrapper db){

    }
}