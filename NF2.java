
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class NF2 extends NF1{
    private static final  int MAX_KEYS = 4;

    protected ArrayList<Properties> dependencyProps;

    @Override
    public boolean verify(Properties props, DBwrapper db, boolean decomp){
        boolean success = super.verify(props, db, decomp);
        if(!success)
            return success;

        String tableName = props.getProperty("tableName");
        ArrayList<String> keyAttrs = (ArrayList<String>)props.get("keyAttrs");
        ArrayList<String> nonKeyAttrs = (ArrayList<String>)props.get("nonKeyAttrs");

        ArrayList<String> reasons = new ArrayList<String>();
        dependencyProps = new ArrayList<Properties>();

        int numKeys = keyAttrs.size();
        if(numKeys > 1){
            try {
                for (String attr : nonKeyAttrs) {
                    int i = 1;
                    while (i <= MAX_KEYS && i < keyAttrs.size()) {
                        ArrayList<ArrayList<String>> combo = getCombinations(keyAttrs, i);
                        for (ArrayList<String> subKeys : combo) {
                            String sqlDepend = SQLwrapper.checkDependency(tableName, subKeys, attr);

                            int count = db.queryCount(sqlDepend);
                            if (count == 0) {
                                success = false;
                                //String X = String.join(",", subKeys);
                                String X = StrUtils.join(subKeys);
                                reasons.add(X + "->" + attr);

                                // chech if a dependency already exist with the same key
                                boolean depKeyExist = false;
                                for(Properties dp : dependencyProps){
                                    if(((ArrayList<String>)dp.get("X")).equals(subKeys)){
                                        ((ArrayList<String>)dp.get("Y")).add(attr);
                                        depKeyExist = true;
                                        break;
                                    }
                                }

                                if(!depKeyExist) {
                                    // record dependency property
                                    Properties dp = new Properties();
                                    dp.put("X", subKeys);
                                    dp.put("Y", new ArrayList<String>(Arrays.asList(attr)));
                                    dependencyProps.add(dp);
                                }
                            }
                        }

                        i++;
                    }
                }
            }
            catch (Exception e){e.printStackTrace();}
        }
        if(!success) {
//            String error = String.join(",", reasons);
            String error = StrUtils.join(reasons);
            IOutils.outputNF(tableName + "\t\t\tN\t\t2NF\t\t" + error);

            if(decomp)
                decomposite(props, db);
        }

        return success;
    }

    @Override
    public void decomposite(Properties props, DBwrapper db){
        String tableName = props.getProperty("tableName");
        ArrayList<String> keyAttrs = (ArrayList<String>)props.get("keyAttrs");
        ArrayList<String> nonKeyAttrs = (ArrayList<String>)props.get("nonKeyAttrs");

        int newTableIdx = 2;
        ArrayList<Properties> newTableProps = new ArrayList<Properties>();
        Properties remainingTable = (Properties) props.clone();

        IOutils.outputDecomposition("#" + tableName + " decomposition:");

        try {
            for (Properties dp : dependencyProps) {
                Properties newDP = new Properties();
                ArrayList<String> X = (ArrayList<String>) dp.get("X");
                ArrayList<String> Y = (ArrayList<String>) dp.get("Y");

                String newTableName = SQLwrapper.PRIVATE_SCHEMA + tableName + "_" + newTableIdx;
                db.dropTable(newTableName);
                String sqlCreateTable = SQLwrapper.decompositeTable(tableName, newTableName, X, Y);
                db.executeUpdate(sqlCreateTable);

                newDP.put("tableName", newTableName);
                newDP.put("keyAttrs", X);
                newDP.put("nonKeyAttrs", Y);
                newTableProps.add(newDP);

                removeAttrsFromTable(remainingTable, Y);

                newTableIdx++;
            }

            // remaining table
            String remainTableName = SQLwrapper.PRIVATE_SCHEMA + tableName + "_1";
            ArrayList<String> remainAttrs = (ArrayList<String>)remainingTable.get("allAttrs");
            db.dropTable(remainTableName);
            String sqlCreateRemainTable = SQLwrapper.decompositeTable(tableName, remainTableName, remainAttrs, new ArrayList<String>());
            db.executeUpdate(sqlCreateRemainTable);

            // output decomposition
            //IOutils.outputDecomposition(remainTableName + "(" + String.join(",", remainAttrs) + ")");
            IOutils.outputDecomposition(remainTableName + "(" + StrUtils.join(remainAttrs) + ")");
            for(Properties dp : newTableProps)
//                IOutils.outputDecomposition(dp.getProperty("tableName") + "(" + String.join(",", (ArrayList<String>)dp.get("keyAttrs"))
//                        + "," + String.join(",", (ArrayList<String>)dp.get("nonKeyAttrs")) + ")");
                IOutils.outputDecomposition(dp.getProperty("tableName") + "(" + StrUtils.join((ArrayList<String>)dp.get("keyAttrs"))
                        + "," + StrUtils.join((ArrayList<String>)dp.get("nonKeyAttrs")) + ")");

            // joint tables
            db.jointTables(tableName, remainTableName, newTableProps);
        }
        catch (Exception e){}


    }

    protected ArrayList<ArrayList<String>> getCombinations(ArrayList<String> attrs, int count){
        if(count > attrs.size())
            return null;

        ArrayList<ArrayList<String>> subsets = new ArrayList<>();
        int[] s = new int[count];                  // here we'll keep indices
        // pointing to elements in input array

        if (count <= attrs.size()) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < count - 1; i++);
            subsets.add(getSubset(attrs, s));
            for(;;) {
                int i;
                // find position of item that can be incremented
                for (i = count - 1; i >= 0 && s[i] == attrs.size() - count + i; i--);
                if (i < 0) {
                    break;
                } else {
                    s[i]++;                    // increment this item
                    for (++i; i < count; i++) {    // fill up remaining items
                        s[i] = s[i - 1] + 1;
                    }
                    subsets.add(getSubset(attrs, s));
                }
            }
        }

        return subsets;
    }

    // generate actual subset by index sequence
    ArrayList<String> getSubset(ArrayList<String> input, int[] subset) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < subset.length; i++)
            result.add(input.get(subset[i]));
        return result;
    }

    private void removeAttrsFromTable(Properties props, ArrayList<String> attrs){
        ArrayList<String> keyAttrs = (ArrayList<String>)props.get("keyAttrs");
        ArrayList<String> nonKeyAttrs = (ArrayList<String>)props.get("nonKeyAttrs");
        ArrayList<String> allAttrs = (ArrayList<String>)props.get("allAttrs");

        for(String attr : attrs){
            keyAttrs.remove(attr);
            nonKeyAttrs.remove(attr);
            allAttrs.remove(attr);
        }
    }
}
