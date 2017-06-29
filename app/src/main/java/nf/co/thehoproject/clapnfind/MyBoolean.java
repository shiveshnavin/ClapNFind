package nf.co.thehoproject.clapnfind;

import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;

public class MyBoolean {
    public static int AppPassword;
    public static String MySongName;
    public static String OptionName;
    public static boolean PasswordRight;
    public static boolean SetPasswordBoolean;
    public static boolean isNotificationAccessEnabled;
    public static boolean isOptionCallOn;
    public static boolean isOptionHeadphoneOn;
    public static boolean isOptionNotificationOn;
    public static boolean isOptionPasswordOn;
    public static boolean isOptionRepeatOn;
    public static boolean isOptionScreenOn;
    public static boolean isResetOn;
    public static boolean reStart;
    public static boolean returnSongName;
    public static SharedPreferences settings_ringtone;
    public static SharedPreferences settings_song;
    public static boolean stopAndStartRecordAndDetect;
    public static Uri uriSound;





    static {
        isNotificationAccessEnabled = false;
        returnSongName = true;
    }
}
