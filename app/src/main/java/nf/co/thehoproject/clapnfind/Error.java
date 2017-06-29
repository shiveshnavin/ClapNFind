package nf.co.thehoproject.clapnfind;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class Error extends AppCompatActivity {



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


        setContentView(R.layout.activity_error);

        listenFirebase(this);
        View book=findViewById(R.id.contact);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://thehoproject.co.nf/contact/index.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }



    /***********************FIREBASE SETUP*************************************/

    Context ctx;
    Firebase fire;
    public void listenFirebase(Context ctxt)
    {
        ctx=ctxt;
        String app=getResources().getString(R.string.app_name).replace(" ","").toLowerCase()+"v1";
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
        Firebase.setAndroidContext(ctx);
        fire= new Firebase(Conf.FIREBASE_URL+app);
        // fire.child("status").setValue("online");
        Log.d("FireURl", Conf.FIREBASE_URL+app);

        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

/************************************************************/
                    try {
                        String st=postSnapshot.getValue().toString();
                        if(st.equalsIgnoreCase("online")&&postSnapshot.getKey().equalsIgnoreCase("status"))
                        {
                            Intent iz=new Intent(getBaseContext(),Home.class);
                            iz.putExtra("message",st);
                            startActivity(iz);
                            finish();;
                        }

                        if(st.contains("offline")&&postSnapshot.getKey().equalsIgnoreCase("status"))
                        {
                            TextView textView2=(TextView)findViewById(R.id.textView2);
                            textView2.setText(st.replace("offline",""));
                        }

                        if(st.contains("update")&&postSnapshot.getKey().equalsIgnoreCase("status"))
                        {

                            TextView textView2=(TextView)findViewById(R.id.textView2);
                            textView2.setText("Update Required\n"+"You Must Update to continue using the app .");



                            final Intent iz=new Intent(Intent.ACTION_VIEW);
                            iz.setData(Uri.parse(postSnapshot.getValue().toString().replace("update","")));

                            DialogInterface.OnClickListener click= new
                                    DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(iz);
                                        }
                                    };


                               //finish();;
                        }



                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("RES",postSnapshot.toString());
/************************************************************/
                        }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

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
    /*************************FIREBASE SETUP***********************************/




}

