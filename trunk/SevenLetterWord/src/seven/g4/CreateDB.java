package seven.g4;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;




public class CreateDB 
{
	private static final String SQL_CREATE_SEVEN_DB = "CREATE DATABASE seven";
	private static final String SQL_CREATE_SEVEN_TABLE = "CREATE TABLE seven(KEYSTRING varchar(7), word varchar(7), suffix varchar(7))";
	private static final String SQL_CREATE_SEVEN_INDEX = "CREATE INDEX key_index ON seven (keystring) ";
	
	private static final String SQL_DROP_TABLE = "DROP TABLE seven";
	private static final String SQL_DROP_DB = "DROP DATABASE seven";
	private static final String SQL_INSERT_SEVEN_TABEL = "insert into seven values(?,?,?)";
	
	private PreparedStatement PS_INSERT_SEVEN = null;
	private Statement st = null;

	private HashSet<String> prefixes = new HashSet<String>();

	
	private Connection m_connection = null;

	
	
	CreateDB() {
		prepareDB();
	}
	
	private boolean prepareDB()	{
		System.out.println("Prepare db");
		try {
			if (m_connection == null || m_connection.isClosed()) {
				try {
					Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				m_connection = DriverManager
						.getConnection("jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ="
								+ "src/seven/g4/seven.mdb");

				m_connection.setReadOnly(false);
			}

			PS_INSERT_SEVEN = m_connection.prepareStatement(SQL_INSERT_SEVEN_TABEL);
			
			st = m_connection.createStatement();
			
			st.execute(SQL_DROP_TABLE);
			st.execute(SQL_CREATE_SEVEN_TABLE);
			st.execute(SQL_CREATE_SEVEN_INDEX);
			
			System.out.println("Finished creating table and index");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


return true;
		
	}
	
	
	private boolean insert()
	{
		int n = 0;
		try {
			Scanner s = new Scanner(new File("src/seven/g4/7letterWords.txt"));
			int i = 0;
			while(s.hasNextLine())
			{
				String word = s.nextLine();
				n++;
				if(n%100 == 0)
					System.out.println("----->deal with "+word);
				addSubSet(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
	private void addSubSet(String word) {
		String sorted = sortedString(word);
		//if(!prefixes.contains(sorted))
			for(String p : getAllSubSet(word))
			{
				//System.out.println(p);
				//System.out.println(p.length());
				ArrayList<Character> suffix = new ArrayList<Character> ();
				for(int i = 0 ; i < word.length(); i++)
					suffix.add(word.charAt(i));
				for(int i = 0 ; i < p.length(); i++)
					suffix.remove(suffix.indexOf(p.charAt(i)));
				String suff = "";
				for(int i = 0 ; i < suffix.size(); i++)
					suff += suffix.get(i);
				
				//System.out.println("In word: "+word+" , the suffix for "+p+" is "+suff);
				
				try {
					
					PS_INSERT_SEVEN.setString(1, p);
					PS_INSERT_SEVEN.setString(2, word);
					PS_INSERT_SEVEN.setString(3, suff);
					PS_INSERT_SEVEN.execute();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
	}
	
	
	
	public static HashSet<String>  getAllSubSet(String s)
	{
		char[] arr = s.toCharArray();
		boolean[] flag = new boolean[arr.length];
		for(int i = 0; i < flag.length; i++)
			flag[i] = true;
		
		HashSet<String> ret = new HashSet<String>();
		getSubSet(arr, flag, ret);
		return ret;
	}
	
	
	
	public static void getSubSet( char[] arr, boolean[] flag, HashSet<String> ret)
	{		

		String s = "";
		for(int i = 0; i < arr.length; i++)
			if (flag[i])
				s += arr[i];
		if(!ret.contains(s) && !s.equals(""))
		{
			ret.add(s);
		}
		
		for(int i = 0; i < arr.length; i++)
		{
			if(flag[i] == true)
			{
				flag[i] = false;
				getSubSet(arr, flag, ret);
				flag[i] = true;
			}
		}	
	}		
			
	
	
	
	private static String sortedString(String word)
	{
		ArrayList<String> letters = new ArrayList<String>();		
		for (int i = 0; i < word.length(); i++) {
			letters.add(word.substring(i, i + 1));
		}
		Collections.sort(letters);
		String sorted = "";
		for (int j = 0; j < word.length(); j++) {
			sorted += letters.get(j);
		}
		return sorted;
	}	
	
	
	
	public static void main(String[] args)
	{
		/*String s = "ABC";
		//HashSet<String> set = new HashSet<String>();
		HashSet<String> set = getAllSubSet(s);
		
		for(String st: set)
			System.out.println(st);
		System.out.println(set.size());
		*/
		
		new CreateDB().insert();
		
	}
	
}