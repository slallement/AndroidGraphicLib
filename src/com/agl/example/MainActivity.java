package com.agl.example;

import com.agl.graphics.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	protected Button b_ex1 = null;
	protected Button b_ex2 = null;
	protected Button b_ex3 = null;
	
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
				Intent intent = new Intent(MainActivity.this, com.agl.example2.Activity2.class);
				startActivity(intent);
			}
			
		});
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout1);
		b_ex3 = new Button(layout.getContext());
		b_ex3.setText("Demo 3");
		layout.addView(b_ex3);
		
		//b_ex3 = (Button) findViewById(R.id.button3);
		b_ex3.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, com.agl.example3.Activity3.class);
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
