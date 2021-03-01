/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seomse.trading.view.util;

import java.io.*;
/**
 * @author ccsweets
 */
public class JarUtil {
    /**
     * Jar 파일에서 파일을 읽는다
     * @param filename file name
     * @return file contents
     * @throws IOException
     */
    public static String readFromJarFile(String filename)
            throws IOException
    {
        InputStream is = JarUtil.class.getClassLoader().getResourceAsStream(filename);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null)
        {
            sb.append(line);
        }
        br.close();
        isr.close();
        is.close();
        return sb.toString();
    }

    /**
     * Jar 파일 내부의 파일을 copy 한다
     * @param filename copy할 파일명
     * @param copyPath paste할 경로
     * @throws IOException
     */
    public static void copyFromJarFile(String filename,String copyPath)
            throws IOException
    {
        InputStream is = JarUtil.class.getClassLoader().getResourceAsStream(filename);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);

        File targetFile = new File(copyPath + "/" + filename);
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }
}
