
import java.sql.*;
import java.util.Properties;
import java.util.ArrayList;

public class DBwrapper {

    private String serverName;
    private String userName;
    private String password;

    public DBwrapper(String server){
        serverName = server;
    }

    public void setCredential(String user, String pwd){
        userName = user;
        password = pwd;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.vertica.jdbc.Driver");
            Properties credentials = new Properties();
            credentials.put("user", userName);
            credentials.put("password", password);
            con = DriverManager.getConnection("jdbc:vertica://"+serverName, credentials);
        } catch (ClassNotFoundException ex) {
            System.out.println("Cannot find the JDBC Driver");
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Cannot connect to the database");
            ex.printStackTrace();
        }
        return con;
    }

    public boolean verityTable(Properties tableProp){
        String sqlCheckTable = SQLwrapper.checkTable(tableProp.getProperty("tableName"));

        try{
            int count = queryCount(sqlCheckTable, false);
            if(count <= 0){
                IOutils.outputNF(tableProp.getProperty("tableName") + "\t\tN\t1NF\t\tTable not exist");
                return false;
            }

            ArrayList<String> attributes = (ArrayList<String>)tableProp.get("allAttrs");
            for(String attr : attributes){
                String sqlChechAttr = SQLwrapper.checkAttribute(tableProp.getProperty("tableName"), attr);
                count = queryCount(sqlChechAttr, false);
                if(count <= 0){
                    IOutils.outputNF(tableProp.getProperty("tableName") + "\t\tN\t1NF\t\tAttribute " + attr + " not exist");
                    return false;
                }
            }
            return true;
        }
        catch(Exception e){
//			e.printStackTrace();
            return false;
        }
    }

    public int queryCount(String query) throws SQLException{
        return queryCount(query, true);
    }

    public int queryCount(String query, boolean writeToFile) throws SQLException{
        if(writeToFile)
            IOutils.outputSQL(query);

        int count = 0;
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            count = rs.getInt("COUNT");
        } catch (SQLException sqlEx) {
//            sqlEx.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
                con.close();
            }
        }

        return count;
    }

    public void executeUpdate(String query) throws SQLException{
        IOutils.outputSQL(query);
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlEx) {
//            sqlEx.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
                con.close();
            }
        }
    }

    public void dropTable(String tableName)throws SQLException{
        String sqlCheckTable = SQLwrapper.checkTable(tableName);
        int count = queryCount(sqlCheckTable, false);
        if(count > 0){
            String sqlDropTable = SQLwrapper.dropTable(tableName);
            executeUpdate(sqlDropTable);
        }
    }


    public void jointTables(String originalTableName, String newTableName, ArrayList<Properties> newProps) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + newTableName;
        String tableNames = ",";

        for (Properties props : newProps){
            String subTableName = props.getProperty("tableName");
            sql += " \n\tJOIN " + subTableName + " ON ";
            tableNames += subTableName + ",";
            ArrayList<String> attrs = (ArrayList<String>) props.get("keyAttrs");
            for(String attr : attrs){
                String temp =  newTableName +"."+attr+" = "+subTableName+"."+attr + " AND ";
                sql += temp;
            }

            sql = sql.substring(0,sql.length() - 5);

        }

        int jointCount = queryCount(sql);

        //String sqlOriginalCount = SQLwrapper.countDistinct(originalTableName, new ArrayList<String>());
        String sqlOriginalCount = SQLwrapper.checkTable(originalTableName);
        int originalCount = queryCount(sqlOriginalCount, false);

        IOutils.outputDecomposition("#Verification:");
        String output = originalTableName + "=join(" + newTableName;
        for (Properties props : newProps){
            output += "," + props.getProperty("tableName");
        }
        output += ")? ";

        if(jointCount == originalCount){
            output += "YES";
        }else{
            output += "NO";
        }
        IOutils.outputDecomposition(output);
        IOutils.outputDecomposition("");
    }
}
