package com.fractalsciences.socialmedia.connectors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datasift.EAPIError;
import org.datasift.EAccessDenied;
import org.datasift.ECompileFailed;
import org.datasift.EInvalidData;
import org.datasift.IMultiStreamConsumerEvents;
import org.datasift.Interaction;
import org.datasift.JSONdn;
import org.datasift.StreamConsumer;
import org.datasift.User;

import com.fractalsciences.socialmedia.handlers.TwitterData;




public class MultiStreamConnector implements IMultiStreamConsumerEvents {

	private User _user = null;
	
	private HashMap<String, String> _hashes = new HashMap<String, String>();

	private int _counter = 0;
	
	private String _java_stream_hash = null;
	
	private boolean _had_java = false;
	
	private HashMap<String, Integer> hashCount = new HashMap<String, Integer>();
	private List<TwitterData> listTweetData = new ArrayList<TwitterData>();
	private List<TwitterData> finalTweetData;
	private int maxInteractions =  0;
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) {
		
		int maxInteractions = 3;
		List<String[]> listQueries = new ArrayList<String[]>();
		String[] query1 = {"twitter.text contains \"iphone\" AND twitter.lang == \"en\"", "iphone"};
		String[] query2 = {"twitter.text contains \"smartphone\" AND twitter.lang == \"en\"", "smartphone"};
		listQueries.add(query1);
		listQueries.add(query2);
		new MultiStreamConnector().run(listQueries, maxInteractions);
	}

	
	public void run(List<TwitterData> finalTweetData, List<String[]> listQueries, int maxInteractions){
		
		this.finalTweetData = finalTweetData;
		run(listQueries, maxInteractions);
	}
	
	
	protected void run(List<String[]> listQueries, int maxInteractions) {
		try {
			this.maxInteractions = maxInteractions;
			
			// Authenticate
			System.out.println("Creating user...");
			_user = new User(DataSiftConfig.username, DataSiftConfig.api_key);

			// Building the hash list. All we actually need is an
			// ArrayList<String> of hashes, but we'll use a HashMap so we can
			// prepend the output with which stream the interaction matched
			System.out.println("Building hash list...");

			// We're going to watch for anything containing either "python" or
			// "php". Then, after 10 interactions we'll also subscribe to a stream with anything containing "java". After 10 further interactions we'll unsubscribe from the "java" stream.
			for(String[] query : listQueries){
				String hash = _user.createDefinition(query[0]).getHash();
				_hashes.put(hash, query[1]);
				hashCount.put(hash, 0);
			}
			
			// Create the consumer
			System.out.println("Getting the consumer...");
			StreamConsumer consumer = StreamConsumer.factory(_user,	StreamConsumer.TYPE_HTTP_MULTI, new ArrayList<String>(_hashes.keySet()), this);

			// And start consuming
			System.out.println("Consuming...");
			System.out.println("--");
			consumer.consume();
		} catch (EInvalidData e) {
			System.out.print("InvalidData: ");
			System.out.println(e.getMessage());
		} catch (ECompileFailed e) {
			System.out.print("CompileFailed: ");
			System.out.println(e.getMessage());
		} catch (EAccessDenied e) {
			System.out.print("AccessDenied: ");
			System.out.println(e.getMessage());
		}
	}
	
	
	
	/**
	 * Called when the connection has been established.
	 * 
	 * @param StreamConsumer consumer The consumer object.
	 */
	public void onConnect(StreamConsumer c) {
		System.out.println("Connected");
		System.out.println("--");
	}
	
	/**
	 * Called when the connection has disconnected.
	 * 
	 * @param StreamConsumer consumer The consumer object.
	 */
	public void onDisconnect(StreamConsumer c) {
		System.out.println("Disconnected");
		System.out.println("--");
	}

	/**
	 * Handle incoming data.
	 * 
	 * @param StreamConsumer
	 *            consumer The consumer object.
	 * @param String
	 *            hash The hash of the stream that matched this interaction.
	 * @param Interaction
	 *            interaction The interaction data.
	 * @throws EInvalidData
	 */
	public void onInteraction(StreamConsumer c, String hash, Interaction i)
			throws EInvalidData {
		try{
			if (hashCount.get(hash) < maxInteractions) {
				TwitterData tweetData = new TwitterData(i.getStringVal("interaction.content"), i.getStringVal("interaction.author.username"), null, _hashes.get(hash), null);
				tweetData.createListWords(tweetData.getTextContent());
				finalTweetData.add(tweetData);
				hashCount.put(hash, hashCount.get(hash) + 1);
//				System.out.print(i.getStringVal("interaction.author.username"));
//				System.out.print(": ");
//				System.out.println(i.getStringVal("interaction.content"));
			}
			else{
//				System.out.println("Unsubscribing: " + _hashes.get(hash));
//				_hashes.remove(hash);
				c.unsubscribe(hash);
			}

		} catch (EInvalidData e) {
			System.out.println("Exception: " + e.getMessage());
			System.out.print("Interaction: ");
			System.out.println(i);
		} catch (EAPIError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		System.out.println("--");

		// Stop after maxInteractions
		if (isMaxNumberTweetsReached()) {
			System.out.println("Stopping consumer...");
			c.stop();
//			for(TwitterData tweet : finalTweetData){
//				System.out.println("TWEET: " + tweet.getTextContent());
//			}
		}
	}
	
	public boolean isMaxNumberTweetsReached(){
		for(Map.Entry<String, Integer> entry :  hashCount.entrySet()){
			if(entry.getValue() != maxInteractions){
				return false;
			}
		}
		return true;
	}

	/**
	 * Handle delete notifications.
	 * 
	 * @param StreamConsumer
	 *            consumer The consumer object.
	 * @param Interaction
	 *            interaction The interaction data.
	 * @throws EInvalidData
	 */
	public void onDeleted(StreamConsumer c, String hash, Interaction i)
			throws EInvalidData {
		// Ignored for this example
	}

	/**
	 * Handle status notifications
	 * 
	 * @param StreamConsumer
	 *            consumer The consumer object.
	 * @param String
	 *            type The status notification type.
	 * @param JSONdn
	 *            info The notification data.
	 */
	public void onStatus(StreamConsumer consumer, String type, JSONdn info) {
		System.out.print("STATUS: ");
		System.out.println(type);
	}

	/**
	 * Called when the consumer has stopped.
	 * 
	 * @param DataSift_StreamConsumer
	 *            $consumer The consumer object.
	 * @param string
	 *            $reason The reason the consumer stopped.
	 */
	public void onStopped(StreamConsumer consumer, String reason) {
		System.out.print("Stopped: ");
		System.out.println(reason);
	}

	/**
	 * Called when a warning is received in the data stream.
	 * 
	 * @param DataSift_StreamConsumer consumer The consumer object.
	 * @param string message The warning message.
	 */
	public void onWarning(StreamConsumer consumer, String message)
			throws EInvalidData {
		System.out.println("Warning: " + message);
	}

	/**
	 * Called when an error is received in the data stream.
	 * 
	 * @param DataSift_StreamConsumer consumer The consumer object.
	 * @param string message The error message.
	 */
	public void onError(StreamConsumer consumer, String message)
			throws EInvalidData {
		System.out.println("Error: " + message);
	}
}
