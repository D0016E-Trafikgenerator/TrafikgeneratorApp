package se.ltu.trafikgeneratorcoap;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.net.Inet4Address;
import se.ltu.trafikgeneratorcoap.R;

public class SendDataInput extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_data_input);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_input, menu);
		return true;
	}
	
	private int length = 10;
	private int counter = 0;
	
	private String[] fileName = 	new String[length];
	private String[] filePath = 	new String[length];
	private String[] ip = 			new String[length];
	
	private String[] timeout = 		new String[length];
	private String[] retransmitt = 	new String[length];		
	private String[] nStart = 		new String[length];	
	private String[] payloadSize = 	new String[length];	
	private String[] port = 		new String[length];	
	private String[] time = 		new String[length];
	private String[] sleep = 		new String[length];
	
	private String[] random = 		new String[length];	
	private String[] probingRate = 	new String[length];
	
	public void next(View view){
		//Port Field
		EditText portField = (EditText) findViewById(R.id.Port);
		String portString = portField.getText().toString();
		if (!portString.equals(""))
			port[counter] = portString;
	    
		//Time Field
		EditText timeField = (EditText) findViewById(R.id.Time);
		String timeString = timeField.getText().toString();
		if (!timeString.equals(""))
			time[counter] = timeString;
		
		EditText timeoutField = (EditText) findViewById(R.id.timeout);
		String timeoutString = timeoutField.getText().toString();
		if(!timeoutString.equals(""))
			timeout[counter] = timeoutString;
		
		EditText randomField = (EditText) findViewById(R.id.random);
		String randomString = randomField.getText().toString();
		if(!randomString.equals(""))
			random[counter] = randomString;
		
		EditText retransmittField = (EditText) findViewById(R.id.retransmitt);
		String retransmittString = retransmittField.getText().toString();
		if(!retransmittString.equals(""))
			retransmitt[counter] = retransmittString;
		
		EditText nstartField = (EditText) findViewById(R.id.nStart);
		String nstartString = nstartField.getText().toString();
		if(!nstartString.equals(""))
			nStart[counter] = nstartString;
		
		EditText probingrateField = (EditText) findViewById(R.id.probingRate);
		String probingrateString = probingrateField.getText().toString();
		if(!probingrateString.equals(""))
			probingRate[counter] = probingrateString;
		
		EditText payloadsizeField = (EditText) findViewById(R.id.payloadSize);
		String payloadsizeString = payloadsizeField.getText().toString();
		if(!payloadsizeString.equals(""))
			payloadSize[counter] = payloadsizeString;
		
		EditText sleepField = (EditText) findViewById(R.id.sleep);
		String sleepString = sleepField.getText().toString();
		if(!sleepString.equals(""))
			sleep[counter] = sleepString;
	
		EditText ipField = (EditText) findViewById(R.id.IPAddress);
		String ipString = ipField.getText().toString();
		//Check if IP-Address is valid
		boolean validIP = false;
		try {
			Inet4Address.getByName(ipString);
			ip[counter] = ipString;
			validIP = true;
		} catch (Exception e) {
			TextView v = (TextView) findViewById(R.id.Error);
			v.setText("IP-Address not valid");	
		}
	
		System.out.println("Counter: " + counter + " | " + filePath[counter] + " | ");
		if (validIP && !(filePath[counter] == null)) 
		{
			counter++;
			counter = Math.max(counter, 0);
			counter = Math.min(counter, length-1);
			
			//xxx Ask if user wants another file
			
			
		} 
		validIP = false;
	}
	
	public void send(View view)
	{
		next(view);
		if(!(filePath[0] == null))
		{
		    Intent intent = new Intent(this, SendData.class);
		    intent.putExtra("timeout", timeout);
		    intent.putExtra("random", random);
		    intent.putExtra("retransmitt", retransmitt);
		    intent.putExtra("nstart", nStart);
		    intent.putExtra("probingrate", probingRate);
		    intent.putExtra("payloadsize", payloadSize);
		    intent.putExtra("filename", filePath);
		    intent.putExtra("port", port);
			intent.putExtra("time", time);
		    intent.putExtra("ip", ip);
		    intent.putExtra("sleep", sleep);
		    intent.putExtra("totalconfigs", counter);
		    startActivityForResult(intent, ResultType.SEND_DATA.index());
	    }
	}
	
	public void load(View view){
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, ResultType.LOAD_FILE.index());
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == ResultType.LOAD_FILE.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			filePath[counter] = data.getStringExtra("path");
    			fileName[counter] = data.getStringExtra("name");
    			TextView v = (TextView) findViewById(R.id.Error);
    			v.setText("Config file:  " + fileName[counter]);
    		}
    	}
    	if(requestCode == ResultType.SEND_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    	}
    }
}

