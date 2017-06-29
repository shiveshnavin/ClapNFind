/*
 * Copyright (C) 2012 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * musicg api in Google Code: http://code.google.com/p/musicg/
 * Android Application in Google Play: https://play.google.com/store/apps/details?id=com.whistleapp
 * 
 */

package nf.co.thehoproject.clapnfind.MusicG;

import java.util.Vector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.musicg.api.ClapApi;
import com.musicg.wave.WaveHeader;


public class DetectorThread extends Thread{

	private RecorderThread recorder;
	private WaveHeader waveHeader;
	private ClapApi clapApi;
	private volatile Thread _thread;

	int requiredClapCount = 2;
	int minDistance = 200;
	int maxDistance = 1000;
	Vector<Long> claps;
	
	
	private OnSignalsDetectedListener onSignalsDetectedListener;
	
	public DetectorThread(RecorderThread recorder){
		this.recorder = recorder;
		AudioRecord audioRecord = recorder.getAudioRecord();
		
		int bitsPerSample = 0;
		if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT){
			bitsPerSample = 16;
		}
		else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT){
			bitsPerSample = 8;
		}
		
		//but we are doing claps so???
		int channel = 0;
		// whistle detection only supports mono channel
		if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO){
			channel = 1;
		}

		waveHeader = new WaveHeader();
		waveHeader.setChannels(channel);
		waveHeader.setBitsPerSample(bitsPerSample);
		waveHeader.setSampleRate(audioRecord.getSampleRate());
		clapApi = new ClapApi(waveHeader);
		
		claps = new Vector<Long>();
	}

	public void start() {
		_thread = new Thread(this);
        _thread.start();
    }
	
	public void stopDetection(){
		_thread = null;
	}
	
	public void run() {
		try {
			byte[] buffer;
			
			Thread thisThread = Thread.currentThread();
			while (_thread == thisThread) {
				//detect sound
				buffer = recorder.getFrameBytes();
				if (buffer != null) {
					//check for claps!
					boolean isClap = clapApi.isClap(buffer);
					long currentTime = System.currentTimeMillis();
					if(isClap){
						claps.add(currentTime);
					}
					//if last clap is expired clear list
					if(claps.size() > 0){
						if(currentTime - claps.get(claps.size() -1) > maxDistance){
							claps.clear();
						}
					}
					//regardless remove all claps that are to close or too far from another clap
					//skip last element
					for(int i = 0; i < claps.size() -1; i++){
						if(claps.get(i+1) - claps.get(i) < minDistance){
							claps.remove(i);
							break;
						}
						if(claps.get(i+1) - claps.get(i) > maxDistance){
							claps.remove(i);
							break;
						}
					}
					
					Log.i("Mitch","claps = " + claps.size());
					if(claps.size() >= requiredClapCount){
						onClapsDetected();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onClapsDetected(){
		if (onSignalsDetectedListener != null){
			onSignalsDetectedListener.onClapDetected();
		}
	}
	
	public void setOnSignalsDetectedListener(OnSignalsDetectedListener listener){
		onSignalsDetectedListener = listener;
	}
}