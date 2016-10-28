package edu.ucar.dls.harvest.scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeComparisonReport {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement gradeBandPStmt = null;
		PreparedStatement benchmarkPStmt = null;
		ResultSet benchmarkRS = null;
		ResultSet gradeBandRS = null;
		
		try
		{
			conn = getConnection();
			
			String errorFormat = "DB Error Benchmark record %s(%s) says grades are %s while grade band %s says %s";
			
			String gradeBandQuery = "select object_ID, primary_grade from objects, relation where relation.ItemID1=? and objects.object_ID=relation.ItemID2 and lower(object_type)='grade group' and primary_grade!=?";
	
			String benchmarkQuery = "select object_ID, primary_grade, full_text from objects where lower(object_type)='benchmark' ";
			
			gradeBandPStmt = conn.prepareStatement(
					gradeBandQuery);
			benchmarkPStmt = conn.prepareStatement(
					benchmarkQuery);
			benchmarkRS = benchmarkPStmt.executeQuery();
			
			while(benchmarkRS.next())
			{
				String benchmarkID = benchmarkRS.getString(1);
				String benchmarkGrade = benchmarkRS.getString(2);
				String benchmarkText = benchmarkRS.getString(3);
				
				gradeBandPStmt.setString(1, benchmarkID);
				gradeBandPStmt.setString(2, benchmarkGrade);
				gradeBandRS = gradeBandPStmt.executeQuery();

				while(gradeBandRS.next())
				{
					String gradebandObjectId = gradeBandRS.getString(1);
					String gradebandGrades = gradeBandRS.getString(2);
					
					System.out.println(String.format(errorFormat, benchmarkID, benchmarkText, benchmarkGrade, gradebandObjectId, gradebandGrades));
				}
				gradeBandRS.close();
				
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(benchmarkRS!=null)
				try{benchmarkRS.close();}catch(Exception e){}
			if(gradeBandRS!=null)
				try{gradeBandRS.close();}catch(Exception e){}
			if(gradeBandPStmt!=null)
				try{gradeBandPStmt.close();}catch(Exception e){}
			if(benchmarkPStmt!=null)
				try{benchmarkPStmt.close();}catch(Exception e){}
			if(conn!=null)
				try{conn.close();}catch(Exception e){}
		}
		
		

	}

	private static Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smsServer", 
						"root",
						"aX87yDe48d#@");
		return connection;
	}

}
