package com.evault.java.samples.apis;

import com.evault.java.samples.account.ApiClientHelper;
import com.evault.java.samples.account.Credential;
import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.AccountApi;
import com.exavault.client.model.Account;
import com.exavault.client.model.AccountResponse;
import com.exavault.client.model.Quota;
import com.exavault.client.model.User;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import static com.evault.java.samples.apis.resource.util.Utils.getErrorStack;

/**
 * To use this sample, add your credentials to a file named {@code credentials.properties} which is located in {@code resources} folder at root level of your project.
 * <p>
 * <ul>
 * 	<li>Your API key will be the {@code exavault.api.Key}</li>
 * 	<li>Your access token will be {@code exavault.access.token}</li>
 * 	<li>Your account URL will be the address you should use for the API endpoint</li>
 * </ul>
 * <p>
 * To obtain your API Key and Token, you'll need to use the Developer page within the web file manager
 * <p>
 * See <a href="https://www.exavault.com/developer/api-docs/#section/Obtaining-Your-API-Key-and-Access-Token">Obtaining-Your-API-Key-and-Access-Token</a>
 * <p>
 * Access tokens do not expire, so you should only need to obtain the key and token once.
 * <p>
 * Your account URL is determined by the name of your account.
 * <p>
 * The URL that you will use is {@code https://accountname.exavault.com/api/v2/} replacing the {@code "accountname"} part with your account name
 * <p>
 * See <a href="https://www.exavault.com/developer/api-docs/#section/Introduction/The-API-URL">The-API-URL</a>
 * <p>
 * This example uses the {@link com.exavault.client.api.AccountApi} to return your account info,
 * check disk usage and use include param to return master user.
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * Used: 0 B (0%)
 * Total size: 350.0 GB
 * Primary Email Address: email@example.com
 * </pre>
 */
public class GetAccount {
	public static void main(String[] args) {
		// Include masterUser in response
		final String include = "masterUser";
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		Credential credential = ApiClientHelper.getCredential();
		AccountApi accountApi = new AccountApi(apiClient);
		try {
			/*
			 * The {@link com.exavault.client.api.AccountApi#getAccount} method will give us access to the current status of our
			 * account
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/getAccount">getAccount</a> for the details of
			 * this method
			 */
			AccountResponse result = accountApi.getAccount(credential.getEvApiKey(), credential.getEvAccessToken(), include);
			printDiskUsage(result);
			printMasterId(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling AccountApi#getAccount" + getErrorStack(e));
		}
	}

	/**
	 * Get the included from the response and fetch "id" from the list and prints it value to the console.
	 *
	 * @param result response from the Account API
	 */
	private static void printMasterId(AccountResponse result) {
		/*
		 * Included is an array of generic objects
		 * You need to cast it to LinkedTreeMap class to get data from it
		 */
		List<User> included = result.getIncluded();
		if (included != null && included.size() > 0) {
			User user = included.get(0);
			// Print out Primary Email Address
			System.out.println("Primary Email Address: " + user.getAttributes().getEmail());
		} else {
			System.err.println("Primary Email Address could not be retrieved");
		}
	}

	/**
	 * The {@link com.exavault.client.model.AccountResponse} object that we got back (result) is composed of additional, nested objects
	 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/getAccount">getAccount</a>
	 * for the details of the response object
	 */
	private static void printDiskUsage(AccountResponse result) {
		Long diskUsed = getDiskUsed(result);
		Long diskLimit = getDiskLimit(result);
		//validate if correct values are retrieved
		if (diskLimit != -1L && diskUsed != -1L) {
			long percentUsed = Math.round((double) diskUsed / (double) diskLimit * 100);
			// The values returned in the Quota object are given in bytes, so we convert that to GB and print out
			System.out.println("Used: " + humanReadableByteCountBin(diskUsed) + " (" + percentUsed + "%)");
			System.out.println("Total size: " + humanReadableByteCountBin(diskLimit));
		} else {
			System.err.println("Disk Used or Disk Limit could not be retrieved");
		}
	}

	/***
	 * Get total disk limit
	 * @param result the account response object to fetch the disk limit from
	 * @return the disk limit or -1 when quota could not be fetched
	 */
	private static Long getDiskLimit(AccountResponse result) {
		Quota quota = getQuota(result);
		if (quota != null) {
			return quota.getDiskLimit();
		}
		return -1L;
	}

	/***
	 * Get the amount of the disk used
	 * @param result the account response object to fetch the disk used from
	 * @return the disk used so far or -1 when quota could not be fetched
	 */
	private static Long getDiskUsed(AccountResponse result) {
		Quota quota = getQuota(result);
		if (quota != null) {
			return quota.getDiskUsed();
		}
		return -1L;
	}

	/**
	 * Get the quota from the response object
	 *
	 * @param result the response object
	 * @return the quota or Null if account data could not be retrieved
	 */
	private static Quota getQuota(AccountResponse result) {
		Account accountData = result.getData();
		if (accountData != null) {
			return accountData.getAttributes().getQuota();
		} else {
			return null;
		}
	}

	/**
	 * Convert bytes to human readable account size i.e. 999 -> 999 B,  1728 -> 1.7 KiB, 28991029248 -> 27.0 GiB
	 *
	 * @param bytes bytes to be converted
	 * @return converted bytes in human readable format.
	 */
	private static String humanReadableByteCountBin(long bytes) {
		long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		if (absB < 1024) {
			return bytes + " B";
		}
		long value = absB;
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
			value >>= 10;
			ci.next();
		}
		value *= Long.signum(bytes);
		return String.format("%.1f %cB", value / 1024.0, ci.current());
	}
}