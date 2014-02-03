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
	
	private String fileName = "";
	private String timeout = "";			//default : 2
	private String random = "";				//default : 1.5 
	private String retransmitt = "";		//default : 4
	private String nStart = "";				//default : 1
	private String probingRate = "";		//default : 1.0
	private String payloadSize = "";		//default : 512
	private String port = "";				//default : 5683
	private String ip = "";
	private String time = "";				//default : 10
	
	public void next(View view){
		//Port Field
		EditText portField = (EditText) findViewById(R.id.Port);
		String portString = portField.getText().toString();
		if (!portString.equals(""))
			port = portString;
	    
		//Time Field
		EditText timeField = (EditText) findViewById(R.id.Time);
		String timeString = timeField.getText().toString();
		if (!timeString.equals(""))
			time = timeString;
		
		EditText timeoutField = (EditText) findViewById(R.id.timeout);
		String timeoutString = timeoutField.getText().toString();
		if(!timeoutString.equals(""))
			timeout = timeoutString;
		
		EditText randomField = (EditText) findViewById(R.id.random);
		String randomString = randomField.getText().toString();
		if(!randomString.equals(""))
			random = randomString;
		
		EditText retransmittField = (EditText) findViewById(R.id.retransmitt);
		String retransmittString = retransmittField.getText().toString();
		if(!retransmittString.equals(""))
			retransmitt = retransmittString;
		
		EditText nstartField = (EditText) findViewById(R.id.nStart);
		String nstartString = nstartField.getText().toString();
		if(!nstartString.equals(""))
			nStart = nstartString;
		
		EditText probingrateField = (EditText) findViewById(R.id.probingRate);
		String probingrateString = probingrateField.getText().toString();
		if(!probingrateString.equals(""))
			probingRate = probingrateString;
		
		EditText payloadsizeField = (EditText) findViewById(R.id.payloadSize);
		String payloadsizeString = payloadsizeField.getText().toString();
		if(!payloadsizeString.equals(""))
			payloadSize = payloadsizeString;
	
		//IP-Field
		EditText ipField = (EditText) findViewById(R.id.IPAddress);
		String ipString = ipField.getText().toString();
		//Check if IP-Address is valid
		boolean validIP = false;
		try {
			Inet4Address.getByName(ipString);
			validIP = true;
			ip = ipString;
		} catch (Exception e) {
		}
	
		if (validIP) 
		{
		    Intent intent = new Intent(this, SendData.class);
		    intent.putExtra("timeout", timeout);
		    intent.putExtra("random", random);
		    intent.putExtra("retransmitt", retransmitt);
		    intent.putExtra("nstart", nStart);
		    intent.putExtra("probingrate", probingRate);
		    intent.putExtra("payloadsize", payloadSize);
		    intent.putExtra("filename", fileName);
		    intent.putExtra("port", port);
			intent.putExtra("time", time);
		    intent.putExtra("ip", ip);
		    startActivityForResult(intent, ResultType.SENDING_DATA.index());
		} 
		else 
		{
			TextView v = (TextView) findViewById(R.id.Error);
			v.setText("IP-Address not valid");
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
    			fileName = data.getStringExtra("result");
    			TextView v = (TextView) findViewById(R.id.Error);
    			v.setText(fileName);
    		}
    	}
    	if(requestCode == ResultType.SENDING_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    	}
    }
}

