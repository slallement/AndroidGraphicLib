package com.agl.example;

import com.agl.graphics.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	protected Button b_ex1 = null;
	protected Button b_ex2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b_ex1 = (Button) findViewById(R.id.button1);
		b_ex1.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				startActivity(intent);
			}
			
		});
		
		b_ex2 = (Button) findViewById(R.id.button2);
		b_ex2.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, com.agl.example2.GameActivity2.class);
				startActivity(intent);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
