package nf.co.thehoproject.clapnfind;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.Power_Off) ImageView Power_Off ;
    @Bind(R.id.Power_On) ImageView Power_On ;
    @Bind(R.id.Change_Ringtone) ImageView Change_Ringtone ;
    @Bind(R.id.Screen) ImageView Screen ;
    @Bind(R.id.Password) ImageView Password ;
    @Bind(R.id.Call) ImageView Call ;
    @Bind(R.id.Repeat_Song) ImageView Repeat_Song ;
    @Bind(R.id.HeadPhone) ImageView HeadPhone ;
    @Bind(R.id.Reset) ImageView Reset;



    Conf.ConfS cf;
    Context ctx;
    Intent opt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx=getApplicationContext();
        try {
            ButterKnife.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        opt=new Intent(ctx,Options.class);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //respond to menu item selection
        int id=item.getItemId();

        if(id==R.id.set)
        {

            startActivity(new Intent(ctx,Home.class));
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onResume()
    {
        super.onResume();;

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


}
