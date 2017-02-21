/**
 * Created by Yuting on 2/19/2017.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class NF3 extends NF2{
    private static final  int MAX_ATTRIBUTES = 17;

    @Override
    public boolean verify(Properties props, DBwrapper db, boolean decomp){
        boolean success = super.verify(props, db, decomp);
        if(!success)
            return success;

        String tableName = props.getProperty("tableName");
        ArrayList<String> nonKeyAttrs = (ArrayList<String>)props.get("nonKeyAttrs");

        ArrayList<String> reasons = new ArrayList<String>();
        dependencyProps = new ArrayList<Properties>();

        int numAttrs = nonKeyAttrs.size();

        if(numAttrs > 1){
            try {
                for (String attr : nonKeyAttrs) {
                    int i = 1;
                    while (i <= MAX_ATTRIBUTES && i < numAttrs) {
                        ArrayList<ArrayList<String>> combo = getCombinations(nonKeyAttrs, i);
                        for (ArrayList<String> subAttrs : combo) {
                            if (subAttrs.contains(attr))
                                continue;

                            String sqlDepend = SQLwrapper.checkDependency(tableName, subAttrs, attr);

                            int count = db.queryCount(sqlDepend);
                            if (count == 0) {
                                success = false;
                                String X = String.join(",", subAttrs);
                                reasons.add(X + "->" + attr);

                                // record dependency property
                                Properties dp = new Properties();
                                dp.put("X", subAttrs);
                                dp.put("Y", new ArrayList<String>(Arrays.asList(attr)));
                                dependencyProps.add(dp);
                            }
                        }

                        i++;
                    }
                }
            }
            catch (Exception e){}
        }

        if(!success) {
            String error = String.join(",", reasons);
            IOutils.outputNF(tableName + "\t\tN\teNF\t\t" + error);

            if(decomp)
                super.decomposite(props, db);
        }
        else{
            IOutils.outputNF(tableName + "\t\tY\t\t\t");
        }

        return success;
    }
}
