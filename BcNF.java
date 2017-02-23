
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class BcNF extends NF3{


    @Override
    public boolean verify(Properties props, DBwrapper db, boolean decomp){
    	// System.out.println("we are here");
        boolean success = super.verify(props, db, decomp);
        if(!success)
            return success;

        String tableName = props.getProperty("tableName");
        ArrayList<String> nonKeyAttrs = (ArrayList<String>)props.get("nonKeyAttrs");
        ArrayList<String> keyAttrs = (ArrayList<String>)props.get("keyAttrs");

        ArrayList<String> reasons = new ArrayList<String>();
        dependencyProps = new ArrayList<Properties>();

        int numAttrs = nonKeyAttrs.size();
        int numKeys = keyAttrs.size();
        
        try{
        	int j=1;

	        while(j <= numAttrs){

	        	ArrayList<ArrayList<String>> combo = getCombinations(nonKeyAttrs, j);

	        	for(ArrayList<String> attrCombo : combo){

	        		int i = 1;
	        	    while(i <= numKeys){

	                    ArrayList<ArrayList<String>> keyCombo = getCombinations(keyAttrs, i);

	                    for(ArrayList<String> comboKey : keyCombo){

	                    	String sqlDepend = SQLwrapper.checkDependencyBcnf(tableName, attrCombo, comboKey);
	                        // System.out.println(sqlDepend);
	                        int count = db.queryCount(sqlDepend);

                            if (count == 0) {
                                success = false;
//                                String X = String.join(",", attrCombo);
//                                String Y = String.join(",", comboKey);
                                String X = StrUtils.join(attrCombo);
                                String Y = StrUtils.join(comboKey);
                                reasons.add(X + "->" + Y);
                            }
                        }
	                    i++;                
	        	    }
	        	}	        	
	        	j++;
	        }

        } catch(Exception e){
        	e.printStackTrace();
        }

        if(!success) {
            //String error = String.join(",", reasons);
            String error = StrUtils.join(reasons);
            IOutils.outputNF(tableName + "\t\t\tN\t\tBCNF\t\t" + error);
            // System.out.println("Not in BCNF because of dependency on: " + error);            
        }
        else{
            IOutils.outputNF(tableName + "\t\tY\t\t\t");
            // System.out.println("In BCNF");
        }     

       

        return success;
    }

} 