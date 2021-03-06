package iot.unipi.it;

import java.sql.*;

public class DBControl {
	private Connection c = null;
	private PreparedStatement pStmt = null;
	private ResultSet rs = null;
	
	private String myTable;
	
	private String myTableAttributes[];
	
	public DBControl(String DB_URL, String username, String password, String myTable, String myTableAttributes[]) throws SQLException{
		try {
			this.myTable = myTable;
			this.myTableAttributes = myTableAttributes;
			this.c = DriverManager.getConnection(DB_URL, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addReading(String URI, Integer timestamp, Integer reading, boolean state)
			throws SQLException{
		try {
			String query = "insert into " + myTable + 
					       " (" + myTableAttributes[1] + ", " +
					       		  myTableAttributes[2] + ", " +
					       		  myTableAttributes[3] + ", " +
					       		  myTableAttributes[4] + ")  VALUES (?, ?, ?, ?)";
			
			pStmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			pStmt.setString (1, URI);
			pStmt.setInt (2, timestamp);
			pStmt.setInt (3, reading);
			pStmt.setBoolean(4, state);
			
			pStmt.execute();
			
			
			pStmt = null;
			rs = null;
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteReadingOfNode(String URI) throws SQLException{
		try {		
			String query = "delete from "+myTable+" where "+myTableAttributes[1]+" = ?"; 
			pStmt = c.prepareStatement(query);
			pStmt.setString(1, URI);
			pStmt.execute();
			pStmt = null;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
			
	}
	
	public boolean deleteSpecificReadingOfNode(String URI, Integer timestamp) {
		try {		
			String query = "delete from "+myTable+" where "+myTableAttributes[1]+ " = ? and "
					+ myTableAttributes[2] + " = ?"; 
			pStmt = c.prepareStatement(query);
			pStmt.setString(1, URI);
			pStmt.execute();
			pStmt = null;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void getReadingOfNode(String URI) throws SQLException{
		try {
			String query = "select * from " + myTable + " where " + myTableAttributes[1] + " = ?";
			pStmt = c.prepareStatement(query);			
			
			pStmt.setString (1, URI);
			
			rs = pStmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("Node: "+rs.getString(myTableAttributes[1])+
									"\ttime: "+rs.getInt(myTableAttributes[2])+
									"\tvalue: "+rs.getInt(myTableAttributes[3])+
									" celsius\talarm on? "+rs.getBoolean(myTableAttributes[4]));			
			}
			
			pStmt = null;
			rs = null;
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getSpecificReadingOfNode(String URI, Integer timestamp) {
		try {
			String query = "select * from " + myTable + " where " + myTableAttributes[1] + " = ? and "
					+ myTableAttributes[2] + " = ?";
			
			pStmt = c.prepareStatement(query);			
			
			pStmt.setString (1, URI);
			pStmt.setInt(2, timestamp);
			
			rs = pStmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("Node: "+rs.getString(myTableAttributes[1])+
									"\ttime: "+rs.getInt(myTableAttributes[2])+
									"\tvalue: "+rs.getInt(myTableAttributes[3])+
									" celsius\talarm on? "+rs.getBoolean(myTableAttributes[4]));			
			}
			
			pStmt = null;
			rs = null;
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
