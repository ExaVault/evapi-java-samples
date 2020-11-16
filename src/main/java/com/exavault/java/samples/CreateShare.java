package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.api.SharesApi;
import com.exavault.client.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.exavault.java.samples.AddNotification.RESPONSE_CODE_201;
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
 * This example uses the {@link com.exavault.client.api.SharesApi} to create a shared folder with a password
 * <p>
 * We are demonstrating the use of the SharesApi, which is used for managing shared folders and receives, as well as
 * for sending upload.
 * <p>
 * See our <a href="https://www.exavault.com/docs/account/05-file-sharing/00-file-sharing-101">Sharing 101 documentation</a>
 * <p>
 * For this demo, we'll create a share for a new folder. If you have an existing file or folder that you want to use
 * for the share, you won't need this step where we use the ResourcesApi to create the folders first.
 * <p>An example output of this program would look like this:
 * <p>
 * <pre>
 * Created new folder /sample_share_[ID]
 * Created shared folder [HASH] for /sample_share_[ID]
 * Password to access the folder is [PASSWORD]
 * </pre>
 */
public class CreateShare {
	private static final String SAMPLE_SHARE = "/sample_share_";
	private static final String PASSWORD = "testpAssword8";
	private static Credential credential;
	private static ResourcesApi resourcesApi;
	private static SharesApi sharesApi;

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		credential = ApiClientHelper.getCredential();
		resourcesApi = new ResourcesApi(apiClient);
		sharesApi = new SharesApi(apiClient);
		Random random = new Random();
		long folderId = createAndGetFolderId(Math.abs(random.nextInt()));
		if (folderId != -1) {
			createShare(folderId);
		} else {
			System.out.println("Could not create a valid folder.");
		}
	}

	/**
	 * Create a folder to share it
	 * New Folder will have random name on each run of this program.
	 * <p>
	 * See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addFolder">addFolder</a>
	 * for the request requestBody schema
	 *
	 * @return
	 */
	private static long createAndGetFolderId(int random) {
		AddFolderRequestBody requestBody = new AddFolderRequestBody();
		requestBody.setPath(SAMPLE_SHARE + random);
		long folderId = 0;
		try {
			/* See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addFolder">addFolder</a>
			 *  for the details of this method
			 */
			ResourceResponse result = resourcesApi.addFolder(credential.getEvApiKey(), credential.getEvAccessToken(), requestBody);
			// Get id of the folder we've just created
			Resource data = result.getData();
			if (data != null) {
				folderId = data.getId();
			} else {
				folderId = -1; //set to invalid id to check further
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling ResourcesApi#addFolder" + getErrorStack(e));
		}
		return folderId;
	}

	/**
	 * Create a sharable folder
	 *
	 * @param folderId the folder id to be used for shared resource
	 */
	public static void createShare(long folderId) {
		/* See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addShare">addShare</a>
		 *  for the requestBody schema
		 */
		AddShareRequestBody requestBody = getRequestBody(folderId);
		try {
			/* See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addShare">addShare</a>
			 *  for the response schema
			 */
			ShareResponse result = sharesApi.addShare(credential.getEvApiKey(), credential.getEvAccessToken(), requestBody);
			//print out the response
			printResponse(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling SharesApi#addShare");
			e.printStackTrace();
		}
	}

	/**
	 * Flush the metric on the console
	 *
	 * @param result the share response
	 */
	private static void printResponse(ShareResponse result) {
		if (result != null) {
			if (result.getResponseStatus() == RESPONSE_CODE_201) {
				Share data = result.getData();
				if (data != null) {
					String folderPath = data.getAttributes().getPaths().get(0);
					System.out.println("Created new folder " + folderPath);
					System.out.println("Created shared folder " + data.getAttributes().getHash() + " for " + folderPath);
					System.out.println("Password to access the folder is " + PASSWORD);
				}
			}
		}
	}

	/**
	 * Following properties are being added for a new shared folder
	 * <p>
	 * <ul>
	 *  <li>We want to add a password to our folder</li>
	 *  <li>We are also going to allow visitors to download only</li>
	 * 	<li>We are using ID as identifier for the new folder we've created</li>
	 * 	<li>We could also have used the full path to the folder</li>
	 * </ul>
	 */
	private static AddShareRequestBody getRequestBody(long folderId) {
		AddShareRequestBody requestBody = new AddShareRequestBody();
		List<String> resources = Collections.singletonList("id:" + folderId);
		requestBody.setType(AddShareRequestBody.TypeEnum.SHARED_FOLDER);
		requestBody.setName("notification-sample");
		requestBody.setResources(resources);
		requestBody.setPassword(PASSWORD);
		requestBody.setAccessMode(Collections.singletonList(AddShareRequestBody.AccessModeEnum.DOWNLOAD));
		return requestBody;
	}
}
