package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.ActivityApi;
import com.exavault.client.model.SessionActivityEntry;
import com.exavault.client.model.SessionActivityResponse;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exavault.java.samples.Utils.getErrorStack;

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
 * This example uses the {@link com.exavault.client.api.ActivityApi} to retrieve the login operation from your account
 * <p>
 * We are demonstrating the use of the ActivityApi, which can be used to retrieve session and webhook logs
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * 3 Users with failed logins:
 * Username                Count
 * =============================
 * dummy                       3
 * </pre>
 */
public class GetActivity {

	private static final String FAILED = "failed";

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		Credential credential = ApiClientHelper.getCredential();
		/*
		 * The getSessionLogs method of the ActivityApi class will give us access activity logs for our account
		 * <p>
		 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/getSessionLogs">getSessionLogs</a>
		 * for the details of this method
		 */

		ActivityApi activityApi = new ActivityApi(apiClient);

		//Offset of the records list
		final int offset = 0;
		//Limit of the records list
		final int limit = 200;
		//Username used for filtering a list
		String userName = null;
		//Path used to filter records
		String path = null;
		//Used to filter session logs by ip address
		String ipAddress = null;
		//Filter session logs for operation type
		final String type = "PASS";
		//Start date of the filter data range
		OffsetDateTime startDate = OffsetDateTime.now().minusDays(1);
		//End date of the filter data range
		OffsetDateTime endDate = OffsetDateTime.now();
		//Comma separated list sort params
		final String sort = "-date";

		try {
			/*
			 * Returns all login activity for the account for the past day
			 * <p>
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/getSessionLogs">getSessionLogs</a>
			 * for the details of the response object
			 */

			SessionActivityResponse result = activityApi.getSessionLogs(credential.getEvApiKey(), credential.getEvAccessToken(),
				startDate, endDate, ipAddress, userName, path, type, offset, limit, sort);
			getAndPrintFailedLogins(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling ActivityApi#getSessionLogs" + getErrorStack(e));
		}
	}

	private static void getAndPrintFailedLogins(SessionActivityResponse result) {
		//build a list of failed login attempts
		List<SessionActivityEntry> failedLogins = new ArrayList<>();
		List<SessionActivityEntry> data = result.getData();
		for (SessionActivityEntry entry : data) {
			if (entry.getAttributes().getStatus().equalsIgnoreCase(FAILED)) {
				failedLogins.add(entry);
			}
		}
		Map<String, Integer> failedCounts = new HashMap<>();
		//build a map to keep the counter of failed logins per user
		for (SessionActivityEntry failedLogin : failedLogins) {
			String username = failedLogin.getAttributes().getUsername();
			if (failedCounts.containsKey(username)) {
				failedCounts.put(username, failedCounts.get(username) + 1);
			} else {
				failedCounts.put(username, 1);
			}
		}
		//print the results on the console
		System.out.println(failedLogins.size() + " Users with failed logins: ");
		System.out.printf("%5s %25s", "Username", "Count");
		System.out.println("");
		System.out.println("==================================");
		for (String userName : failedCounts.keySet()) {
			System.out.format("%-20s %10d\n", userName, failedCounts.get(userName));
		}
	}
}
