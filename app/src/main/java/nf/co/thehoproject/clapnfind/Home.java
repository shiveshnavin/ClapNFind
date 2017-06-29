package nf.co.thehoproject.clapnfind;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.leaking.slideswitch.SlideSwitch;
import com.rollbar.android.Rollbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CheckedInputStream;

import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.onsets.OnsetHandler;
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector;
import butterknife.Bind;
import butterknife.ButterKnife;
import nf.co.thehoproject.clapnfind.MusicG.DetectorThread;
import nf.co.thehoproject.clapnfind.MusicG.OnSignalsDetectedListener;
import nf.co.thehoproject.clapnfind.MusicG.RecorderThread;

public class Home extends AppCompatActivity implements OnsetHandler {




      public static ImageView Power_Off ;
     public static ImageView Power_On ;
    @Bind(R.id.Change_Ringtone) ImageView Change_Ringtone ;
    @Bind(R.id.Screen) ImageView Screen ;
    @Bind(R.id.Password) ImageView Password ;
    @Bind(R.id.Call) ImageView Call ;
    @Bind(R.id.Repeat_Song) ImageView Repeat_Song ;
    @Bind(R.id.HeadPhone) ImageView HeadPhone ;
    @Bind(R.id.Reset) ImageView Reset;

    @Bind(R.id.old)  View old;
    @Bind(R.id.neww)  View neww;

    Conf.ConfS cf;
    Intent opt;

    public int SENSITIVITY=24,THRESHOLD=5;


    private byte[] buffer;
    private AudioRecord recorder;
    private boolean mIsRecording;
    private PercussionOnsetDetector mPercussionOnsetDetector;
    private be.hogent.tarsos.dsp.AudioFormat tarsosFormat;
    private int clap;
    static   int SAMPLE_RATE = 8000;
    DetectorThread detectorThread;


    private Button mListenButton;
    private Button audio;
    private TextView hint;
    private Bitmap bitmap;

    public static Activity act;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static Context ctx;
    final int validSampleRates[] = new int[] { 8000, 11025, 16000, 22050,
            32000, 37800, 44056, 44100, 47250, 4800, 50000, 50400, 88200,
            96000, 176400, 192000, 352800, 2822400, 5644800 };

    RecorderThread recorderThread;
    View ll;
    boolean working =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

if(!isNetworkAvailable()&&false)
{
    Intent iz=new Intent(getBaseContext(),Error.class);
    iz.putExtra("message","Must Have an Internet connection to work");
    startActivity(iz);
    finish();;
}
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_home);
        try {
            act=this;
            ctx=getApplicationContext();
            Rollbar.init(getApplicationContext(), "5158971ae2bd440ab622fce2c013404d", "production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ButterKnife.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        opt=new Intent(ctx,Options.class);


         String app=getResources().getString(R.string.app_name).replace(" ","").toLowerCase()+"v1";





        ll=findViewById(R.id.ll);
        AndroidNetworking.initialize(ctx);
        AndroidNetworking.get(getString(R.string.server)+"status.php?q="+app+
                "&u="+ URLEncoder.encode(getUser())+"&t="+System.currentTimeMillis()).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("re",""+response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        Log.d("re",""+ANError.toString());
                    }
                });
