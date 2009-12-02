package seven.g2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import seven.ui.Scrabble;

public class AddScrabbleScoresToWords {
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
		initDB();
		try {
			Statement s2 = conn.createStatement();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM words where score=7");
			Scrabble scrab = new Scrabble();
			while(rs.next())
			{
				String word = rs.getString("word");
				int score = scrab.getWordScore(word);
				System.out.println(word);
				System.out.println(score);
				s2.execute("UPDATE words set score=" + score + " where word='"+word+"'");
				s2.close();
			}
			
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		disconnect();
	}
}
