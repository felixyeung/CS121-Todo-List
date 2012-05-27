package felix.todo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.NameValuePair; 
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TodoMain extends Activity {
	
	Intent intent;
	
	String username;
	String password;
	
	static TextView feedback;

	/* we just need these fields for menus to work */
	static final private int MENU_PREFERENCES = Menu.FIRST;
	private static final int SHOW_PREFERENCES = 1;
	
	/* capture our preferences into some varibles. 
	 * we will try to log in with them
	 */
    private void fetchPrefs() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        username = prefs.getString(TodoLogin.PREF_USERNAME, "");
        password = prefs.getString(TodoLogin.PREF_PASSWORD, "");  
    }
    
    /* do we have saved credentials? */
	private boolean hasSavedCreds() {
		if (username.equals("") || password.equals(""))
			return false;
		return true;
	}
    	
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
    public void onStart() {
    	super.onStart(); 
    	
        fetchPrefs();
        
        /* do we have username and password saved? */
        if (!hasSavedCreds()) {
        	intent = new Intent(TodoMain.this, TodoLogin.class);
        	startActivity(intent);
        }
        
        /* 
         * DO THINGS HERE
         */
        feedback = (TextView)findViewById(R.id.main_text);
        
        String items_json = getItems(username, password);
        
        TodoMain.feedback.setText(items_json);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0, MENU_PREFERENCES, Menu.NONE, "Edit Credentials");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	switch(item.getItemId()) {
    		case (MENU_PREFERENCES): {
    			intent = new Intent(TodoMain.this, TodoLogin.class);
    			startActivityForResult(intent, SHOW_PREFERENCES);
    	        return true;
    		}
    	}
    	
    	return false;
    }
    
    public String getItems(String username, String password) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://crowdlab.soe.ucsc.edu/ToDoListServer/default/android_get_items.json");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            
            HttpEntity entity = response.getEntity();
            
            String raw_json = "";
            
            if (entity != null) {
            	/* 
            	 * we got our "webpage" to return here as a stream
            	 */
            	InputStream instream = entity.getContent();
            	
            	/* now we need to make the "webpage" into a string */
            	BufferedInputStream binstream = new BufferedInputStream(instream);
            	ByteArrayBuffer buff = new ByteArrayBuffer(50);
            	
            	int curr = 0;
            	while ((curr = binstream.read()) != -1) {
            		buff.append((byte)curr);
            	}
            	
            	raw_json = new String(buff.toByteArray());
            	
            	/* Jeez, end making string */
            }
            
            
            /*ok, we have good results here */
            return(raw_json); 
            
        } catch (ClientProtocolException e) {
        	return e.getMessage();
        } catch (IOException e) {
        	return e.getMessage();
        }
    }
}