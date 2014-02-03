package se.ltu.trafikgeneratorcoap;

import se.ltu.trafikgeneratorcoap.R;

import android.os.Bundle;
import android.view.Menu;

public class ReceiveDataSensor extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_data_sensor);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recieve_data_sensor, menu);
		return true;
	}

}
