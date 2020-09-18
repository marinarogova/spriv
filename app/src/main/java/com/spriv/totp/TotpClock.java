package com.spriv.totp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Clock input for the TOTP. The input is based on the current system time
 * and is adjusted by a persistently stored correction value (offset in minutes).
 */
public class TotpClock {

  // @VisibleForTesting
  static final String PREFERENCE_KEY_OFFSET_MINUTES = "timeCorrectionMinutes";

  private final SharedPreferences mPreferences;

  private final Object mLock = new Object();

  /**
   * Cached value of time correction (in minutes) or {@code null} if not cached. The value is cached
   * because it's read very frequently (once every 100ms) and is modified very infrequently.
   *
   * @GuardedBy {@link #mLock}
   */
  private Integer mCachedCorrectionMinutes;

  public TotpClock(Context context) {
    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  /**
   * Gets the number of milliseconds since epoch.
   */
  public long currentTimeMillis() {
    return System.currentTimeMillis() + getTimeCorrectionMinutes() * 60 * 1000; // minute in millis
  }

  /**
   * Gets the currently used time correction value.
   *
   * @return number of minutes by which this device is behind the correct time.
   */
  public int getTimeCorrectionMinutes() {
    synchronized (mLock) {
      if (mCachedCorrectionMinutes == null) {
        mCachedCorrectionMinutes = mPreferences.getInt(PREFERENCE_KEY_OFFSET_MINUTES, 0);
      }
      return mCachedCorrectionMinutes;
    }
  }

  /**
   * Sets the currently used time correction value.
   *
   * @param minutes number of minutes by which this device is behind the correct time.
   */
  public void setTimeCorrectionMinutes(int minutes) {
    synchronized (mLock) {
      mPreferences.edit().putInt(PREFERENCE_KEY_OFFSET_MINUTES, minutes).commit();
      // Invalidate the cache to force reading actual settings from time to time
      mCachedCorrectionMinutes = null;
    }
  }
}
