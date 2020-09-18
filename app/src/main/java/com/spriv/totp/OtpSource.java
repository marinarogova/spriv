package com.spriv.totp;


import java.util.Collection;

import com.spriv.data.SprivAccountId;

/**
 * Abstraction for collection of OTP tokens.
 *
 * @author cemp@google.com (Cem Paya)
 */
public interface OtpSource {

  /**
   * Enumerate list of accounts that this OTP token supports.
   *
   * @param result Collection to append usernames. This object is NOT cleared on
   *               entry; if there are existing items, they will not be removed.
   * @return Number of accounts added to the collection.
   */
  int enumerateAccounts(Collection<String> result);

  /**
   * Return the next OTP code for specified username.
   * Invoking this function may change internal state of the OTP generator,
   * for example advancing the counter.
   *
   * @param accountName Username, email address or other unique identifier for the account.
   * @return OTP as string code.
   */
  String getNextCode(SprivAccountId accountId) throws OtpSourceException;

  /**
   * Gets the counter for generating or verifying TOTP codes.
   */
  TotpCounter getTotpCounter();

  /**
   * Gets the clock for generating or verifying TOTP codes.
   */
  TotpClock getTotpClock();
}

