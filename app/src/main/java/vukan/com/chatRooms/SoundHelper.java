package vukan.com.chatRooms;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;

/**
 * <h1>SoundHelper</h1>
 *
 * <p><b>SoundHelper</b> class is responsible for reproducing sound when someone tap button to send message.</p>
 */
class SoundHelper {
    private final SoundPool mSoundPool;
    private final int mSoundID;
    private final float mVolume;
    private boolean mLoaded;

    SoundHelper(AppCompatActivity activity) {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mVolume = (float) (audioManager != null ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : 0) / (float) (audioManager != null ? audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 0);
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(6).build();
        } else mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> mLoaded = true);
        mSoundID = mSoundPool.load(activity, R.raw.pop_sound_effect, 1);
    }

    /**
     * This method is responsible for play pop sound on sending new message.
     *
     * @see SoundPool#play(int, float, float, int, int, float)
     */
    void playSound() {
        if (mLoaded) mSoundPool.play(mSoundID, mVolume, mVolume, 1, 0, 3f);
    }
}