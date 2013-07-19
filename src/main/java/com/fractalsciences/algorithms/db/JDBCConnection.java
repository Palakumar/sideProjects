package com.fractalsciences.algorithms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fractalsciences.socialmedia.handlers.TwitterData;

public class JDBCConnection {

	private static String url = "jdbc:mysql://localhost:3306/fractalsciences";
	private static String user = "root";
	private static String password = "fractal";
	private static Connection connexion;

	public static void main(String[] args) throws SQLException{

		String test1 = "test 'ho'";
		String test2 = "test 'ho'";
		String test3 = "test 'ho'";
		test1 = test1.replaceAll("'", "\\'");
		test2 = test2.replace("'", "\\\'");
		test3 = test3.replace("'", "\\\\'");
		
		JDBCConnection conn = new JDBCConnection();
		//conn.testConnexion();
		conn.getConnection();
		TwitterData tweet = new TwitterData("Palak", "test 'ho'", null, "test's", null);
		conn.saveTweets(tweet);
		List<TwitterData> twitterData = conn.retrieveTweets("test's");
		for(TwitterData tweeti : twitterData){
			System.out.println(tweet.toString());
		}
		conn.closeConnection();
	}


	/**
	 * Open JDBC Connection Pool
	 * @return
	 */
	public void getConnection(){
		Connection connexion = null;
		try {
			Class.forName( "com.mysql.jdbc.Driver" );
		} catch ( ClassNotFoundException e ) {
			System.out.println( "Error while loading JDBC driver!"
					+ e.getMessage() );
		}
		try {
			connexion = DriverManager.getConnection( url, user, password);
			System.out.println("Got connection!");
			this.connexion = connexion;
		} catch ( SQLException e ) {
			System.out.println( "Error while connecting: "
					+ e.getMessage() );
		} 
	}


