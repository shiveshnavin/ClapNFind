package nf.co.thehoproject.clapnfind;

/**
 * Created by shivesh on 7/10/16.
 */


public interface AmplitudeClipListener
{
    /**
     * return true if recording should stop
     */
    public boolean heard(int maxAmplitude);
}