/*
Identically Clap Loudly two times roughly with interval of 0.3-0.5 sec in between"
    bulletSwitch.setState(true);
 */

        Power_Off=(ImageView) findViewById(R.id.Power_Off);
        Power_On=(ImageView)findViewById(R.id.Power_On);



        btn=(Button)findViewById(R.id.button);
        audio=(Button)findViewById(R.id.audio);
         txt=(TextView)findViewById(R.id.textView);
        hint=(TextView)findViewById(R.id.hint);
        final String pa = Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind";
        File f=new File(pa);
        if(!f.exists())
        {
            f.mkdir();
        }
        final String pa2 = Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/mp3";
        f=new File(pa2);
        if(!f.exists())
        copyFileOrDir("mp3");
        Conf.load();
        cf=Conf.eLoad();
        if(cf.PASSWORD_ACTIVE)
            getPhone();

        ol();
        ne();
    }
   public static Intent i;
    public static SlideSwitch tog;
    public String getUser()
    {
        try{
            AccountManager man=AccountManager.get(this);
            Account[] acc=man.getAccountsByType("com.google");
            List<String> pos=new LinkedList<String>();
            for(Account a:acc)
            {
                pos.add(a.name);

            }

            if(!pos.isEmpty()&&pos.get(0)!=null)
            {
                String em=pos.get(0);
                return em;
            }}catch (Exception e)
        {
            e.printStackTrace();
        }
        return "null";

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();

                Conf.AUDIO=uri.toString();
                Log.d("uri",""+Conf.AUDIO);
                Conf.save();
                audio.setText(uri.getPath());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public static void set()
    {
        Conf.load();
    }


    public void ol()
    {  neww.setVisibility(View.GONE);
        old.setVisibility(View.VISIBLE);



        if(Conf.AUDIO.equals("-"))
            audio.setText("Select Audio");
        else
            audio.setText(""+Conf.AUDIO);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);

            }
        });
        hint.setText(Html.fromHtml("<b>HINT</b><br>" +
                "Identically Clap with Loudness <b>"+Conf.THRESHOLD+"/10</b><br><b>Max</b> Delay beetween 2 claps <b>"
                +Conf.DELAY+"ms</b> . Sensetivity is <b>"+Conf.SENSITIVITY+"/40</b>"));


        // txt.setText("Listening Stopped !");
        tog=(SlideSwitch) findViewById(R.id.tog);
        if(!isMyServiceRunning(ListenerService.class))
        {

            tog.setState(false);
            txt.setText("Listening Stopped ! !");
            ll.setEnabled(true);
        }
        else {
            tog.setState(true);
        }
        try {
            SAMPLE_RATE=getValidSampleRates();
            Log.d("SAMPL",""+SAMPLE_RATE);
        } catch (Exception e) {

            Rollbar.reportMessage("ERR SAMPLING", ""+e.getStackTrace().toString());
            e.printStackTrace();
        }

        i=new Intent(this,ListenerService.class);


        tog.setSlideListener(new SlideSwitch.SlideListener() {

            @Override
            public void close() {
                //
                btn.setText("Start");
                ctx.stopService(i);
                txt.setText("Stopped !");


                ll.setEnabled(true);



            }

            @Override
            public void open() {
                // Do something ,,,

                if(!isMyServiceRunning(ListenerService.class))
                {
                    btn.setText("Stop");
                    i.putExtra("ss", "start");
                    txt.setText("Listening...");
                    ctx.startService(i);

                    ll.setEnabled(false);
                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r= RingtoneManager.getRingtone(getApplicationContext(), notification);
/*

        try {
            ads();
        } catch (Exception e) {
            e.printStackTrace();
        }

*/
        thr();
        dela();
        sens();



    }

    public void ne() {

        neww.setVisibility(View.VISIBLE);
        old.setVisibility(View.GONE);
        final String path = Environment.getExternalStorageDirectory().getPath().toString() + "/.clapnfind";
        File fp = new File(path);

        if (!new File(path).exists() || fp.listFiles().length < 7) {

            if (!new File(path).exists())
                new File(path).mkdir();


            Power_Off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    btn.setText("Start");
                    ctx.stopService(i);
                    txt.setText("Stopped !");
                    Power_Off.setVisibility(View.GONE);
                    Power_On.setVisibility(View.VISIBLE);


                }
            });


            Power_On.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isMyServiceRunning(ListenerService.class)) {
                        btn.setText("Stop");
                        i.putExtra("ss", "start");
                        txt.setText("Listening...");
                        ctx.startService(i);

                    }
                }
            });

            Change_Ringtone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "Change_Ringtone");
                    startActivity(new Intent(Home.this, Tones.class));

                }
            });

            Screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "Screen");
                    startActivity(opt);

                }
            });

            Password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "Password");
                    startActivity(opt);

                }
            });

            Call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "Call");
                    startActivity(opt);

                }
            });

            Repeat_Song.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "Repeat_Song");
                    startActivity(opt);

                }
            });

            HeadPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opt.putExtra("act", "HeadPhone");
                    startActivity(opt);

                }
            });

            Reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cf = new Conf.ConfS();
                    Conf.eSave(cf);

                    Conf.DELAY = 2000L;
                    Conf.THRESHOLD = 4L;
                    Conf.SENSITIVITY = 24L;
                    Conf.AUDIO = "-";
                    Conf.save();
                    snack("Restart app to see changes");

                }
            });


        }
    }


    public void iniNew()
    {

        cf=Conf.eLoad();


        if(isMyServiceRunning(ListenerService.class))
        {
            Power_Off.setVisibility(View.VISIBLE);
            Power_On.setVisibility(View.GONE);
        }
        else {

            Power_Off.setVisibility(View.GONE);
            Power_On.setVisibility(View.VISIBLE);
        }

        if(cf.PASSWORD_ACTIVE)
        {
            Password.setImageResource(R.drawable.password_on);
            // HeadPhone.setEnabled(true);
        }
        else {
            Password.setImageResource(R.drawable.password_off);
            //  HeadPhone.setEnabled(false);
        }

        if(cf.HEADPHONE_BEEP)
        {
            HeadPhone.setImageResource(R.drawable.headphone_on);
           // HeadPhone.setEnabled(true);
        }
        else {
            HeadPhone.setImageResource(R.drawable.headphone_off);
          //  HeadPhone.setEnabled(false);
        }

        if(cf.REPEAT)
        {Repeat_Song.setImageResource(R.drawable.repeat_song_on);
           // Repeat_Song.setEnabled(true);
        }
        else {
            Repeat_Song.setImageResource(R.drawable.repeat_song_off);
           // Repeat_Song.setEnabled(false);
        }

        if(cf.CALL_DISABLE)
        {Call.setImageResource(R.drawable.call_on);
           // Call.setEnabled(true);
        }
        else {Call.setImageResource(R.drawable.call_off);


            // Call.setEnabled(false);
        }


        if(cf.SCREEN_DISABLE)
        {Screen.setImageResource(R.drawable.screen_on);
           // Screen.setEnabled(true);
        }
        else {
            Screen.setImageResource(R.drawable.screen_off);
           // Screen.setEnabled(false);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //respond to menu item selection
        int id=item.getItemId();
        if(id==R.id.set)
        {

            ol();
        }
        if(id==R.id.rate)
        {

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=com.mezitech.clap.find"));

            // i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
        }
        if(false)
        {

            Conf.DELAY=2000L;
            Conf.THRESHOLD=4L;
            Conf.SENSITIVITY=24L;
            Conf.AUDIO="-";
            Conf.save();
            snack("Restart app to see changes");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        tog=(SlideSwitch) findViewById(R.id.tog);
        if(!isMyServiceRunning(ListenerService.class))
        {

            tog.setState(false);
            txt.setText("Listening Stopped ! !");
            ll.setEnabled(true);
        }
        iniNew();
    }

    public void detect()
    {
        try {
            Clapper clapper=new Clapper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ads()
    {


// set recording boolean to false
        mIsRecording = false;
        clap = 0;


        // button to initialize call back
        mListenButton = (Button) findViewById(R.id.button);
        mListenButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    mListenButton.setText("Listen");
                    txt.setText("Listening Stopped !");
                    mIsRecording = false;
                } else {

                    txt.setText("Listening...");
                    mListenButton.setText("Stop listening");
                    mIsRecording = true;
                    // STEP 5: start recording and detect clap
                    listen();
                    // END STEP 5
                }
            }
        });

        // STEP 1: setup AudioRecord
        minBufferSize= AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        buffer = new byte[minBufferSize];

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);
        // END STEP 1

        // STEP 2: create detector

        mPercussionOnsetDetector = new PercussionOnsetDetector(SAMPLE_RATE,
                (minBufferSize / 2), this, SENSITIVITY,THRESHOLD);


    }
    int minBufferSize;
    public static TextView txt;
    Button btn;
    public void prepareMusicAndListenForClaps(){
        try {
              detectorThread.setOnSignalsDetectedListener(new OnSignalsDetectedListener() {
                public void onClapDetected() {
                    //detectorThread.stopDetection();
                    detectorThread.setOnSignalsDetectedListener(null);

                    txt.setText("2x Clapped Sensed...");
                    working =false;

                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {

//        detectorThread.stopDetection();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");

        if(neww.getVisibility()==View.VISIBLE)
        moveTaskToBack (true);
        else if(neww.getVisibility()==View.GONE||old.getVisibility()==View.VISIBLE)
        {

            neww.setVisibility(View.VISIBLE);
            old.setVisibility(View.GONE);
            iniNew();
        }

        /*
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);*/
    }









    // STEP 3: handle Onset event
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getValidSampleRates() {


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

    Long c1,c2;
    @Override
    public void handleOnset(double time, double salience) {

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
            c1 = 0L;
            c2 = 0L;
        if(diff>=250&&diff<=600)
        {


            runOnUiThread(new Runnable() {
            @Override
            public void run() {


                txt.setText("2 Claps Sensed...");
                try {
                    if(r.isPlaying())
                    {
                        r.stop();
                    }
                      r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        }

        }




        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler h=new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        txt.setText("Listening...");
                        if(clap==1)
                       clap=0;
                    }
                },1000);
            }
        });

    }
    Ringtone r;
    // END STEP 3

    // STEP 4: Setup recording
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
                            (minBufferSize / 2), Home.this, SENSITIVITY,THRESHOLD);


                    Rollbar.reportMessage("ERR RECORDING", ""+e.getStackTrace().toString());
                    e.printStackTrace();
                }
                recorder.stop();
                System.out.println("recorder stopped");
            }

        });

        listeningThread.start();

    }

    // END STEP 4
    public class Clapper
    {
        private static final String TAG = "Clapper";

        private static final long DEFAULT_CLIP_TIME = 1000;
        private long clipTime = DEFAULT_CLIP_TIME;
        private AmplitudeClipListener clipListener;

        private boolean continueRecording;

        /**
         * how much louder is required to hear a clap 10000, 18000, 25000 are good
         * values
         */
        private int amplitudeThreshold;

        /**
         * requires a little of noise by the user to trigger, background noise may
         * trigger it
         */
        public static final int AMPLITUDE_DIFF_LOW = 10000;
        public static final int AMPLITUDE_DIFF_MED = 18000;
        /**
         * requires a lot of noise by the user to trigger. background noise isn't
         * likely to be this loud
         */
        public static final int AMPLITUDE_DIFF_HIGH = 25000;

        private static final int DEFAULT_AMPLITUDE_DIFF = AMPLITUDE_DIFF_MED;

        private MediaRecorder recorder;

        private String tmpAudioFile;

        public Clapper() throws IOException
        {
            this(DEFAULT_CLIP_TIME, "/tmp.3gp", DEFAULT_AMPLITUDE_DIFF, null, null);
        }

        public Clapper(long snipTime, String tmpAudioFile,
                       int amplitudeDifference, Context context, AmplitudeClipListener clipListener)
                throws IOException
        {
            this.clipTime = snipTime;
            this.clipListener = clipListener;
            this.amplitudeThreshold = amplitudeDifference;
            this.tmpAudioFile = tmpAudioFile;
        }

        public boolean recordClap()
        {
            Log.d(TAG, "record clap");
            boolean clapDetected = false;

            try
            {
             //   recorder = AudioUtil.prepareRecorder(tmpAudioFile);
            }
            catch (Exception io)
            {
                Log.d(TAG, "failed to prepare recorder ", io);

            }

            recorder.start();
            int startAmplitude = recorder.getMaxAmplitude();
            Log.d(TAG, "starting amplitude: " + startAmplitude);

            do
            {
                Log.d(TAG, "waiting while recording...");
                waitSome();
                int finishAmplitude = recorder.getMaxAmplitude();
                if (clipListener != null)
                {
                    clipListener.heard(finishAmplitude);
                }

                int ampDifference = finishAmplitude - startAmplitude;
                if (ampDifference >= amplitudeThreshold)
                {
                    Log.d(TAG, "heard a clap!");
                    clapDetected = true;
                }
                Log.d(TAG, "finishing amplitude: " + finishAmplitude + " diff: "
                        + ampDifference);
            } while (continueRecording || !clapDetected);

            Log.d(TAG, "stopped recording");
            done();

            return clapDetected;
        }

        private void waitSome()
        {
            try
            {
                // wait a while
                Thread.sleep(clipTime);
            } catch (InterruptedException e)
            {
                Log.d(TAG, "interrupted");
            }
        }

        /**
         * need to call this when completely done with recording
         */
        public void done()
        {
            Log.d(TAG, "stop recording");
            if (recorder != null)
            {
                if (isRecording())
                {
                    stopRecording();
                }
                //now stop the media player
                recorder.stop();
                recorder.release();
            }
        }

        public boolean isRecording()
        {
            return continueRecording;
        }

        public void stopRecording()
        {
            continueRecording = false;
        }
    }



    public void thr()
    {
        final Long step = 1L;
        final Long max = 10L;
        final Long min = 0L;

// Ex :
        hint.setText(Html.fromHtml("<b>HINT</b><br>" +
                "Identically Clap with Loudness <b>"+Conf.THRESHOLD+"/10</b><br><b>Max</b> Delay beetween 2 claps <b>"
                +Conf.DELAY+"ms</b> . Sensetivity is <b>"+Conf.SENSITIVITY+"/40</b>"));

// If you want values from 3 to 5 with a step of 0.1 (3, 3.1, 3.2, ..., 5)
// this means that you have 21 possible values in the seekbar.
// So the range of the seek bar will be [0 ; (5-3)/0.1 = 20].
        SeekBar seekbar=(SeekBar)findViewById(R.id.thre);
        seekbar.setMax( (int)((max - min) / step) );


        seekbar.setProgress( Conf.THRESHOLD.intValue());
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser)
                    {
                        // Ex :
                        // And finally when you want to retrieve the value in the range you
                        // wanted in the first place -> [3-5]
                        //
                        // if progress = 13 -> value = 3 + (13 * 0.1) = 4.3
                        Long value = min + (progress * step);
                        Log.d("Thres",""+value);
                        Conf.THRESHOLD=value;
                        Conf.save(Conf.DELAY,Conf.THRESHOLD,Conf.SENSITIVITY);
                        snack();


                        hint.setText(Html.fromHtml("<b>HINT</b><br>" +
                                "Identically Clap with Loudness <b>"+Conf.THRESHOLD+"/10</b><br><b>Max</b> Delay beetween 2 claps <b>"
                                +Conf.DELAY+"ms</b> . Sensetivity is <b>"+Conf.SENSITIVITY+"/40</b>"));

                    }
                }
        );
    }


    public void dela()
    {
        final Long step = 20L;
        final Long max = 2000L;
        final Long min = 0L;

// Ex :
// If you want values from 3 to 5 with a step of 0.1 (3, 3.1, 3.2, ..., 5)
// this means that you have 21 possible values in the seekbar.
// So the range of the seek bar will be [0 ; (5-3)/0.1 = 20].
        SeekBar seekbar=(SeekBar)findViewById(R.id.dela);
        seekbar.setMax( (int)((max - min) / step) );
        seekbar.setProgress( Conf.DELAY.intValue());

        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser)
                    {
                        // Ex :
                        // And finally when you want to retrieve the value in the range you
                        // wanted in the first place -> [3-5]
                        //
                        // if progress = 13 -> value = 3 + (13 * 0.1) = 4.3
                        Long value = min + (progress * step);
                        Log.d("dela",""+value);
                        Conf.DELAY=value;
                        Conf.save(Conf.DELAY,Conf.THRESHOLD,Conf.SENSITIVITY);
                        snack();

                        hint.setText(Html.fromHtml("<b>HINT</b><br>" +
                                "Identically Clap with Loudness <b>"+Conf.THRESHOLD+"/10</b><br><b>Max</b> Delay beetween 2 claps <b>"
                                +Conf.DELAY+"ms</b> . Sensetivity is <b>"+Conf.SENSITIVITY+"/40</b>"));



                    }
                }
        );
    }



    public void sens()
    {
        final Long step = 1L;
        final Long max = 40L;
        final Long min = 0L;

// Ex :
// If you want values from 3 to 5 with a step of 0.1 (3, 3.1, 3.2, ..., 5)
// this means that you have 21 possible values in the seekbar.
// So the range of the seek bar will be [0 ; (5-3)/0.1 = 20].
        SeekBar seekbar=(SeekBar)findViewById(R.id.sens);
        seekbar.setMax( (int)((max - min) / step) );


        seekbar.setProgress( Conf.SENSITIVITY.intValue());
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser)
                    {
                        // Ex :
                        // And finally when you want to retrieve the value in the range you
                        // wanted in the first place -> [3-5]
                        //
                        // if progress = 13 -> value = 3 + (13 * 0.1) = 4.3
                        Long value = min + (progress * step);
                        Log.d("sens",""+value);
                        Conf.SENSITIVITY=value;
                        Conf.save(Conf.DELAY,Conf.THRESHOLD,Conf.SENSITIVITY);

                        snack();


                        hint.setText(Html.fromHtml("<b>HINT</b><br>" +
                                "Identically Clap with Loudness <b>"+Conf.THRESHOLD+"/10</b><br><b>Max</b> Delay " +
                                "beetween 2 claps <b>"
                                +Conf.DELAY+"ms</b> . Sensetivity is <b>"+Conf.SENSITIVITY+"/40</b>"));


                    }
                }
        );


    }








