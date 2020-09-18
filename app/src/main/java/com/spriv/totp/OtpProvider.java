package com.spriv.totp;

import java.security.GeneralSecurityException;
import java.util.Collection;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;

import com.spriv.data.SprivAccountId;
import com.spriv.model.AccountsDataProvider;
import com.spriv.totp.Base32String.DecodingException;
import com.spriv.totp.PasscodeGenerator.Signer;

/**
 * Class containing implementation of HOTP/TOTP.
 * Generates OTP codes for one or more accounts
 */
public class OtpProvider implements OtpSource {

  private static final int PIN_LENGTH = 6; // HOTP or TOTP
  private static final int REFLECTIVE_PIN_LENGTH = 9; // ROTP
  /** Default passcode timeout period (in seconds) */
  public static final int DEFAULT_INTERVAL = 30;

  private final AccountsDataProvider mAccountsDataProvider;

  /** Counter for time-based OTPs (TOTP). */
  private final TotpCounter mTotpCounter;

  /** Clock input for time-based OTPs (TOTP). */
  private final TotpClock mTotpClock;
  
  private static final String LOCAL_TAG = "Spriv.OTPProvider";

  @Override
  public int enumerateAccounts(Collection<String> result) {
    return -1;
  }

  @Override
  public String getNextCode(SprivAccountId accountId) throws OtpSourceException {
    return getCurrentCode(accountId, null);
  }

  @Override
  public TotpCounter getTotpCounter() {
    return mTotpCounter;
  }

  @Override
  public TotpClock getTotpClock() {
    return mTotpClock;
  }

  private String getCurrentCode(SprivAccountId accountId, byte[] challenge) throws OtpSourceException {
    String secret = getSecret(accountId);

    long otp_state = mTotpCounter.getValueAtTime(millisToSeconds(mTotpClock.currentTimeMillis()));
    return computePin(secret, otp_state, challenge);
  }

  private OtpProvider(AccountsDataProvider accountsDataProvider, TotpClock totpClock) {
    this(DEFAULT_INTERVAL, accountsDataProvider, totpClock);
  }

  private OtpProvider(int interval, AccountsDataProvider accountsDataProvider, TotpClock totpClock) {
    mAccountsDataProvider = accountsDataProvider;
    mTotpCounter = new TotpCounter(interval);
    mTotpClock = totpClock;
  }
  
  public static OtpProvider CreteOtpProvider(AccountsDataProvider accountsDataProvider, TotpClock totpClock)
  {
	  return new OtpProvider(accountsDataProvider, totpClock);
  }

  /**
   * Computes the one-time PIN given the secret key.
   *
   * @param secret the secret key
   * @param otp_state current token state (counter or time-interval)
   * @param challenge optional challenge bytes to include when computing passcode.
   * @return the PIN
   */
  private String computePin(String secret, long otp_state, byte[] challenge)
      throws OtpSourceException {
    if (secret == null || secret.length() == 0) {
      throw new OtpSourceException("Null or empty secret");
    }

    try {
      Signer signer = getSigningOracle(secret);
      PasscodeGenerator pcg = new PasscodeGenerator(signer,
        (challenge == null) ? PIN_LENGTH : REFLECTIVE_PIN_LENGTH);

      return (challenge == null) ?
             pcg.generateResponseCode(otp_state) :
             pcg.generateResponseCode(otp_state, challenge);
    } catch (GeneralSecurityException e) {
      throw new OtpSourceException("Crypto failure", e);
    }
  }

  /**
   * Reads the secret key that was saved on the phone.
   * @param user Account name identifying the user.
   * @return the secret key as base32 encoded string.
   */
  String getSecret(SprivAccountId accountId) {
    return mAccountsDataProvider.getSecret(accountId);
  }
  
  public static final long millisToSeconds(long timeMillis) {
	    return timeMillis / 1000;
	  }
  
  public static Signer getSigningOracle(String secret) {
	    try {
	      byte[] keyBytes = decodeKey(secret);
	      //final Mac mac = Mac.getInstance("HMACSHA1");
	      final Mac mac = Mac.getInstance("HmacSHA512");
	      mac.init(new SecretKeySpec(keyBytes, ""));

	      // Create a signer object out of the standard Java MAC implementation.
	      return new Signer() 
	      {
	        @Override
	        public byte[] sign(byte[] data) {
	          return mac.doFinal(data);
	        }
	      };
	    } 
	    catch (Exception error) 
	    {
	      Log.e(LOCAL_TAG, error.getMessage());
	      return null;
	    }
	  }

	  private static byte[] decodeKey(String secret) throws DecodingException 
	  {
	    return Base32String.decode(secret);
	  }
}
