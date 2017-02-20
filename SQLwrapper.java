/**
 * Created by Yuting on 2/19/2017.
 */

import java.util.ArrayList;

public class SQLwrapper {

    public static final String PUBLIC_SCHEMA = "public";
    public static final String PRIVATE_SCHEMA = "team01schema";

    public static String checkTable(String tableName){
//		String query = "SELECT COUNT(*) FROM " + tableName;
//
//        query += tableName;
//        query += "'";

        return "SELECT COUNT(*) FROM " + tableName;
    }

    public static String checkAttribute(String tableName, String attrName){
        return "SELECT COUNT(" + attrName + ") FROM " + tableName;
    }

    public static String checkAttrNull(String tableName, String attrName){
        return "SELECT COUNT(*) FROM " + tableName + " \n\tWHERE " + attrName + " IS NULL";
    }

    public static String checkAttrDuplicate(String tableName, ArrayList<String> attrs){
        String columns = String.join(",", attrs);
        return "SELECT " + columns + " COUNT(*) FROM " + tableName + " \n\tGROUP BY " + columns + " \n\tHAVING COUNT(*) > 1";
    }

    public static String checkDependency(String tableName, ArrayList<String> X, String Y){
        String sql = "SELECT COUNT(*) FROM " + tableName + " r1, " + tableName + " r2 \n\tWHERE ";
        for (String attr : X) {
            sql += "r1." + attr + "=r2." + attr + " AND ";
        }

        sql += "r1." + Y + " <> r2." + Y;

        return sql;
    }

    public static String dropTable(String tableName){
        return "DROP TABLE " + tableName;
    }

    public static String decompositeTable(String tableName, String newTableName, ArrayList<String> keys, ArrayList<String> attrs){
        String sql = "SELECT DISTINCT " + String.join(",", keys);
        if(attrs.size() > 0)
            sql += "," + String.join(",", attrs);
        return sql + "\n\tINTO " + newTableName + " FROM " + tableName;
    }

    public static String countDistinct(String tableName, ArrayList<String> attrs){
        String sql = "SELECT COUNT(*) FROM (\n\tSELECT DISTINCT ";
        if(attrs.size() > 0)
            sql += String.join(",", attrs);
        else
            sql += "*";
        sql += " FROM " + tableName + ")";

        return sql;
    }
}
