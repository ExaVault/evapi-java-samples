package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.UsersApi;
import com.exavault.client.model.AddUserRequestBody;
import com.exavault.client.model.User;
import com.exavault.client.model.UserResponse;
import com.exavault.client.model.UsersPermissions;

import java.util.Random;
import java.util.UUID;

import static com.exavault.java.samples.AddNotification.RESPONSE_CODE_201;
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
 * This example uses the {@link com.exavault.client.api.UsersApi} to create a new user with a home directory
 * <p>
 * We are demonstrating the use of the UsersApi, which is used to create, update and remove users in your account.
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * Created new user xyz-1UXLeMi9t35X0ggI8NfYq-1604022293 as ID #123456
 * </pre>
 */
public class CreateUser {
	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		Credential credential = ApiClientHelper.getCredential();
		// random value will allow to create unique user in the account
		UUID uuid = UUID.randomUUID();
		Random rand = new Random();
		UsersApi usersApi = new UsersApi(apiClient);
		/*
		 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/addUser">addUser</a>
		 * for the request requestBody schema
		 */
		AddUserRequestBody requestBody = new AddUserRequestBody();
		requestBody.setEmail("testuser@example.com");
		requestBody.setPassword("testpaSsword8");
		requestBody.setHomeResource("/");
		UsersPermissions permissions = new UsersPermissions()
			                               .delete(true)
			                               .download(true)
			                               .upload(true)
			                               .modify(true);
		requestBody.setPermissions(permissions);
		requestBody.setRole(AddUserRequestBody.RoleEnum.USER);
		requestBody.setTimeZone("America/Los_Angeles");
		requestBody.setUsername("testuser-" + uuid.toString().replaceAll("-", "") + "-" + Math.abs(rand.nextInt()));
		requestBody.setWelcomeEmail(true);
		try {
			/*
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/addUser">addUser</a>
			 * for the response requestBody schema
			 */
			UserResponse result = usersApi.addUser(credential.getEvApiKey(), credential.getEvAccessToken(), requestBody);
			//print out the response
			if (result != null) {
				if (result.getResponseStatus() == RESPONSE_CODE_201) {
					User data = result.getData();
					if (data != null) {
						System.out.println("Created new user " + data.getAttributes().getUsername() + " as ID #" + data.getId());
					}
				}
			}
		} catch (ApiException e) {
			//Customizing the error response from APIException
			System.err.println("Exception when calling UsersApi#addUser => \n" + Utils.customErrorResponse(e));
		}
	}
}
