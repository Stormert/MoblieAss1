package com.example.todaytv;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	final static String RSSFEED_URL = "http://thetvdb.com/rss/newtoday.php";
	final static String NO_NETWORK_ERROR = "NO INTERNETT CONNECTION!";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        final ListView itemlist = (ListView) findViewById(R.id.itemlist);  //you need this is the xml layout for your program
        fillList(itemlist);		//Moved this to onResume as i want it refreshed when comming back from the ignored list.
        
        //click actions
        itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id){
				TextView clickedView = (TextView) view; 						//get clicked view
				String name = extractName(clickedView.getText().toString());	//remove episode name
				if(!name.equals(NO_NETWORK_ERROR)){
					Show clickedShow = new Show(name, -1);							//add to new show object
					clickedShow.save(dbHelper);										//save to database
					Toast.makeText(MainActivity.this, name+" added to ignore", Toast.LENGTH_SHORT).show(); //what happened
					itemlist.removeViewInLayout(clickedView);
				}
			}
		});
	}

	
	//fill list with the shows not on ignore list
	private void fillList(ListView list){
		String[] RSSTitles;
		List<String> showNames = new ArrayList<String>(); //list of names not on the ignore list
		if(isNetworkAvailable()){
	        RSSTitles = downloadRSS(RSSFEED_URL);        
	        //make a list of names not on the ignored list in the database
	        String[] ignoreList = Show.getAllNames(new DatabaseHelper(this)); //get list of ignored shows
	        for(int x=0; x < RSSTitles.length; x++){
	        	boolean found = false;
	        	for(int i = 0; i < ignoreList.length; i++){
	        		if(extractName(RSSTitles[x]).equals(ignoreList[i])){
	        			found = true;
	        		}
	        	}
	        	if(!found)
	        		showNames.add(RSSTitles[x]);
	        }
		}else{
			showNames.add(NO_NETWORK_ERROR);
		}
        //print list to list view
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,showNames); // more needed in the resources
        list.setAdapter(adapter);  // assign the titles to the list
		
	}
	
	//refresh the list
	@Override
	protected void onResume() {
	    super.onResume();
	   
	    final ListView itemlist = (ListView) findViewById(R.id.itemlist);  //you need this is the xml layout for your program
        fillList(itemlist);
	}
	
	/**
     * @author Simon McCallum
     *
     * This function downloads and parses an rss feed from a specific
     * location passed in the URL and then
     * @param  URL          a String containing the absolute URL giving the 
     *                                          base location and name of the rss feed
     * @return RSSTitles    an Array of Strings which are the titles from the RSS feed
     */ 
    private String[] downloadRSS(String URL) {
        InputStream in = null;
        String[] RSSTitles = new String[0];
        try {
            in = OpenHttpConnection(URL);
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }        
            
            doc.getDocumentElement().normalize(); 
            
            //---retrieve all the <item> nodes---
            NodeList itemNodes = doc.getElementsByTagName("item"); 
            
            String strTitle = "";
            RSSTitles = new String[itemNodes.getLength()];
            for (int i = 0; i < itemNodes.getLength(); i++) { 
                Node itemNode = itemNodes.item(i); 
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) { 
                    //---convert the Node into an Element---
                    Element itemElement = (Element) itemNode;
                    
                    //---get all the <title> element under the <item> 
                    // element---
                    NodeList titleNodes = (itemElement).getElementsByTagName("title");
                    
                    //---convert a Node into an Element---
                    Element titleElement = (Element) titleNodes.item(0);
                    
                    //---get all the child nodes under the <title> element---
                    NodeList textNodes = 
                        ((Node) titleElement).getChildNodes();
                    
                    //---retrieve the text of the <title> element---
                    strTitle = ((Node) textNodes.item(0)).getNodeValue();
                    RSSTitles[i] = strTitle;
                } 
            }
        } catch (IOException e1) {
            e1.printStackTrace();            
        }
        return RSSTitles;
    }
    
    private String extractName(String fullName){
    	String[] name = fullName.split(":");
    	return name[0];
    }
	
	private InputStream OpenHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		
		final URL url = new URL(urlString);
		final URLConnection conn = url.openConnection();
		
		if(!(conn instanceof HttpURLConnection)){
			throw new IOException("Not an  HTTP connection");
		}
		
		try {
			final HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.getInputStream();
			response = httpConn.getResponseCode();
			if(response == HttpURLConnection.HTTP_OK){
				in = httpConn.getInputStream();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return in;
	}
	
	/** open leader boards */
    public void openIgnoreList(View view){
    	Intent intent = new Intent(this, IgnoreList.class);
    	startActivity(intent);
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
