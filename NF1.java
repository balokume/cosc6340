
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
        catch(Exception e){e.printStackTrace();}

        if(!success) {
//            String error = String.join(",", reasons);
            String error = StrUtils.join(reasons);
            IOutils.outputNF(tableName + "\t\t\tN\t\t1NF\t\t" + error);
        }

        return success;
    }

    public void decomposite(Properties props, DBwrapper db){

    }
}