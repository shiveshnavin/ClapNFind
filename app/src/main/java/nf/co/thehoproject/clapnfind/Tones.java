package nf.co.thehoproject.clapnfind;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tones extends AppCompatActivity {

    Context ctx;
    LinearLayout ll;
    Conf.ConfS cf;
    View pick;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_tones);
        ctx=this;
        ll=(LinearLayout)findViewById(R.id.ll);
        pick=findViewById(R.id.pick);
        Conf.load();

        cf=Conf.eLoad();
        mp=new MediaPlayer();


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);

            }
        });


        LayoutInflater lf=getLayoutInflater();
        for(int i=0;i<cf.paths.size();i++) {
           final View row =lf.inflate(R.layout.ring_row,ll,false);
            final int pos=i;
           final ImageView pp=(ImageView)row.findViewById(R.id.pp);
            TextView tx=(TextView)row.findViewById(R.id.tx);


                pp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            if(playing)
                        {
                            mp.stop();
                            mp.reset();
                            mp.release();

                            playing=false;
                            pp.setImageResource(R.drawable.start_ringtone_listen);
                        }
                        else {
                            Uri ur=Uri.parse(cf.paths.get(pos));
                            mp=MediaPlayer.create(ctx,ur);
                           mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                               @Override
                               public void onCompletion(MediaPlayer mediaPlayer) {
                                   pp.setImageResource(R.drawable.start_ringtone_listen);
                                   mp.stop();
                                   mp.reset();
                                   mp.release();
                                   playing=false;

                               }
                           });

                                playing=true;
                            mp.start();
                            pp.setImageResource(R.drawable.stop_ringtone_listen);
                        } } catch (Exception e) {
                            pp.setImageResource(R.drawable.start_ringtone_listen);
                        e.printStackTrace();
                    }
                    }
                });
            final String path= Environment.getExternalStorageDirectory().getPath().toString()+"/.clapnfind/";

            tx.setText(""+utl.refineString(cf.paths.get(pos).replace(path,"").replace("mp3"," ")," "));

            tx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(cf.paths.get(pos));

                    Conf.AUDIO=uri.toString();
                    Log.d("uri",""+Conf.AUDIO);
                    Conf.save();
                    utl.toast(ctx,"Picked : "+Conf.AUDIO);

                }
            });

            ll.addView(row);

        }











    }







public boolean playing=false;


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();

                Conf.AUDIO=uri.toString();
                Log.d("uri",""+Conf.AUDIO);
                Conf.save();
                utl.toast(ctx,"Picked : "+Conf.AUDIO);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





}
