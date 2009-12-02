package seven.g2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class WordSuccessorGenerator {
	
	public static void gen()
	{
		initDB();
		Statement s;
		
		try {
			Statement s2 = conn.createStatement();
			s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM letter_groups");
			while(rs.next())
			{
				String letters = rs.getString("letters");
				int counts[] = new int[26];
				for(int i = 0;i<26;i++)
					counts[i] = 0;
				for(int i =0;i<letters.length();i++)
					counts[letters.charAt(i) - 'A']++;
				String query = "update lettercollections SET ";
				for(int i=0;i<26;i++)
				{
					query += String.valueOf((char) (i + 'A')) + "=" + counts[i];
					if(i != 25)
						query += ",";
				}
				query += " where letters=\""+letters+"\";";

				s2.execute(query);
				s2.close();
			}
			
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		disconnect();
	}
	public static void disconnect()
	{
		if (conn != null)
        {
            try
            {
                conn.close ();
                System.out.println ("Database connection terminated");
            }
            catch (Exception e) { /* ignore close errors */ }
        }
	}
	static Connection conn = null;
	private static void initDB()
	{
		

        try
        {
            String url = "jdbc:sqlite:src/seven/g2/util/seven.db";
            Class.forName ("org.sqlite.JDBC").newInstance ();
            conn = DriverManager.getConnection (url);
            System.out.println ("Database connection established");
        }
        catch (Exception e)
        {
            System.err.println ("Cannot connect to database server");
        }

	}
	
	public static void main(String[] args) {
		gen();
	}
}
