package se.ltu.trafikgeneratorcoap;

import se.ltu.trafikgeneratorcoap.send.Sending;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;

public class ReceiveDataSensor extends AbstractActivity {

    //A ProgressDialog object  
    private ProgressDialog progressDialog;  
  
	private String ip;
	private int port;
	private int seconds;
	private String filePath = "";
	private int timeout;
	private float random;
	private int retransmitt;
	private int nStart;
	private float probingRate;
	private int payloadSize;
	private int connections;
	
	private Intent intent;
	
    public boolean bool = false;
    public int indexer = 0;
    private int progressbarUpdate = 1;
    
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);          
        intent = getIntent();
        timeout = parseInt("timeout");
        random = parseFloat("random");
        retransmitt = parseInt("retransmitt");
        nStart = parseInt("nstart");
	    probingRate = parseFloat("probingrate");
	    payloadSize = parseInt("payloadsize");
	    port = parseInt("port");
	    seconds = parseInt("time");
	    connections = parseInt("connections");
	    
	    filePath = intent.getStringExtra("filename");
	    ip = intent.getStringExtra("ip");
	    
        //Initialize a LoadViewTask object and call the execute() method 
	    for(int i = 0; i < connections; i++)
	    {
	    	System.out.println("Creating processes nr : " + i);
	    	new LoadViewTask().execute();
	    }
    } 
    
    private float parseFloat(String s)
    {
	    try {
			float floatReturn = Float.parseFloat(intent.getStringExtra(s));
			return floatReturn;
		} catch (NumberFormatException e) {
			return 0.0f;
		}
    }
    
    private int parseInt(String s)
    {
	    try {
			int intReturn = Integer.parseInt(intent.getStringExtra(s));
			return intReturn;
		} catch (NumberFormatException e) {
			return 0;
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
	            progressDialog = new ProgressDialog(ReceiveDataSensor.this);  
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
    	   	//Sending.sendData(filePath);
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
