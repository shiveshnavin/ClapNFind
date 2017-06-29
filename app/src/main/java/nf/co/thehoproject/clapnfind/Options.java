package nf.co.thehoproject.clapnfind;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Options extends AppCompatActivity {

    @Bind (R.id.title)
    TextView title;
    @Bind (R.id.desc)
    TextView desc;
    @Bind (R.id.onoff)
    ImageView onoff;


    Context ctx;
    Conf.ConfS cf;
    String act;
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

        setContentView(R.layout.activity_options);

        try {
            ctx=this;
            ButterKnife.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        act=getIntent().getStringExtra("act");
        if(act!=null)
        {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            finish();
        }

    }

    public void init()
    {


        cf=Conf.eLoad();
       if(act.equals("Screen")){

        title.setText(getResources().getString(R.string.option_title_screen));
           desc.setText(getResources().getString(R.string.option_content_screen));
           if(cf.SCREEN_DISABLE)
           {
               onoff.setImageResource(R.drawable.option_screen_button_on);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.SCREEN_DISABLE=false;
                       Conf.eSave(cf);
                       init();
                   }
               });

           }
           else {
               onoff.setImageResource(R.drawable.option_screen_button_off);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.SCREEN_DISABLE=true;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }






    }
       if(act.equals("Password")){


           title.setText(getResources().getString(R.string.option_title_password));
           desc.setText(getResources().getString(R.string.option_content_password));
           if(cf.PASSWORD_ACTIVE)
           {
               onoff.setImageResource(R.drawable.option_password_button_on);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.PASSWORD_ACTIVE=false;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
           else {
               onoff.setImageResource(R.drawable.option_password_button_off);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       getPhone();
                   }
               });
           }
    }
       if(act.equals("Call")){


           title.setText(getResources().getString(R.string.option_title_call));
           desc.setText(getResources().getString(R.string.option_content_call));
           if(cf.CALL_DISABLE)
           {
               onoff.setImageResource(R.drawable.option_call_button_on);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.CALL_DISABLE=false;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
           else {
               onoff.setImageResource(R.drawable.option_call_button_off);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.CALL_DISABLE=true;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
    }
       if(act.equals("Repeat_Song")){


           title.setText(getResources().getString(R.string.option_title_repeat));
           desc.setText(getResources().getString(R.string.option_content_repeat));
           if(cf.REPEAT)
           {
               onoff.setImageResource(R.drawable.option_repeat_button_on);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.REPEAT=false;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
           else {
               onoff.setImageResource(R.drawable.option_repeat_button_off);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.REPEAT=true;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
    }
       if(act.equals("HeadPhone")){


           title.setText(getResources().getString(R.string.option_title_headphone));
           desc.setText(getResources().getString(R.string.option_content_headphone));
           if(cf.HEADPHONE_BEEP)
           {
               onoff.setImageResource(R.drawable.option_headphone_button_on);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.HEADPHONE_BEEP=false;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
           else {
               onoff.setImageResource(R.drawable.option_headphone_button_off);
               onoff.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cf.HEADPHONE_BEEP=true;
                       Conf.eSave(cf);
                       init();
                   }
               });
           }
    }





    }

    Dialog dig ;
    public void getPhone()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Enter Password");
        builder.setView(R.layout.dialog_input);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                EditText button=(EditText)dig.findViewById(R.id.search_et);
                final String phone=button.getText().toString();
                cf.PASSWORD=phone;
                cf.PASSWORD_ACTIVE=true;

                Conf.eSave(cf);
                init();
                dialog.dismiss();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                dialog.dismiss();;
            }
        });
        dig = builder.create();
        dig.show();



    }


}
