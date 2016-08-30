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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/** Finds a valid UUID for a single Mp4 File **/
public class GetUUID {
  public static final int SIZE_AND_TYPE = 8;
  private static final int FIRST_UUID_SIZE = 24;
  private static final int MDAT_SIZE = 1;
  private static final int SDAT_SIZE = 8;
  private static final int SDAT_CHECKSUM = 1;
  private String TAG = "GetUUID";
  private final int readLimit = 8;

  private static final byte[] uuid = new byte[]{'u', 'u', 'i', 'd'};
  private static final byte[] mdat = new byte[]{'m', 'd', 'a', 't'};
  private static final byte[] cthd = new byte[]{'c', 't', 'h', 'd'};
  private static final byte[] sdat = new byte[]{'s', 'd', 'a', 't'};
  private static final byte[] ssss = new byte[]{'S', 'S', 'S', 'S'};
  private static final byte[] categoryHeader = new byte[]{'1', '0', '0', '0'};
  private static final byte[] categoryTennis = new byte[]{'T', 'E', 'N', 'N'};
  private static final byte[] cthdDataType = new byte[]{0x10};
  private byte[] uuidStringIdf = new byte[]{(byte) 0xb4, (byte) 0x8e, (byte) 0xb1, (byte) 0x7e, (byte) 0x64, (byte) 0x97,
      (byte) 0x11, (byte) 0xe4, (byte) 0xb3, (byte) 0xb5, (byte) 0x00, (byte) 0x1b, (byte) 0xdc,
      (byte) 0x03, (byte) 0xdc, (byte) 0xc2};


  public byte[] checkForUUIDHeader(File clickedMp4File) {
    boolean isUUID = false;
    byte type[] = new byte[4];
    byte uuidString[] = new byte[16];
    byte[] dataType = new byte[1];
    byte[] returnBuffer = null;
    int currentHeaderSize;
    DataInputStream mp4DataStream = null;

    try {
      FileInputStream mp4InputStream = new FileInputStream(clickedMp4File);
      BufferedInputStream mp4BufferStream = new BufferedInputStream(mp4InputStream);
      mp4DataStream = new DataInputStream(mp4BufferStream);

      while (!isUUID) {
        mp4DataStream.mark(readLimit);
        currentHeaderSize = mp4DataStream.readInt();
        mp4DataStream.read(type);
        isUUID = Arrays.equals(type, uuid);

        if (Arrays.equals(type, mdat) && currentHeaderSize == MDAT_SIZE) {
          skipMDAT(mp4DataStream);

        } else if (!isUUID) {
          mp4DataStream.reset();
          mp4DataStream.skipBytes(currentHeaderSize);

        } else {
          //a uuid header found
          //check for uuid size and type, then read uuid string identifier
          mp4DataStream.reset();
          mp4DataStream.mark(readLimit);
          mp4DataStream.skipBytes(SIZE_AND_TYPE);
          mp4DataStream.read(uuidString);

          //check if identifier matches
          if (Arrays.equals(uuidString, uuidStringIdf)) {
            mp4DataStream.mark(readLimit);
            int subtypeSize = mp4DataStream.readInt();
            mp4DataStream.read(type);

            //check to see if correct uuid. (correct uuid contains cthd)
            if (Arrays.equals(type, cthd)) {
              mp4DataStream.reset();
              mp4DataStream.readLong();
              mp4DataStream.read(type);
              if (Arrays.equals(type, ssss)) {
                mp4DataStream.read(type);
                if (Arrays.equals(type, categoryHeader)) {
                  mp4DataStream.read(type);
                  if (Arrays.equals(type, categoryTennis)) {

                    mp4DataStream.read(dataType); // data form

                    if (Arrays.equals(dataType, cthdDataType)) {
                      mp4DataStream.reset();
                      mp4DataStream.skipBytes(subtypeSize);

                      mp4DataStream.mark(readLimit);
                      subtypeSize = mp4DataStream.readInt();
                      mp4DataStream.read(type);

                      //confirm that uuid contains sdat and read
                      returnBuffer = readSDAT(type, returnBuffer, mp4DataStream, subtypeSize);
                    }
                  }
                }
              }
            }

          } else {
            //Look for next UUID - skip current uuid size
            mp4DataStream.skipBytes(currentHeaderSize - FIRST_UUID_SIZE);
            isUUID = false;
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (mp4DataStream != null) {
          mp4DataStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return returnBuffer;
  }

  private byte[] readSDAT(byte[] type, byte[] returnBuffer, DataInputStream mp4DataStream,
                          int subtypeSize) throws IOException {
    byte checksum[] = new byte[1];
    if (Arrays.equals(type, sdat)) {
      mp4DataStream.mark(readLimit);
      mp4DataStream.skipBytes(SDAT_CHECKSUM);
      returnBuffer = new byte[subtypeSize - SDAT_SIZE - SDAT_CHECKSUM];
      mp4DataStream.read(returnBuffer);

      byte[] temp = generateChecksum(returnBuffer);

      mp4DataStream.reset();
      mp4DataStream.read(checksum);

      if (Arrays.equals(checksum, temp)) {
        return returnBuffer;
      } else {
        return null;
      }

    }
    return returnBuffer;
  }

  private byte[] generateChecksum(byte[] returnBuffer) {
    int wordSum = 0;
    for (byte b : returnBuffer) {
      wordSum = wordSum + b;
    }
    wordSum = wordSum % 256;
    return new byte[]{(byte) wordSum};
  }

  private void skipMDAT(DataInputStream mp4DataStream) throws IOException {
    long mdatSize;
    mdatSize = mp4DataStream.readLong();
    mp4DataStream.reset();
    if (mdatSize > Integer.MAX_VALUE) {
      mp4DataStream.skipBytes(Integer.MAX_VALUE);
      mdatSize = mdatSize - Integer.MAX_VALUE;
      mp4DataStream.skipBytes((int) mdatSize);
    }
    mp4DataStream.skipBytes((int) mdatSize);
  }
}
