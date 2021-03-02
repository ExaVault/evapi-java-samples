package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.UsersApi;
import com.exavault.client.model.User;
import com.exavault.client.model.UserAttributes;
import com.exavault.client.model.UserCollectionResponse;
import com.exavault.client.model.UserPermissions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.exavault.java.samples.Utils.customErrorResponse;

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
 * This example uses the {@link com.exavault.client.api.UsersApi} to create a report of account users
 * <p>
 * We are demonstrating the use of the UsersApi, which can be used to retrieve user settings
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * Listed: [total_users] users to [filename]
 * </pre>
 */
public class GetUsers {
	private static final String COMMA = ",";
	//by default file will be saved in the parent folder of the project
	static final String fileName = "users.csv";

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		Credential credential = ApiClientHelper.getCredential();
		UsersApi usersApi = new UsersApi(apiClient);

		/*
		 * Following parameters can be used to fetch the users:
		 * 1. username (String) -> The username of the user you are looking for. Only entries with the same username as this will be in the list of results
		 *    Does not support wildcard searches
		 * 2. nickname (String) -> Nickname to search for. Ignored if `username` is provided. Supports wildcard searches
		 * 3. email (String) -> Email to search for. Ignored if `username` is provided. Supports wildcard searches
		 * 4. role (String) -> Types of users to include the list. Ignored if `username` is provided
		 *    Valid options are **admin**, **master** and **user**
		 * 5. status (Integer) ->Whether a user is locked. Ignored if `username` is provided. **0** means user is locked, **1** means user is not locked
		 * 6. homeDir (String) ->Path for user's home directory. Ignored if `username` is provided. Supports wildcard searches
		 * 7. search (String) ->Searches the nickname, email, role and homeDir fields for the provided value
		 *    Ignored if `username` is provided. Supports wildcard searches
		 * 8. offset (Integer)-> Starting user record in the result set. Can be used for pagination
		 * 9. sort  (String)-> Sort order or matching users. You can sort by multiple columns by separating sort options with a comma
		 *    the sort will be applied in the order specified. The sort order for each sort field is ascending unless it is prefixed with a minus (“-“),
		 *    in which case it will be descending.  Valid sort fields are: **nickname**, **username**, **email**, **homeDir** and **modified**
		 */

		//Number of users to return. Can be used for pagination
		final int limit = 50;
		//Comma separated list of relationships to include in response. Valid options are **homeResource** and **ownerAccount**
		final String include = "ownerAccount";
		// The list where we will store all users
		int offset = 0; //default value

		try {
			/*
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/listUsers">listUsers</a>
			 * for the details of this method
			 * By using Null for String and 0 for Integer type, means we are skipping applying those parameters as filters
			 * (Refer to the table defined above for the usage of different filters)
			 */
			UserCollectionResponse response = usersApi.listUsers(credential.getEvApiKey(), credential.getEvAccessToken(), null, null,
				null, null, null, null, null, offset, null, limit, include);
			List<User> users = new ArrayList<>(response.getData());

			/*
			 * It is possible that amount of users in account is more than we specified in limit param so
			 * we need to do as many calls as we need to get all users
			 */
			if (response.getTotalResults() > response.getReturnedResults() + offset) {
				offset = offset + limit;
				response = usersApi.listUsers(credential.getEvApiKey(), credential.getEvAccessToken(), null,
					null, null, null, null, null, null, offset, null, limit, include);
				users.addAll(response.getData());
			}
			// print our list of all users in the account
			exportToCsv(users);
		} catch (ApiException e) {
			// In order to capture the detailed error message from the ExaVault API, we are using a custom ErrorResponse class to parse the ApiException
			System.err.println("Exception when calling UsersApi#listUsers => \n" + Utils.customErrorResponse(e));
		} catch (IOException e) {
			// In order to capture the detailed error message from the ExaVault API, we are using a custom ErrorResponse class to parse the IOException
			System.err.println("Exception when writing data to csv file => \n" + customErrorResponse(e));
		}
	}

	//export user data to a csv
	private static void exportToCsv(List<User> users) throws IOException {
		StringBuilder userData = new StringBuilder();
		userData.append("id,username,email,nickname,home_folder,role,time_zone,download,upload,modify,delete," +
			                "list,change_password,share,send_notifications,view_form_data,delete_form_data,expiration,accessed,locked,created,modified");
		userData.append("\n");
		for (User user : users) {
			UserAttributes attributes = user.getAttributes();
			StringBuilder userData2 = new StringBuilder();
			if (attributes != null) {
				UserPermissions permissions = attributes.getPermissions();
				userData2
					.append(user.getId())
					.append(COMMA)
					.append(attributes.getUsername())
					.append(COMMA)
					.append(attributes.getEmail())
					.append(COMMA)
					.append(attributes.getNickname())
					.append(COMMA)
					.append(attributes.getHomePath())
					.append(COMMA)
					.append(attributes.getRole().getValue())
					.append(COMMA)
					.append(attributes.getTimeZone())
					.append(COMMA)
					.append(permissions.isDownload())
					.append(COMMA)
					.append(permissions.isUpload())
					.append(COMMA)
					.append(permissions.isModify())
					.append(COMMA)
					.append(permissions.isDelete())
					.append(COMMA)
					.append(permissions.isList())
					.append(COMMA)
					.append(permissions.isChangePassword())
					.append(COMMA)
					.append(permissions.isShare())
					.append(COMMA)
					.append(permissions.isNotification())
					.append(COMMA)
					.append(permissions.isViewFormData())
					.append(COMMA)
					.append(permissions.isDeleteFormData())
					.append(COMMA)
					.append(attributes.getExpiration())
					.append(COMMA)
					.append(attributes.getAccessTimestamp())
					.append(COMMA)
					.append(attributes.getStatus().getValue() == 0)
					.append(COMMA)
					.append(attributes.getCreated())
					.append(COMMA)
					.append(attributes.getModified())
					.append(COMMA);
			}
			userData.append(userData2.toString());
			userData.append("\n");
		}
		try (PrintWriter out = new PrintWriter(fileName)) {
			out.println(userData.toString());
		}
		System.out.println("Listed: " + users.size() + " users to " + System.getProperty("user.dir") + File.separator + fileName);
	}
}
