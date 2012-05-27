package felix.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TodoLogin extends Activity {
	
	public static final String PREF_PASSWORD = "PREF_PASSWORD";
	public static final String PREF_USERNAME = "PREF_USERNAME";
	
	Context context;
	Intent intent;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	EditText username_input;
	EditText password_input;
	Button login_button;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /* these lines let us know that there are prefs to be read */
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        /* hook our fields into variables */
        username_input = (EditText)findViewById(R.id.username_input);
        password_input = (EditText)findViewById(R.id.password_input);
        login_button = (Button)findViewById(R.id.login_button);
        
        /* Populate our fields with credentials already in our prefs, but only do so for username */
        username_input.setText(prefs.getString(this.PREF_USERNAME, ""));
        
        /* prepare an editor so we can change our prefs on button click */
		editor = prefs.edit();
		
		login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String username = username_input.getText().toString();
            	String password = password_input.getText().toString();
            	
            	if (validateCred(username, password)) {
	            	editor.putString(TodoLogin.PREF_USERNAME, username);
	            	editor.putString(TodoLogin.PREF_PASSWORD, password);
	            	
	            	editor.commit();
	            	
	            	/* once we are done setting our prefs return of front */
	            	intent = new Intent(TodoLogin.this, TodoMain.class);
	            	startActivity(intent);
            	}
            	else {
            		/* our input is not ok, return a toast and do nothing */
            		Toast toast = Toast.makeText(TodoLogin.this, "Please enter login information.", Toast.LENGTH_SHORT);
            		toast.show();
            	}
            }

			private boolean validateCred(String username, String password) {
				if (username.equals("") || password.equals(""))
					return false;
				return true;
			}
        });
		
		
    }
    
}