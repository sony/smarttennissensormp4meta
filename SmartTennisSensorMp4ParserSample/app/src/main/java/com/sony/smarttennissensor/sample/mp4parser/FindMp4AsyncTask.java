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

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

//returning list of all mp4 files found on device
public class FindMp4AsyncTask extends AsyncTask<Void, Void, ArrayList<File>> {
  private ParseMp4FromTennisSensorCallback mCallback = null;

  public FindMp4AsyncTask(ParseMp4FromTennisSensorCallback callback) {
    mCallback = callback;
  }

  @Override protected ArrayList<File> doInBackground(Void... params) {

    SearchDirectories searchDirectory = new SearchDirectoriesImp();
    ArrayList<File> mp4List = searchDirectory.getVideos();
    return mp4List;
  }

  //deal with onPostExecute in MainAcitivity
  @Override
  protected void onPostExecute(ArrayList<File> result) {
    if (mCallback != null) {
      mCallback.onPostExecute(result);
    }
  }

  /**
   * Interface for Activity callback
   */
  public interface ParseMp4FromTennisSensorCallback {
    void onPostExecute(ArrayList<File> result);

  }
}
