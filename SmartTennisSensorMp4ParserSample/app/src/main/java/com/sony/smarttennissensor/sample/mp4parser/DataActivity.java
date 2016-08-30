/*
 * Copyright (C) 2016 Sony Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sony.smarttennissensor.sample.mp4parser;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {
  private ListView mListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_data);
    mListView = (ListView) findViewById(R.id.list_data);

    //get extracted meta data from Main Activity
    Intent intent = getIntent();
    ArrayList<String> mp4MetaData = intent.getStringArrayListExtra("elements");

    if (mp4MetaData == null) {
      setContentView(R.layout.error);
    } else {
      new DisplayXML().execute(mp4MetaData);
    }
  }

  private class DisplayXML extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

    //grab elements from XML to display
    @Override protected ArrayList<String> doInBackground(ArrayList<String>... params) {
      ArrayList<String> mp4MetaData = params[0];
      ArrayList<String> mp4DisplayData = new ArrayList<>();
      int shotCounter = 1;

      for (int i = 0; i < mp4MetaData.size(); i++) {

        String currentTag = null;
        String currentData = mp4MetaData.get(i);
        int colonIndex = currentData.indexOf(":");
        String contents = currentData.substring(colonIndex + 1);
        String dataTag = currentData.substring(0, colonIndex);

        switch (dataTag) {
          case "Shot Data":
            if (shotCounter != 1) {
              currentTag = "\n" + "\nShot Data " + shotCounter;
            } else
              currentTag = "Shot Data " + shotCounter;
            shotCounter++;
            break;
          case "A":
            currentTag = "Timestamp";
            break;
          case "B":
            currentTag = "Swing Type";
            break;
          case "C":
            currentTag = "Racket id";
            break;
          case "D":
            currentTag = "Sensor udid";
            break;
          case "E":
            currentTag = "Racket Model Version";
            break;
          case "F":
            currentTag = "Sensor Model Name";
            break;
          case "G":
            currentTag = "Sensor Region";
            break;
          case "H":
            currentTag = "Sensor Firmware";
            break;
          case "I":
            currentTag = "Sensor Engine";
            break;
          case "J":
            currentTag = "Impact Position";
            break;
          case "K":
            currentTag = "Impact Position Prob";
            break;
          case "L":
            currentTag = "Impact Energy";
            break;
          case "M":
            currentTag = "Ball Speed";
            break;
          case "N":
            currentTag = "Ball Spin";
            break;
          case "O":
            currentTag = "Swing Speed";
            break;
          case "P":
            currentTag = "Swing Type Prob";
            break;
          case "Q":
            currentTag = "Dominant Hand";
            break;
          case "R":
            currentTag = "Data Accuracy";
            break;
          case "S":
            currentTag = "Delete Flag";
            break;
          case "T":
            currentTag = "Update Flag";
            break;
        }
        mp4DisplayData.add(currentTag + ": " + contents);
      }
      return mp4DisplayData;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
      if (result != null) {
        mListView.setAdapter(new DataAdapter(DataActivity.this, result));
      } else {
        setContentView(R.layout.error);
      }
    }
  }

  private static class DataAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> data;
    private LayoutInflater inflater = null;

    public DataAdapter(Context context, ArrayList<String> data) {
      this.context = context;
      this.data = data;
      inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return data.size();
    }

    @Override
    public Object getItem(int position) {
      return data.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View vi = convertView;
      if (vi == null)
        vi = inflater.inflate(R.layout.data_row, null);
      TextView text = (TextView) vi.findViewById(R.id.text_1);
      text.setText(data.get(position));

      return vi;
    }
  }
}




