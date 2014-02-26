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
  
	/*private String[] ip;
	private Integer[] port;
	private Integer[] seconds;
	private String[] filePath;
	private Integer[] timeout;
	private Float[] random;
	private Integer[] retransmitt;
	private Integer[] nStart;
	private Float[] probingRate;
	private Integer[] payloadSize;
	private Integer[] connections;*/
    
    private String[] ip;
	private String[] port;
	private String[] seconds;
	private String[] filePath;
	private String[] timeout;
	private String[] random;
	private String[] retransmitt;
	private String[] nStart;
	private String[] probingRate;
	private String[] payloadSize;
	private String[] sleep;
	
	private Intent intent;
	private int totalConfigs;
	
	
    public boolean bool = false;
    public int indexer = 1;
    private int progressbarUpdate = 1;
    
    private TrafficConfig[] config;
    
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
		System.out.println("Sending");
        super.onCreate(savedInstanceState);  
        intent = getIntent();
        timeout = 			intent.getStringArrayExtra("timeout");
        retransmitt = 		intent.getStringArrayExtra("retransmitt");
        nStart = 			intent.getStringArrayExtra("nStart");
	    payloadSize = 		intent.getStringArrayExtra("payloadSize");
	    port = 				intent.getStringArrayExtra("port");
	    seconds = 			intent.getStringArrayExtra("seconds");
        random = 			intent.getStringArrayExtra("random");
	    probingRate = 		intent.getStringArrayExtra("probingRate");
	    filePath = 			intent.getStringArrayExtra("filePath");
	    ip = 				intent.getStringArrayExtra("ip");
	    sleep = 			intent.getStringArrayExtra("sleep");
	    totalConfigs =		intent.getIntExtra("totalconfigs", 0);
	    
	    System.out.println("configs: " + totalConfigs);
	    
	    config = new TrafficConfig[totalConfigs];

        //Initialize a LoadViewTask object and call the execute() method 
    	//System.out.println("Creating processes nr : " + i);
	    
    	/*config[i] = new TrafficConfig(TrafficConfig.fileToString(filePath[i]));
    	
	    if(!(parseInt(timeout[i]) == null))
	    	config[i].setIntegerSetting(Settings.COAP_ACK_TIMEOUT, parseInt(timeout[i]));
	    if(!(parseInt(retransmitt[i]) == null))
	    	config[i].setIntegerSetting(Settings.COAP_MAX_RETRANSMIT, parseInt(retransmitt[i]));
	    if(!(parseInt(nStart[i]) == null))
	    	config[i].setIntegerSetting(Settings.COAP_NSTART, parseInt(nStart[i]));
	    if(!(parseInt(payloadSize[i]) == null))
	    	config[i].setIntegerSetting(Settings.TRAFFIC_MESSAGESIZE, parseInt(payloadSize[i]));
	    if(!(parseInt(port[i]) == null))
	    	config[i].setIntegerSetting(Settings.TEST_TESTPORT, parseInt(port[i]));
	    if(!(parseInt(seconds[i]) == null))
	    	config[i].setIntegerSetting(Settings.TRAFFIC_MAXSENDTIME, parseInt(seconds[i]));
	    
	    if(!(parseFloat(random[i]) == null))
	    	config[i].setDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR, parseFloat(random[i]));
	    if(!(parseFloat(probingRate[i]) == null))
	    	config[i].setDecimalSetting(Settings.COAP_PROBING_RATE, parseFloat(probingRate[i]));
	    
	    config[i].setStringSetting(Settings.TEST_SERVER, ip[i]);*/
    	
    	new LoadViewTask().execute();
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
        	if(this.index == 1)
        	{
	            progressDialog = new ProgressDialog(SendData.this);  
	            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	            progressDialog.setTitle("Loading...");  
	            progressDialog.setMessage("Sending data... Please wait.");  
	            progressDialog.setCancelable(true);  
	            progressDialog.setIndeterminate(false);  
	            progressDialog.setMax(totalConfigs);   
	            progressDialog.setProgress(0);  
	            progressDialog.show();
        	}
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {   
        	//Sending.sendData(filePath[index], getApplicationContext());
        	publishProgress(progressbarUpdate++);
        	if(!(index == totalConfigs))
        	{
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		new LoadViewTask().execute();
        	}
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
			if(progressbarUpdate == (totalConfigs + 1))
			{
	            progressDialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
        }  
    }  
}  
