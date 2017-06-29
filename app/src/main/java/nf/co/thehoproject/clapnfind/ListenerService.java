package nf.co.thehoproject.clapnfind;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.rollbar.android.Rollbar;

import java.io.IOException;
import java.util.Date;

import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.onsets.OnsetHandler;
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector;
import nf.co.thehoproject.clapnfind.MusicG.DetectorThread;

/**
 * Created by shivesh on 7/10/16.
 */
public class ListenerService extends Service {




    private byte[] buffer;
    private AudioRecord recorder;
    private boolean mIsRecording;
    private PercussionOnsetDetector mPercussionOnsetDetector;
    private be.hogent.tarsos.dsp.AudioFormat tarsosFormat;
    private int clap;
    static   int SAMPLE_RATE = 8000;
    DetectorThread detectorThread;


    public static Notification myNotication;
    public static NotificationManager manager;
    public static Context ctx;

    static Intent cur;
    public static MediaPlayer mediaPlayer;
    public static Ringtone r;
    @Override
    public void onCreate() {
        super.onCreate();
        if (cur != null) {
            String action = cur.getAction();
            if(action!=null)
                if(action.equals(ACTION_STOP))
                {
                    stopSelf();
                    return;
                }
        }
        ctx=this;
        Conf.load();;
        Conf.ConfS cz=Conf.eLoad();

        mediaPlayer=new MediaPlayer();
        try {
            Uri ur=Uri.parse(Conf.AUDIO);



            if(Conf.AUDIO.length()<5)
            {
               Conf.AUDIO=cz.paths.get(0);
                Conf.save();
            }
            //Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //mediaPlayer.setDataSource(ctx,defaultRingtoneUri);
            ur=Uri.parse(Conf.AUDIO);
            mediaPlayer.setDataSource(ctx,ur);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    utl.log("Pla STRT");
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(cf.REPEAT&&mediaPlayer.getDuration()>1 )
                    {
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        try {
            SAMPLE_RATE=getValidSampleRates();
            Log.d("SAMPL",""+SAMPLE_RATE);
        } catch (Exception e) {

            e.printStackTrace();
        }
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r= RingtoneManager.getRingtone(getApplicationContext(), notification);



        try {
            ads();



            Home.Power_Off.setVisibility(View.VISIBLE);
            Home.Power_On.setVisibility(View.GONE);
            Home.act.moveTaskToBack(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notif();
        //postNotif("");

    }


    int minBufferSize;

    int i=0;
    Long c1=0L,c2=0L;
    Handler h;
    public void ads()
    {
        h=new Handler();

        rx=new Runnable() {
            @Override
            public void run() {

                if(clap==1)
                    clap=0;
            }
        };
        handler=new OnsetHandler() {
            @Override
            public void handleOnset(double time, double salience) {
                {

                    System.out.println(String.format("%.4f;%.4f", time, salience));
                    clap += 1;

                    // have we detected a pitch?


                    if(clap==1)
                        c1=System.currentTimeMillis();

                    if(clap==2)
                        c2=System.currentTimeMillis();
                    Log.d("CLAP","No of Claps : "+clap+"\n AT : C1 :"+c1+"\nC2 : "+c2);



                    if(clap==2) {
                        Long diff = c2 - c1;
                        Log.e("CLAP", "DIFF : " + diff);

                        clap = 0;
                        if(diff>=0&&diff<=Conf.DELAY)
                        {


                            checkAndRing();


                        }
                        else {
                        }
                        c1 = 0L;
                        c2 = 0L;

                        h.removeCallbacks(rx);
                        clap=0;
                    }



                    if(clap==1)
                    h.postDelayed(rx,Conf.DELAY);


                }
            }
        };
        Log.d("LISTENNING ","LISTENNING AS");
// set recording boolean to false
        mIsRecording = true;
        clap = 0;
        // STEP 1: setup AudioRecord
        minBufferSize= AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        buffer = new byte[minBufferSize];

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);
        // END STEP 1

        // STEP 2: create detector

/*
         mh=new Handler();
          rr=new Runnable() {
            @Override
            public void run() {

                Log.d("Iteration",""+i);
                mh.postDelayed(rr,2000);

            }
        };
         mh.postDelayed(rr,2000);
        */ mPercussionOnsetDetector = new PercussionOnsetDetector(SAMPLE_RATE,
                (minBufferSize / 2),handler , Conf.SENSITIVITY, Conf.THRESHOLD);

        Log.d("CONFIG IS","SR "+Conf.SENSITIVITY+"\nTH"+Conf.THRESHOLD);
        if(recorder!=null)
        listen();
        else {
            Log.d("rec","REcorder is null");
        }

    }



    public static boolean  DISABLE_WHEN_IN_CALL=false;
    public static boolean DISABLE_WHEN_SCREEN_ON=false;
    public static boolean REPEAT=false;
    public static boolean LOUD_SPEAKER_WHEN_HEADPHONE=false;


    public boolean isScreenOn()
    {
         PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean result= Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT_WATCH&&
                powerManager.isInteractive()|| Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT_WATCH&&
                powerManager.isScreenOn();
        return result;


    }



    public boolean isCallOn()
    {
        AudioManager manager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }
        else{
            return false;
        }


    }
   public static Conf.ConfS cf;
    public boolean isREPEAT()
    {
        boolean on=false;

         return cf.REPEAT;


    }



    public boolean isHeadphoneConnected()
    {
        boolean on=false;

        AudioManager a = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        boolean headphones = (a.getRouting(a.getMode()) & AudioManager.ROUTE_HEADSET) == AudioManager.ROUTE_HEADSET;

        return headphones;


    }






    public void checkAndRing(){


       // if(cf==null)
        cf =Conf.eLoad();

        boolean defRing=true;
        if((isScreenOn()&&cf.SCREEN_DISABLE)||(isCallOn()&&cf.CALL_DISABLE))
        {defRing=false;

            utl.log("DEF RING=FALSE");
        }
       else if(isHeadphoneConnected()&&cf.HEADPHONE_BEEP)
        {
            AudioManager audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.STREAM_MUSIC);
            audioManager.setSpeakerphoneOn(true);

            if(!  mediaPlayer.isPlaying()){
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


                defRing=true;
            }
        }
        if(cf.REPEAT )
        {
                  utl.log("REPEAT SET  "+defRing );

        }

        if(defRing){
            try {
                ring();
            } catch (Exception e) {
                e.printStackTrace();
            }
            utl.log("RINGING" );
        }

    }





    public void ring()
    {

        try {

            if(Conf.AUDIO.equals("-")&&false)
            {
                if(r.isPlaying())
                {
                    r.stop();
                }
               r.play();
            }
            else {

                try {
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();

                  //  mediaPlayer.release();
                    ;
                   // mediaPlayer.reset();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                Uri ur=Uri.parse(Conf.AUDIO);
               // mediaPlayer=new MediaPlayer();

//                mediaPlayer.setDataSource(ctx,ur);
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                    mediaPlayer=new MediaPlayer();

                    Log.d("SR",""+ur.toString());
        mediaPlayer.setDataSource(ctx,ur);
                    mediaPlayer.prepare();

                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        utl.log("Pla STRT");
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(cf.REPEAT&&mediaPlayer.getDuration()>1 )
                        {
                            try {
                                mediaPlayer.start();
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });


                utl.log("REPEATING PLA START " + mediaPlayer.isLooping());
               /* if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    Uri ur=Uri.parse(Conf.AUDIO);
                    mediaPlayer.setDataSource(ctx,ur);



                    mediaPlayer.prepare();
                } else {
                    try {

                        Log.d("SRC", "" + Conf.AUDIO);
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }






    OnsetHandler handler;
    Runnable rx;
    public void listen() {
        recorder.startRecording();

        tarsosFormat = new be.hogent.tarsos.dsp.AudioFormat(
                (float) SAMPLE_RATE, // sample rate
                16, // bit depth
                1, // channels
                true, // signed samples?
                false // big endian?
        );

        Thread listeningThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (mIsRecording) try {
                    {
                        int bufferReadResult = recorder.read(buffer, 0,
                                buffer.length);
                        AudioEvent audioEvent = new AudioEvent(tarsosFormat,
                                bufferReadResult);
                        audioEvent.setFloatBufferWithByteBuffer(buffer);
                        mPercussionOnsetDetector.process(audioEvent);

                    }
                } catch (Exception e) {

                    mPercussionOnsetDetector = new PercussionOnsetDetector(SAMPLE_RATE,
                            (minBufferSize / 2), handler, Conf.SENSITIVITY,Conf.THRESHOLD);


                    Rollbar.reportMessage("ERR RECORDING", ""+e.getStackTrace().toString());
                    e.printStackTrace();
                }
                recorder.stop();
                System.out.println("recorder stopped");
            }

        });

        listeningThread.start();

        Log.d("LISTENNING ","LISTENNING STARTED");

    }
    Handler mh;
    Runnable rr;
    @Override
    public void onDestroy() {



        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        mIsRecording=false;
        try {
            /*mPercussionOnsetDetector = new PercussionOnsetDetector(SAMPLE_RATE,
                    (minBufferSize / 2), null, Conf.SENSITIVITY,Conf.THRESHOLD);
*/
            try {
                mPercussionOnsetDetector.processingFinished();
                mPercussionOnsetDetector.setHandler(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(detectorThread!=null) {


    if(recorder!=null)
                recorder.stop();

                detectorThread.stopDetection();
                Log.d("FINALLY","DETECTOR STOPPED");
            }
            Log.d("FINALLY","FINALLY SERVICE DESTROYED");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {


            Home.tog.setState(false);
            Home.txt.setText("Listening Stopped ! !");



            mNotifiManager.cancel(NOTIFICATION_DEFAULT);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        super.onDestroy();
    }




    public static class  ListenReciever extends BroadcastReceiver {
        private final String TAG = "ListenReciever";

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals("nf.co.thehoproject.clapnfind.Home")){
                //action for sms received

                Log.d("NOt","SSTOpped Service");
                ctx.stopService(cur);

            }
            else if(action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
                //action for phone state changed
            }
        }
    }



    public   final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("nf.co.thehoproject.clapnfind.Home")){
                //action for sms received
                unregisterReceiver(this);
                Log.d("NOt","SSTOpped Service");
                stopService(cur);

            }
            else if(action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
                //action for phone state changed
            }
        }
    };

    ListenReciever recv;
    private void postNotif(String notifString) {


        Intent intent = new Intent("nf.co.thehoproject.clapnfind.Home");

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(ctx);

        builder.setAutoCancel(false);
        builder.setTicker("Listening claps...");
        builder.setContentTitle("Listening to claps..");
         builder.setContentText("Click to Stop LISTENING");
        builder.setSmallIcon(R.drawable.ring);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("");   //API level 16
       // builder.setNumber(100);
        builder.build();

        myNotication = builder.getNotification();
        manager.notify(11, myNotication);

        IntentFilter filter = new IntentFilter();
        filter.addAction("nf.co.thehoproject.clapnfind.Home");
         filter.addAction("nf.co.thehoproject.clapnfind.STOP"); //further more


        recv=new ListenReciever();
        registerReceiver(recv,filter);
Log.d("NOt","REGISTERED RECIEVER");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getValidSampleRates() throws Exception {


        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rates = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer  ", "Size :" + size + " & Rate: " + rates);

        for (int rate : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported


                return  rate;
            }
        }
        return Integer.valueOf(rates);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        cur=intent;
        if (intent != null) {
            String action = intent.getAction();
            if(action!=null)
            if(action.equals(ACTION_STOP))
            {

                try {
                    if(Home.ctx!=null&&Home.i!=null)
                    {


                        Home.ctx.stopService(Home.i);
                        Log.d("FINALLY","DONE FROM er Home.ctx");

                       // stopSelf();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
            return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }











    public static String ACTION_STOP="nf.co.thehoproject.clapnfind.ACTION_STOP";
    public static int NOTIFICATION_DEFAULT=12221,REQUEST_CODE_STOP=2113;
    public static Notification mNotification;
    public static NotificationManager mNotifiManager;
    public static RemoteViews mRemoteViews;
    public static   void notif()
    {
        if (mNotifiManager != null) {
            mNotifiManager.cancel(NOTIFICATION_DEFAULT);
        }
            Resources res=ctx.getResources();
        Bitmap largeIconTemp = BitmapFactory.decodeResource(res,
                R.drawable.clap);
        Bitmap largeIcon = Bitmap.createScaledBitmap(
                largeIconTemp,
                res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),false);
        largeIconTemp.recycle();




            if (mRemoteViews == null) {
                mRemoteViews = new RemoteViews(ctx.getPackageName(), R.layout.notification_player);
            }


            PendingIntent pendingIntentPP = null,pendingIntentN= null,pendingIntentS = null;

            Intent stopIn = null,ppIn=null,nextIn=null;

            stopIn = new Intent(ACTION_STOP);
            pendingIntentS = PendingIntent.getService(ctx.getApplicationContext(),
                    REQUEST_CODE_STOP, stopIn,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.not,
                    pendingIntentS);


/*
            ppIn = new Intent(ACTION_PLAY);
            pendingIntentPP = PendingIntent.getService(ctx.getApplicationContext(),
                    REQUEST_CODE_STOP, ppIn,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.ppN,
                    pendingIntentPP);


            nextIn = new Intent(ACTION_NEXT);
            pendingIntentN = PendingIntent.getService(ctx.getApplicationContext(),
                    REQUEST_CODE_STOP, nextIn,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.nextN,
                    pendingIntentN);

            Intent bring = new Intent(ACTION_PAUSE);
            PendingIntent pendingIntentB = PendingIntent.getService(ctx.getApplicationContext(),
                    REQUEST_CODE_STOP, bring,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.sub,pendingIntentB);/*,
                    pendingIntentB);
            mRemoteViews.setOnClickPendingIntent(R.id.titleN,
                    pendingIntentB);
            mRemoteViews.setOnClickPendingIntent(R.id.subtitleN,
                    pendingIntentB);*/




            mNotification = new NotificationCompat.Builder(ctx.getApplicationContext())
                    .setSmallIcon(R.drawable.ring).setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setContent(mRemoteViews)
                    .build();

            //Show the notification in the notification bar.
            mNotifiManager  =(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            mNotifiManager.notify(NOTIFICATION_DEFAULT, mNotification);
            Log.d("","NOTIFIED NEXT");




    }









}
