package nf.co.thehoproject.clapnfind;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Conf {


    public static String FIREBASE_URL="https://shiveshnavin.firebaseio.com/";


    public static Long DELAY=2000L;
    public static Long THRESHOLD=4L;
    public static Long SENSITIVITY=24L;
    public static String AUDIO="-";



    public static String PASSWORD="-";
    public static boolean SCREEN_DISABLE=false;
    public static boolean CALL_DISABLE=false;
    public static boolean REPEAT=false;
    public static boolean HEADPHONE_BEEP=false;

    public static String conf;



    public static class ConfS
    {
        public   Long DELAY=2000L;
        public   Long THRESHOLD=4L;
        public   Long SENSITIVITY=24L;
        public   String AUDIO="-";



        public   String PASSWORD="-";
        public   boolean PASSWORD_ACTIVE=false;
        public   boolean SCREEN_DISABLE=false;
        public   boolean CALL_DISABLE=false;
        public   boolean REPEAT=false;
        public   boolean HEADPHONE_BEEP=false;
        public   String conf;

        ArrayList<String> paths;
        public ConfS()
        {

            final String path= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/";

            paths=new ArrayList<>();
                paths.add(path+ "mp3/alarm.mp3");
                paths.add(path+ "mp3/birds.mp3");
                paths.add(path+ "mp3/cat.mp3");
                paths.add(path+ "mp3/click_1.mp3");
                paths.add(path+ "mp3/dog.mp3");
                paths.add(path+ "mp3/noise.mp3");
                paths.add(path+ "mp3/police.mp3");
                paths.add(path+ "mp3/rooster.mp3");
        }




    }

    public static ConfS eLoad()
    {
        ConfS cf=new ConfS();

        conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2fe.conf";


        Gson job=new Gson();
        FileOperations fop=new FileOperations();
        if(!(new File(conf).exists()))
        {
            try {

                fop.write(conf,job.toJson(cf));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        cf=job.fromJson(fop.read(conf),ConfS.class);
        utl.log("READ E\n"+fop.read(conf));
         ;/*
        DELAY=cf.DELAY;
        THRESHOLD=cf.THRESHOLD;
        SENSITIVITY=cf.SENSITIVITY;
        AUDIO=cf.AUDIO;

*/
        PASSWORD =cf.PASSWORD;
        SCREEN_DISABLE=cf.SCREEN_DISABLE;
        CALL_DISABLE=cf.CALL_DISABLE;
        REPEAT=cf.REPEAT;
        HEADPHONE_BEEP=cf.HEADPHONE_BEEP;
        return cf;


    }


    public static void eSave( ConfS cf)
    {
        conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2fe.conf";


        Gson job=new Gson();
        FileOperations fop=new FileOperations();
            try {

                fop.write(conf,job.toJson(cf));

                utl.log("WROTE E\n"+fop.read(conf));
            } catch (Exception e) {
                e.printStackTrace();
            }



    }







public static void load()
{
    conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2f.conf";
    FileOperations fop=new FileOperations();
    if(!(new File(conf).exists()))
    {
        try {
            JSONObject job=new JSONObject();
            Log.d("LOADED",""+job.toString());
            job.put("DELAY",DELAY);
            job.put("THRESHOLD",THRESHOLD);
            job.put("SENSITIVITY",SENSITIVITY);
            job.put("AUDIO",AUDIO);
            fop.write(conf,job.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    try {
        JSONObject job=new JSONObject(fop.read(conf));
        Log.d("LOADED",""+job.toString());
        DELAY=job.getLong("DELAY");
        THRESHOLD=job.getLong("THRESHOLD");
        SENSITIVITY=job.getLong("SENSITIVITY");
        AUDIO=job.getString("AUDIO");




    } catch (JSONException e) {
        e.printStackTrace();
    }

}


    public static void save(Long DELAY,Long THRESHOLD,Long SENSITIVITY)
    {
        conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2f.conf";
        FileOperations fop=new FileOperations();
        try {
            JSONObject job=new JSONObject();
            Log.d("LOADED",""+job.toString());
            job.put("DELAY",DELAY);
            job.put("THRESHOLD",THRESHOLD);
            job.put("SENSITIVITY",SENSITIVITY);
            job.put("AUDIO",AUDIO);
            fop.write(conf,job.toString());




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public static void save(Long DELAY,Long THRESHOLD,Long SENSITIVITY,String AUDIO)
    {
        conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2f.conf";
        FileOperations fop=new FileOperations();
        try {
            JSONObject job=new JSONObject();
            Log.d("LOADED",""+job.toString());
            job.put("DELAY",DELAY);
            job.put("THRESHOLD",THRESHOLD);
            job.put("SENSITIVITY",SENSITIVITY);
            job.put("AUDIO",AUDIO);
            fop.write(conf,job.toString());




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    public static void save()
    {
        conf= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/.clap2f.conf";
        FileOperations fop=new FileOperations();
        try {
            JSONObject job=new JSONObject();
            Log.d("LOADED",""+job.toString());
            job.put("DELAY",DELAY);
            job.put("THRESHOLD",THRESHOLD);
            job.put("SENSITIVITY",SENSITIVITY);
            job.put("AUDIO",AUDIO);
            fop.write(conf,job.toString());




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }















    public static class Stat
    {

        String app="";
        String user="";
        String status="";

    }



}
