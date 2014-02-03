package se.ltu.trafikgeneratorcoap;

import se.ltu.trafikgeneratorcoap.R;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Menu;

public class Main extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendData(View view) {
		Intent intent = new Intent(this, SendDataInput.class);
		startActivityForResult(intent, ResultType.SEND_DATA.index());
	}
	
	public void recieveData(View view) {
		Intent intent = new Intent(this, ReceiveDataInput.class);
		startActivityForResult(intent, ResultType.RECEIVE_DATA.index());
	}
	
	public void sendDataSensor(View view) {
		/*Intent intent = new Intent(this, SendDataSensorInput.class);
		startActivityForResult(intent, ResultType.SEND_DATA_SENSOR.index());*/
	}
	
	public void receiveDataSensor(View view) {
		/*Intent intent = new Intent(this, ReceiveDataSensorInput.class);
		startActivityForResult(intent, ResultType.RECEIVE_DATA_SENSOR.index());*/
	}
	
	public void exit(View view){
        finish();
        System.exit(0);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == ResultType.SEND_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			System.out.println("Sending done");
    		}
    	}
    	
    	if(requestCode == ResultType.RECEIVE_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			System.out.println("Receiving done");
    		}
    	}
    }
}

