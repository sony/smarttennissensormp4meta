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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FindMp4AsyncTask.ParseMp4FromTennisSensorCallback {
  private ListView mListView;
  public static final String DATA_BUNDLE = "elements";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mListView = (ListView) this.findViewById(R.id.list);
  }

  @Override
  public void onResume() {
    super.onResume();
    checkPermissions();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      new FindMp4AsyncTask(this).execute();
    } else {
      Toast.makeText(MainActivity.this, "Permission not granted.", Toast.LENGTH_LONG).show();
    }
  }

  //check if current sdk gives permission to access internal storage
  private void checkPermissions() {
    if (Build.VERSION.SDK_INT >= 23) {
      boolean hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;

      if (hasPermission) {
        new FindMp4AsyncTask(this).execute();
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
      }
    } else {
      new FindMp4AsyncTask(this).execute();
    }
  }

  @Override public void onPostExecute(ArrayList<File> result) {

    //create a list view with using mp4list
    if (result == null) {
      setContentView(R.layout.file_error);
    } else {

      FileNameAdapter adapter = new FileNameAdapter(MainActivity.this, result);
      mListView.setAdapter(adapter);
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View itemView, int position, long id) {
          final File clickedMp4File = (File) parent.getAdapter().getItem(position);
          class OnClickAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

            @Override protected ArrayList<String> doInBackground(Void... params) {
              FileParser parser = new FileParserImp();
              ArrayList<String> dataList = parser.parse(clickedMp4File);
              return dataList;
            }

            @Override protected void onPostExecute(ArrayList<String> dataList) {
              Intent dataIntent = new Intent(itemView.getContext(), DataActivity.class);
              Bundle bundle = new Bundle();
              bundle.putStringArrayList(DATA_BUNDLE, dataList);
              dataIntent.putExtras(bundle);
              startActivity(dataIntent);
            }
          }
          new OnClickAsyncTask().execute();
        }
      });
    }
  }

  private static class FileNameAdapter extends BaseAdapter {
    Context context;
    ArrayList<File> data;
    private LayoutInflater inflater = null;

    public FileNameAdapter(Context context, ArrayList<File> data) {
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
        vi = inflater.inflate(R.layout.row, null);
      TextView text = (TextView) vi.findViewById(R.id.text);
      String filePath = data.get(position).toString();
      int shortened = filePath.indexOf("Movie");
      String onlyMp4Name = filePath.substring(shortened + 6);
      text.setText(onlyMp4Name);
      return vi;
    }
  }

}
