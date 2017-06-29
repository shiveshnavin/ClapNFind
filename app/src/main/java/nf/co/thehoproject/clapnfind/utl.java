package nf.co.thehoproject.clapnfind;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by shivesh on 8/4/16.
 */
public class utl {



    public static boolean match(String parent,String query)
    {
        boolean match=false;
        if(parent.toLowerCase().contains(query.toLowerCase()))
        {
            match=true;
        }


        return match;
    }





    private static  boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }



    public static class Util
    {

        public   Dialog dialog;
        public   void showDig(boolean show,Context ctx)
        {
            try {
                if(show)
                {

                    utl.log("SHOWING DIG");
                    dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.gen_load);
                    final Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                    dialog.getWindow().getAttributes().alpha = 0.7f;



                    dialog.setTitle("Select Content Language");

                    dialog.setContentView(R.layout.gen_load);
                    //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.show();

                }
                else   {



                    utl.log("NOT SHOWING DIG");
                    if(dialog!=null)
                        if(dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


   public static Dialog dialog;
    public static void showDig(boolean show,Context ctx)
    {
        try {
            if(show)
            {

                utl.log("SHOWING DIG");
                dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.gen_load);
                final Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.getWindow().getAttributes().alpha = 0.7f;



                dialog.setTitle("Select Content Language");

                dialog.setContentView(R.layout.gen_load);
                //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();

            }
            else   {



                utl.log("NOT SHOWING DIG");
                if(dialog!=null)
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static String refineString(String red,String rep)
    {
        red = red.replaceAll("[^a-zA-Z0-9]", rep);
        return  red;
    }


    private boolean isValidMail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }










    public static Bitmap convertBitmap(String path)   {
                Bitmap bitmap=null;
                BitmapFactory.Options bfOptions=new BitmapFactory.Options();
                bfOptions.inDither=false;                     //Disable Dithering mode
                bfOptions.inPurgeable=true;                   //Tell to gc that whether itneeds free memory, the Bitmap can be cleared
                bfOptions.inInputShareable=true;              //Which kind of reference will be                used to recover the Bitmap data after being clear, when it will be used in the future
                bfOptions.inTempStorage=new byte[32 * 1024];
                File file=new File(path);
                FileInputStream fs=null;
                try {
                    fs = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    if(fs!=null)
                    {
                        bitmap=BitmapFactory.decodeFileDescriptor(fs.
                                getFD(), null, bfOptions);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    if(fs!=null) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return bitmap;
            }




    public static Bitmap convertBitmap(Context ctx,  String  path,int q)   {


        Log.d("path",path);

        Bitmap bitmap=null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inDither=false;                     //Disable Dithering mode
        bfOptions.inPurgeable=true;                   //Tell to gc that whether itneeds free memory, the Bitmap can be cleared
        bfOptions.inInputShareable=true;              //Which kind of reference will be                used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage=new byte[32 * 1024];
        File file=new File(path);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if(fs!=null)
            {
                bitmap=BitmapFactory.decodeFileDescriptor(fs.
                        getFD(), null, bfOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        Bitmap bitmap1=Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*(q/100)),
                (int)(bitmap.getHeight()*(q/100)),true);

        try{

            bitmap.recycle();;
            bitmap=null;

        }catch (Exception e)
        {
            Log.d("Recycle fail","Never Ming at convertBitmap()")
;            e.printStackTrace();
        }
        return bitmap1;
    }




    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static  void saveScaledBitmap(Bitmap bmp,int h,int w,String dest){

        Bitmap bmm=getResizedBitmap(bmp,h,w);


        File de=new File(dest);

       /*     try {  if(!de.exists())
                de.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        FileOutputStream out= null;
        try {
            out = new FileOutputStream(de);
        bmp.compress(Bitmap.CompressFormat.PNG,1,out);
        out.flush();;
        out.close();;
            Log.d("SAVED",dest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Bitmap overlay(Bitmap b1,Bitmap b2)
    {

        b2=utl.getResizedBitmap(b2,b1.getHeight(),b1.getWidth());
        Bitmap ov=Bitmap.createBitmap(b1.getWidth(), b1.getHeight(), b1.getConfig());
        Canvas cv=new Canvas(ov);
        cv.drawBitmap(b1,new Matrix(),null);
        cv.drawBitmap(b2,new Matrix(),null);
        return ov;

    }


    public static void bitmapToFile(Bitmap bmp,String dest)
    {

        File f=new File(dest);
        try {
            FileOutputStream os;

            os=new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG,100,os);
            os.flush();
            os.close();
            bmp.recycle();


        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }

    public static String resizePng(Context ctx,String src,String dest, int quality)
    {

        try {
            Bitmap bmp = convertBitmap(ctx,src,quality);
            if (bmp.getWidth()<512) {

                File de=new File(dest);
                FileOutputStream out=new FileOutputStream(de);
                bmp.compress(Bitmap.CompressFormat.PNG,90,out);
                out.flush();;
                out.close();;

                return dest;
            }


            File de=new File(dest);
            FileOutputStream out=new FileOutputStream(de);
            bmp.compress(Bitmap.CompressFormat.PNG,quality,out);
            out.flush();;
            out.close();;







        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return dest;
    }





    public static void toast(Context c,String t) {
        Toast.makeText(c, t, Toast.LENGTH_LONG).show();
    }

   

    public static void toast(Context c,String tzz,Integer col)
    { 
        Toast t=Toast.makeText(c, tzz, Toast.LENGTH_LONG);
        t.getView().setBackgroundColor(c.getResources().getColor(col));
        t.show();



    }


    public static void log(String t)
    {

        Log.d("TAG UTL", ""+t);
    }

    
    
    public static void diag(Context c,String title,String desc,String action,DialogInterface.OnClickListener click)
    {



        final AlertDialog.Builder
                alertDialogBuilder = new AlertDialog.Builder
                (c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setNeutralButton(action,click);


        AlertDialog alertDialog
                = alertDialogBuilder.create();


        alertDialog.show();
    }




    public static void copyFile(File src,File dst)
    {
        try{

        InputStream in=new FileInputStream(src);
        OutputStream os=new FileOutputStream(dst);

            byte []buf=new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                os.write(buf,0,len);
            }
            in.close();
            os.close();




        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }




    public static  String folder=Environment.getExternalStorageDirectory().getPath();;
    public  utl(){

        folder= Environment.getExternalStorageDirectory().getPath();


    }
    public static void diag(Context c,String title,String desc)
    {
        final AlertDialog.Builder
                alertDialogBuilder = new AlertDialog.Builder
                (c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setNeutralButton("Close", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface
                                                dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog
                = alertDialogBuilder.create();


        alertDialog.show();
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap
                (bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth,int i) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) ;
        float scaleHeight = ((float) newHeight) ;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap
                (bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
public Bitmap circle(Bitmap bmp)
{
    Bitmap out=Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas cv=new Canvas(out);
    int color= Color.RED;
    Paint paint=new Paint();
    Rect rect=new Rect(0,0,bmp.getWidth(),bmp.getHeight());
    RectF rectF=new RectF(rect);
    paint.setAntiAlias(true);
    cv.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    cv.drawOval(rectF, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    cv.drawBitmap(bmp,rect,rect,paint);


    return out;


}


}
