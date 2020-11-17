package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.model.ResourceResponse;

import java.io.File;

import static com.exavault.java.samples.AddNotification.RESPONSE_CODE_201;
import static com.exavault.java.samples.Utils.BASE_PATH;
import static com.exavault.java.samples.Utils.getAllLocalFiles;
import static com.exavault.java.samples.Utils.getErrorStack;
import static com.exavault.java.samples.Utils.uploadFile;

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
 * This example uses the {@link com.exavault.client.api.ResourcesApi} to upload a file to your account
 * <p>
 * We are demonstrating the use of the ResourcesApi, which can be used to manage upload and folders in your account
 * For this demo, we'll upload a file found in <i>{@code /resources/upload}</i> folder inside this project. There are parameters to control
 * whether upload can be overwritten by repeated uploads
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * Uploaded /Quick_1321256739 Start1604022386.pdf
 * </pre>
 */
public class UploadFiles {
	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		Credential credential = ApiClientHelper.getCredential();
		ResourcesApi resourcesApi = new ResourcesApi(apiClient);
		try {
			File[] files = getAllLocalFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					//upload only files, no directories
					if (file.isFile()) {
						ResourceResponse result = uploadFile(resourcesApi, credential, file);
						if (result != null) {
							validateAndPrintResponse(result, file.getName());
						}
					}
				}
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling ResourcesApi#uploadFile" + getErrorStack(e));
		}
	}

	/**
	 * Validate the response code and print the uploaded file's name
	 *
	 * @param result   Response object
	 * @param fileName filename uploaded
	 */
	private static void validateAndPrintResponse(ResourceResponse result, String fileName) {
		if (result.getResponseStatus() == RESPONSE_CODE_201) {
			System.out.println("Uploaded " + BASE_PATH + "/" + fileName);
		}
	}
}
