package org.ab498.videofied;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Filess
{
    static public String readLine(Context context, String fileName, int lineNum){

        String lineIWant="";
        try{
            BufferedReader br = new BufferedReader(
                    new FileReader(fileName));
            for(int i = 0; i < lineNum-1; ++i)
                br.readLine();
            lineIWant = br.readLine();
        }catch(Exception e){}

        return lineIWant;

    }
    static public String readFullAssets(Context context, String fileName){

        BufferedReader reader = null;
        String res="";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                res+=mLine+"\n";
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return res;
    }
    static public String readFull(Context context, String fileName){

        if(new File(fileName).exists()){
            String linesIWant="";
            String line = "";
            try{
                BufferedReader br = new BufferedReader(
                        new FileReader(fileName));
                //br.readLine();
                while((line = br.readLine()) != null)
                    linesIWant += line+"\n";
                //lineIWant = br.readLine();
            }catch(Exception e){}

            return linesIWant.trim();
        }else{
            return "";
        }

    }
    static public boolean append(Context context, String fileName, String data){
        File file1 = new File(fileName);
        try
        {
            file1.createNewFile();
            FileWriter fw = new FileWriter(file1, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data); // "\n" because the new data will overwrite the previous
            //and it will be lost
            bw.flush();
            FileReader fr = new FileReader(file1);
            BufferedReader br = new BufferedReader(fr);

            br.close();
            bw.close();

        }
        catch (IOException e)
        {}
        return true;
    }
    static public boolean overwrite(Context context, String fileName, String data){
        File file1 = new File(fileName);
        try
        {
            file1.createNewFile();
            FileWriter fw = new FileWriter(file1);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data); // "\n" because the new data will overwrite the previous
            //and it will be lost
            bw.flush();
            FileReader fr = new FileReader(file1);
            BufferedReader br = new BufferedReader(fr);

            br.close();
            bw.close();

        }
        catch (IOException e)
        {}
        return true;
    }


    public static void extractFolder(String zipFile) throws IOException {
        int buffer = 2048;
        File file = new File(zipFile);

        try (ZipFile zip = new ZipFile(file)) {
            String newPath = zipFile.substring(0, zipFile.length() - 4);

            new File(newPath).mkdir();
            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(newPath, currentEntry);
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte[] data = new byte[buffer];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {

                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data, 0, buffer)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                        is.close();
                    }
                }

                if (currentEntry.endsWith(".zip")) {
                    // found a zip file, try to open
                    extractFolder(destFile.getAbsolutePath());
                }
            }
        }
    }



    static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {

            if(filename.contains(".zip")){
                Toast.makeText(context,context.getExternalFilesDir(null).getAbsolutePath() +"/"+filename,Toast.LENGTH_LONG).show();

                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);

                    String outDir = context.getExternalFilesDir(null).getAbsolutePath() + "" ;

                    File outFile = new File(outDir, filename);

                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
            }
        }
    }
    static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}