public void snack()
{


    Handler z=new Handler();
    z.postDelayed(new Runnable() {
        @Override
        public void run() {
            Snackbar snackbar = Snackbar
                    .make(ll, "Restart Listening to See Changes", Snackbar.LENGTH_LONG);
           /*  .setAction("RESTART", new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                }
            });*/

// Changing message text color
            //snackbar.setActionTextColor(Color.RED);

// Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    },1500);

}




    public void snack(final String mess)
    {

                Snackbar snackbar = Snackbar
                        .make(ll, mess, Snackbar.LENGTH_LONG);
           /*  .setAction("RESTART", new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                }
            });*/

// Changing message text color
                //snackbar.setActionTextColor(Color.RED);

// Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();


    }
    private void copyFileOrDir(String path) {
        Log.e("COPYING","COPYING FILES");
        final String pa = Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind";
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = pa+ "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdir();
                for (int i = 0; i < assets.length; ++i) {
                    copyFileOrDir(path + "/" + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();
        final String pa = Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind";


        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = pa + "/" + filename;
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
            Log.e("tag", e.getMessage());
        }

    }

    Dialog dig ;
    public void getPhone()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Enter Password");
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_input);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                EditText button=(EditText)dig.findViewById(R.id.search_et);
                final String phone=button.getText().toString();

                if(phone.equals(cf.PASSWORD))
                {

                    dialog.dismiss();
                }
                else {
                    utl.toast(Home.this,"Wrong Password");
                    getPhone();;
                }



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                finish();
            }
        });
        dig = builder.create();
        dig.show();



    }

}
