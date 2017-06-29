package nf.co.thehoproject.clapnfind;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by shivesh on 6/6/16.
 */
public class ExecuterU extends AsyncTask<Void,Void,Void> {


    interface callBack{

        void doInBackground(int progress);
        void doPostExecute();

    }

    callBack b;
    int prog;
    ProgressDialog pd;
    Context ctx;
    String message,folder;
    Runnable r;

    public ExecuterU( Context ctx, String message)
    {
        this.ctx=ctx;
        this.r=r;
        this.message=message;
    }

   @Override
   protected void onPreExecute()
   {
       super.onPreExecute();
       folder= Environment.getExternalStorageDirectory().getPath().toString()+"/vidmo";

       pd=new ProgressDialog(ctx);
       pd.setMessage(message);
       pd.setCancelable(false);
       pd.setCanceledOnTouchOutside(false);
//       pd.show();
   }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
      //  pd.dismiss();
        doNe();

    }

    public void doIt()
    {

    }



    public void doNe()
    {

    }


    @Override
    protected Void doInBackground(Void... voids) {


try {
    doIt();
}catch (Exception e)
{
    Log.d("Exce","While loaovelayingding bitmaps");
    e.printStackTrace();
}
        return null;
    }












    /******************************************/
     public String TARGET_BASE_PATH = "/sdcard/appname/voices/";
    private void copyFilesToSdCard() {
        copyFileOrDir(""); // copy all files in assets folder in myproject
    }
    private void copyFileOrDir(String path) {
        AssetManager assetManager = ctx.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  TARGET_BASE_PATH + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !
                        path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";
                    if (!path.startsWith("images") && !path.startsWith
                            ("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }
    private void copyFile(String filename) {
        AssetManager assetManager = ctx.getAssets();
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
            newFileName = TARGET_BASE_PATH +
                    filename.substring(0, filename.length()-4);
            else
            newFileName = TARGET_BASE_PATH + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }
    }
}
