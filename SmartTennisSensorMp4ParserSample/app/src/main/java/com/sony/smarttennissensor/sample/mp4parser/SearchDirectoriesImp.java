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

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

/** Return an ArrayList<Files> for Videos found on the device from Smart Tennis Sensor**/
class SearchDirectoriesImp implements SearchDirectories {

  private static final String USER_FOLDER_PATH = "/Smart Tennis Sensor/User";
  private static final String MOVIE_PATH_EXTENSION = "/Movie";
  private static final String MP4_PATH_EXTENSION = ".mp4";

  @Override public ArrayList<File> getVideos() {
    ArrayList<File> userDirectoryList = new ArrayList<>();
    ArrayList<File> mp4List = new ArrayList<>();
    File mSmartTennisVideoDir = new File(Environment.getExternalStorageDirectory().getPath() + USER_FOLDER_PATH);

    //Putting User directories from "/Smart Tennis Sensor/User" into list
    try {
      Collections.addAll(userDirectoryList, mSmartTennisVideoDir.listFiles());
    } catch (Exception e) {
      if (true)
        return null;
    }

    for (File userDirectories : userDirectoryList) {
      File moviePath = new File(userDirectories.toString() + MOVIE_PATH_EXTENSION);

      //check to see if movie path of user exists
      if (moviePath.exists()) {

        //grabbing files with extension .mp4 (don't need to check header)
        File[] mp4files = moviePath.listFiles(new FilenameFilter() {
          @Override public boolean accept(File dir, String file) {
            return file.endsWith(MP4_PATH_EXTENSION);
          }
        });
        //add extracted mp4 files to arrayList
        for (File mp4File : mp4files) {
          mp4List.add(mp4File);
        }
      }
    }
    return mp4List;
  }
}
