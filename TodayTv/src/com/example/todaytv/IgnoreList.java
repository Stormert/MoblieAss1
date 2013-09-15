package com.example.todaytv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class IgnoreList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ignore_list);
		
		final ListView itemlist = (ListView) findViewById(R.id.itemlist);  //you need this is the xml layout for your program
		String[] ignoreList = Show.getAllNames(new DatabaseHelper(this));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ignoreList);  // assign the titles to the list
        itemlist.setAdapter(adapter);  // assign the titles to the list
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        
      //click actions
        itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id){
				TextView clickedView = (TextView) view; 						//get clicked view
				String name = clickedView.getText().toString();					//remove episode name
				Show clickedShow = new Show(name, -1);							//add to new show object
				clickedShow.delete(dbHelper, name);										//save to database
				Toast.makeText(IgnoreList.this, name+" is no longer Ignored", Toast.LENGTH_SHORT).show(); //what happened
				clickedView.setTextColor(Color.parseColor("#615CFF"));
				//itemlist.removeViewInLayout(clickedView); //this buggs out big time for some reason
			}
		});
	}

}
