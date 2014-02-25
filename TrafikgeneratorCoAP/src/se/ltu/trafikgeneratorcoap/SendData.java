package se.ltu.trafikgeneratorcoap;

import se.ltu.trafikgeneratorcoap.send.Sending;
import se.ltu.trafikgeneratorcoap.send.TrafficConfig;
import se.ltu.trafikgeneratorcoap.send.Settings;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;

public class SendData extends AbstractActivity {  
    //A ProgressDialog object  
    private ProgressDialog progressDialog;  
  
	private String ip;
	private Integer port;
	private Integer seconds;
	private String filePath = "";
	private Integer timeout;
	private Float random;
	private Integer retransmitt;
	private Integer nStart;
	private Float probingRate;
	private Integer payloadSize;
	private Integer connections;
	
	private Intent intent;
	
    public boolean bool = false;
    public int indexer = 0;
    private int progressbarUpdate = 1;
    
    private TrafficConfig config;
    
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        intent = getIntent();
        timeout = parseInt("timeout");
        retransmitt = parseInt("retransmitt");
        nStart = parseInt("nstart");
	    payloadSize = parseInt("payloadsize");
	    port = parseInt("port");
	    seconds = parseInt("time");
	    connections = parseInt("connections");
        random = parseFloat("random");
	    probingRate = parseFloat("probingrate");
	    filePath = intent.getStringExtra("filename");
	    ip = intent.getStringExtra("ip");
	    
	    config = new TrafficConfig(TrafficConfig.fileToString(filePath));
	    
	    if(!(timeout == null))
	    	config.setIntegerSetting(Settings.COAP_ACK_TIMEOUT, timeout);
	    if(!(retransmitt == null))
	    	config.setIntegerSetting(Settings.COAP_MAX_RETRANSMIT, retransmitt);
	    if(!(nStart == null))
	    	config.setIntegerSetting(Settings.COAP_NSTART, nStart);
	    if(!(payloadSize == null))
	    	config.setIntegerSetting(Settings.TRAFFIC_MESSAGESIZE, payloadSize);
	    if(!(port == null))
	    	config.setIntegerSetting(Settings.TEST_TESTPORT, port);
	    if(!(seconds == null))
	    	config.setIntegerSetting(Settings.TRAFFIC_MAXSENDTIME, seconds);	    
	    if(!(random == null))
	    	config.setDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR, random);
	    if(!(probingRate == null))
	    	config.setDecimalSetting(Settings.COAP_PROBING_RATE, probingRate);
	    
	    config.setStringSetting(Settings.TEST_SERVER, ip);
	    
        //Initialize a LoadViewTask object and call the execute() method 
	    for(int i = 0; i < connections; i++)
	    {
	    	System.out.println("Creating processes nr : " + i);
	    	new LoadViewTask().execute();
	    }
    } 
    
    private Float parseFloat(String s)
    {
	    try {
			float floatReturn = Float.parseFloat(intent.getStringExtra(s));
			return floatReturn;
		} catch (NumberFormatException e) {
			return null;
		}
    }
    
    private Integer parseInt(String s)
    {
	    try {
			int intReturn = Integer.parseInt(intent.getStringExtra(s));
			return intReturn;
		} catch (NumberFormatException e) {
			return null;
		}
    }
    
    //To use the AsyncTask, it must be subclassed  
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
    	private int index;
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	this.index = indexer;
        	indexer++;
        	if(this.index == 0)
        	{
	            progressDialog = new ProgressDialog(SendData.this);  
	            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	            progressDialog.setTitle("Loading...");  
	            progressDialog.setMessage("Sending data... Please wait.");  
	            progressDialog.setCancelable(true);  
	            progressDialog.setIndeterminate(false);  
	            progressDialog.setMax(connections);   
	            progressDialog.setProgress(0);  
	            progressDialog.show();
        	}
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {   
        	Sending.sendData(filePath, getApplicationContext());
        	publishProgress(progressbarUpdate++);
        	return null;
        }  
  
        //Update the progress  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
            //set the current progress of the progress dialog  
            progressDialog.setProgress(values[0]);  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
        	System.out.println("End of process nr : " + this.index);
			if(progressbarUpdate == (connections + 1))
			{
	            progressDialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
        }  
    }  
}  
