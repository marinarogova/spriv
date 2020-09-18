package com.spriv.totp;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.spriv.utils.RunOnThisLooperThreadExecutor;

class SyncNowController {
  enum Result {
    TIME_CORRECTED,
    TIME_ALREADY_CORRECT,
    CANCELLED_BY_USER,
    ERROR_CONNECTIVITY_ISSUE,
  }

  private enum State {
    NOT_STARTED,
    IN_PROGRESS,
    DONE,
  }

  private static final String LOG_TAG = "TimeSync";

  private final TotpClock mTotpClock;
  private final NetworkTimeProvider mNetworkTimeProvider;
  private final Executor mBackgroundExecutor;
  private final Executor mCallbackFromBackgroundExecutor;
  private final boolean mBackgroundExecutorServiceOwnedByThisController;

  private State mState = State.NOT_STARTED;
  private Result mResult;

  // @VisibleForTesting
  SyncNowController(
      TotpClock totpClock,
      NetworkTimeProvider networkTimeProvider,
      Executor backgroundExecutor,
      boolean backgroundExecutorServiceOwnedByThisController,
      Executor callbackFromBackgroundExecutor) {
    mTotpClock = totpClock;
    mNetworkTimeProvider = networkTimeProvider;
    mBackgroundExecutor = backgroundExecutor;
    mBackgroundExecutorServiceOwnedByThisController =
        backgroundExecutorServiceOwnedByThisController;
    mCallbackFromBackgroundExecutor = callbackFromBackgroundExecutor;
  }

  SyncNowController(TotpClock totpClock, NetworkTimeProvider networkTimeProvider) {
    this(
        totpClock,
        networkTimeProvider,
        Executors.newSingleThreadExecutor(),
        true,
        new RunOnThisLooperThreadExecutor());
  }

    /**
   * Starts this controller's operation (initiates a Time Sync).
   */
  public void syncTime() {
    mState = State.IN_PROGRESS;
    // Avoid blocking this thread on the Time Sync operation by invoking it on a different thread
    // (provided by the Executor) and posting the results back to this thread.
    mBackgroundExecutor.execute(new Runnable() {
      @Override
      public void run() {
        runBackgroundSyncAndPostResult(mCallbackFromBackgroundExecutor);
      }
    });
  }
  
  /**
   * Invoked when the time correction value was successfully obtained from the network time
   * provider.
   *
   * @param timeCorrectionMinutes number of minutes by which this device is behind the correct time.
   */
  private void onNewTimeCorrectionObtained(int timeCorrectionMinutes) {
    if (mState != State.IN_PROGRESS) {
      // Don't apply the new time correction if this controller is not waiting for this.
      // This callback may be invoked after the Time Sync operation has been cancelled or stopped
      // prematurely.
      return;
    }

    long oldTimeCorrectionMinutes = mTotpClock.getTimeCorrectionMinutes();
    Log.i(LOG_TAG, "Obtained new time correction: "
        + timeCorrectionMinutes + " min, old time correction: "
        + oldTimeCorrectionMinutes + " min");
    if (timeCorrectionMinutes == oldTimeCorrectionMinutes) {
      finish(Result.TIME_ALREADY_CORRECT);
    } else {
      mTotpClock.setTimeCorrectionMinutes(timeCorrectionMinutes);
      finish(Result.TIME_CORRECTED);
    }
  }

  /**
   * Terminates this controller's operation with the provided result/outcome.
   */
  private void finish(Result result) {
    if (mState == State.DONE) {
      // Not permitted to change state when already DONE
      return;
    }
    if (mBackgroundExecutorServiceOwnedByThisController) {
      ((ExecutorService) mBackgroundExecutor).shutdownNow();
    }
    mState = State.DONE;
    mResult = result;
  }

  /**
   * Obtains the time correction value (<b>may block for a while</b>) and posts the result/error
   * using the provided {@link Handler}.
   */
  private void runBackgroundSyncAndPostResult(Executor callbackExecutor) {
    long networkTimeMillis;
    try {
      networkTimeMillis = mNetworkTimeProvider.getNetworkTime();
    } catch (IOException e) {
      Log.w(LOG_TAG, "Failed to obtain network time due to connectivity issues");
      callbackExecutor.execute(new Runnable() {
        @Override
        public void run() {
          finish(Result.ERROR_CONNECTIVITY_ISSUE);
        }
      });
      return;
    }

    long timeCorrectionMillis = networkTimeMillis - System.currentTimeMillis();
    final int timeCorrectionMinutes = (int) Math.round(
        ((double) timeCorrectionMillis) / (60 * 1000));
    callbackExecutor.execute(new Runnable() {
      @Override
      public void run() {
        onNewTimeCorrectionObtained(timeCorrectionMinutes);
      }
    });
  }
}