	/**
	 * Close connection
	 * @param connexion
	 */
	public void closeConnection(){
		try {
			connexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void saveTwitterDataBase(List<TwitterData> listTweets) throws SQLException{
		
		Statement statement = null;
		int result = 0;
		try {
			connexion = DriverManager.getConnection( url, user, password);
			statement = connexion.createStatement();
			
			for(TwitterData twitterData : listTweets){
				String author = twitterData.getUserName().replace("'", "\\\'") ;
				String textContent = twitterData.getTextContent().replace("'", "\\\'");
				String searchTerm = twitterData.getSearchTerm().replace("'", "\\\'") ;
				
				result = statement.executeUpdate( "INSERT INTO twitterData(" +
						"author, textContent, searchKeyword) VALUES('" +
						author + "', '" +
						textContent + "', '" +
						searchTerm +
						"');" );
			}
			System.out.println( "Request result: " + result + "." );

		} catch ( SQLException e ) {
			System.out.println( "Error while connecting: "
					+ e.getMessage() );
		} finally {
			statement.close();
		}
	
	}




	/**
	 * Save to DB tweets
	 * @param request
	 * @throws SQLException 
	 */
	public void saveTweets(TwitterData twitterData) throws SQLException {
		String author = twitterData.getUserName().replace("'", "\\\'") ;
		String textContent = twitterData.getTextContent().replace("'", "\\\'");
		String searchTerm = twitterData.getSearchTerm().replace("'", "\\\'") ;
		
		Statement statement = null;
		int result = 0;
		try {
			connexion = DriverManager.getConnection( url, user, password);
			statement = connexion.createStatement();
			
			result = statement.executeUpdate( "INSERT INTO twitterData(" +
					"author, textContent, searchKeyword) VALUES('" +
					author + "', '" +
					textContent + "', '" +
					searchTerm +
					"');" );
			System.out.println( "Request result: " + result + "." );

		} catch ( SQLException e ) {
			System.out.println( "Error while connecting: "
					+ e.getMessage() );
		} finally {
			statement.close();
		}
	}


	/**
	 * Retrieve Tweets related to list of searchTerms.
	 * @param listSearchTerm
	 * @return
	 * @throws SQLException 
	 */
	public List<TwitterData> retrieveAllTweets(List<String> listSearchTerm) throws SQLException{
		List<TwitterData> allTwitterData = new ArrayList<TwitterData>();
		for(String searchTerm : listSearchTerm){
			List<TwitterData> twitterData = retrieveTweets(searchTerm);
			allTwitterData.addAll(twitterData);
		}
		return allTwitterData;
	}
	
	
	/**
	 * Retrieve Tweets related to search term.
	 * @param searchTerm
	 * @return
	 * @throws SQLException
	 */
	public List<TwitterData> retrieveTweets(String searchTermOld) throws SQLException{
		PreparedStatement updateTestParam = null;
    	ResultSet rs = null;
    	List<TwitterData> twitterData = new ArrayList<TwitterData>();
    	String searchTerm = searchTermOld.replace("'", "\\\'") ;
    	try {
    		connexion.setAutoCommit(false);
    		String updateString = "SELECT * FROM twitterData WHERE searchKeyword='" + searchTerm + "';";
    		updateTestParam = connexion.prepareStatement(updateString);
    		rs = updateTestParam.executeQuery();
    		
    		// Read Results returned from DB
    		while(rs.next()){
    			TwitterData tweet = new TwitterData(rs.getString(3), rs.getString(2), null, rs.getString(4), null);
    			tweet.createListWords(tweet.getTextContent());
    			twitterData.add(tweet);
    		}
    	} catch ( SQLException e ) {
    		System.out.println( "Error while connecting: "
    				+ e.getMessage() );
    	} finally {
    		if (updateTestParam != null) {
    			updateTestParam.close();
    		}
    	}
    	return twitterData;
	} 
	
	
	/**
	 * Save List of Word to DB
	 * @param wordList
	 * @throws SQLException
	 */
	public void saveWordList(List<String> wordList) throws SQLException{

		Statement statement = null;
		int result = 0;
		try {
			connexion = DriverManager.getConnection( url, user, password);
			statement = connexion.createStatement();

			for(String word : wordList){
			result = statement.executeUpdate( "INSERT INTO wordList(" +
					"word) VALUES('" +
					word + 
					"');" );
			}
			System.out.println( "Request result: " + result + "." );

		} catch ( SQLException e ) {
			System.out.println( "Error while connecting: "
					+ e.getMessage() );
		} finally {
			statement.close();
		}
	
	}
	
	
	public List<String> retrieveWordList() throws SQLException{
		PreparedStatement updateTestParam = null;
    	ResultSet rs = null;
    	List<String> wordList = new ArrayList<String>();
    	try {
    		connexion.setAutoCommit(false);
    		String updateString = "SELECT * FROM wordList;";
    		updateTestParam = connexion.prepareStatement(updateString);
    		rs = updateTestParam.executeQuery();
    		
    		// Read Results returned from DB
    		while(rs.next()){
    			String word = rs.getString(2);
    			wordList.add(word);
    		}
    	} catch ( SQLException e ) {
    		System.out.println( "Error while connecting: "
    				+ e.getMessage() );
    	} finally {
    		if (updateTestParam != null) {
    			updateTestParam.close();
    		}
    	}
    	return wordList;
	}
	
	public void truncateWordListTable() throws SQLException{
		PreparedStatement updateTestParam = null;
    	try {
    		connexion.setAutoCommit(false);
    		String sql = "TRUNCATE TABLE wordList;";
    		updateTestParam = connexion.prepareStatement(sql);
    		updateTestParam.executeQuery();
    	} catch ( SQLException e ) {
    		System.out.println( "Error while connecting: "
    				+ e.getMessage() );
    	} finally {
    		if (updateTestParam != null) {
    			updateTestParam.close();
    		}
    	}
	}
	
	public static void testConnexion() throws SQLException{

		try {
			Class.forName( "com.mysql.jdbc.Driver" );
		} catch ( ClassNotFoundException e ) {
			System.out.println( "Error while loading JDBC driver!"
					+ e.getMessage() );
		}

		Connection connexion = null;
		Statement statement = null;
		int result = 0;
		try {
			connexion = DriverManager.getConnection( url, user, password);
			statement = connexion.createStatement();
			System.out.println("Connexion successful!");
		} catch ( SQLException e ) {
			System.out.println( "Error while connecting: "
					+ e.getMessage() );
		} finally {
			statement.close();
			connexion.close();
		}
	}

}
