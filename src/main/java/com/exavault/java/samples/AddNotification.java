package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.NotificationsApi;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
 * We are demonstrating the use of the NotificationsApi, which can be used to manage notification settings for upload
 * and folders
 * <p>
 * For this demo, we'll create a new folder and add notification on it
 * <p>
 * If you have an existing file or folder that
 * you want to create a notification for, you won't need the step where we use the ResourcesApi to create the
 * folders first. Set {@code isCreateFolder} to {@code false}, and {@code folderId} to your existing folderid
 * <p>
 * A sample output from this program should look alike this:
 * <pre>
 * {@code
 * Created new folder /sample_notifications_[ID]/uploads
 * Created new folder /sample_notifications_[ID]/downloads
 * Created download notification for /sample_notifications_[ID]/downloads
 * Created upload  notification for /sample_notifications_[ID]/uploads
 * }</pre>
 */
public class AddNotification {

	private static final String BASE_FOLDER_NAME_UPLOAD = "/sample_notifications_%d/uploads";
	private static final String BASE_FOLDER_NAME_DOWNLOAD = "/sample_notifications_%d/downloads";
	private static final String USER_ALL = "notice_user_all";
	//dummy email addresses, use actual email addresses to get notified instead
	private static final String TEST_EMAIL1 = "testnotifications@example.com";
	private static final String TEST_EMAIL2 = "testnotifications1@example.com";
	private static final String TEST_EMAIL3 = "testnotifications2@example.com";
	private static final String UPLOADED = "New upload have been uploaded";
	private static final String DOWNLOADED = "New upload have been downloaded";
	public static final int RESPONSE_CODE_201 = 201;
	private static final String CREATED_NEW_FOLDER = "Created new folder ";
	private static Credential credential;
	private static ResourcesApi resourcesApi;
	private static NotificationsApi notificationsApi;

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		credential = ApiClientHelper.getCredential();
		resourcesApi = new ResourcesApi(apiClient);
		notificationsApi = new NotificationsApi(apiClient);
		Random random = new Random();
		int nextInt = Math.abs(random.nextInt());
		NotificationResponse uploadResponse = createFolderAndAddNotification(true, nextInt);
		NotificationResponse downloadResponse = createFolderAndAddNotification(false, nextInt);
		if (downloadResponse != null && uploadResponse != null) {
			if (uploadResponse.getResponseStatus() == RESPONSE_CODE_201 && downloadResponse.getResponseStatus() == RESPONSE_CODE_201) {
				String uploadPath = uploadResponse.getData().getAttributes().getPath();
				String downloadPath = downloadResponse.getData().getAttributes().getPath();
				System.out.println(CREATED_NEW_FOLDER + uploadPath);
				System.out.println(CREATED_NEW_FOLDER + downloadPath);
				System.out.println("Created download notification for " + downloadPath);
				System.out.println("Created upload notification for " + uploadPath);
			}
		}
	}

	//create folder and add notification to it
	private static NotificationResponse createFolderAndAddNotification(boolean isUpload, int randomNumber) {
		long folderId = createAndGetFolderId(randomNumber, isUpload);
		//Add Notification only if valid folderId is used
		if (folderId != -1) {
			return addNotification(folderId, isUpload);
		} else {
			System.out.println("Could not create a valid folder.");
			return null;
		}
	}

	/**
	 * Now we will create notification
	 * <p>
	 * See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addNotification">addNotification</a>
	 * for the request body schema
	 *
	 * @param folderId the folder id on which we need to set the notification
	 * @param isUpload if request is upload or download
	 * @return NotificationResponse the response
	 */
	private static NotificationResponse addNotification(long folderId, boolean isUpload) {
		AddNotificationRequestBody requestBody = constructBody(folderId, isUpload);
		try {
			/*
			 * The addNotification method of the NotificationsApi returns a NotificationResponse object
			 * See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addNotification">addNotification</a>
			 *  for the details of the response object
			 */
			return notificationsApi.addNotification(credential.getEvApiKey(), credential.getEvAccessToken(), requestBody);
		} catch (ApiException e) {
			// In order to capture the detailed error message from the ExaVault API, we are using a custom ErrorResponse class to parse the ApiException
			System.err.println("Exception when calling NotificationsApi#addNotification => \n" + Utils.customErrorResponse(e));
			return null;
		}
	}

	/**
	 * We will pass in the id identifier of the folder in the resource parameter
	 * <p>
	 * We want to be notified by email whenever anyone uploads to our folder, so we are using
	 * the constant "notice_user_all", which means anyone, including users and share recipients
	 * <p>
	 * See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addNotification">addNotification</a>
	 * for a list of other constants that can be used in the usernames array
	 * <p>
	 * We are sending the notification to a bunch of email addresses
	 * <p>
	 * We have added an optional custom message to be included in each notification email
	 * <p>
	 *
	 * @param folderId newly created folder id
	 * @param isUpload if upload or download
	 * @return constructed requestBody object
	 */
	private static AddNotificationRequestBody constructBody(long folderId, boolean isUpload) {
		AddNotificationRequestBody requestBody = new AddNotificationRequestBody();
		List<String> usernames = Collections.singletonList(USER_ALL);
		List<String> recipients = Arrays.asList(TEST_EMAIL1, TEST_EMAIL2, TEST_EMAIL3);
		requestBody.setResource("id:" + folderId);
		requestBody.setUsernames(usernames);
		requestBody.setRecipients(recipients);
		if (isUpload) {
			requestBody.setAction(AddNotificationRequestBody.ActionEnum.UPLOAD);
			requestBody.setMessage(UPLOADED);
		} else {
			requestBody.setAction(AddNotificationRequestBody.ActionEnum.DOWNLOAD);
			requestBody.setMessage(DOWNLOADED);
		}
		requestBody.setType(AddNotificationRequestBody.TypeEnum.FOLDER);
		requestBody.setSendEmail(true);
		return requestBody;
	}

	/**
	 * Create a folder to set notification on it.
	 * New Folder will have random name on each run of this program
	 * <p>
	 * See <a href="https://www.exavault.com/developer/api-docs/v2#operation/addFolder">addFolder</a> for the request body schema
	 *
	 * @return
	 */
	private static long createAndGetFolderId(int randomNumber, boolean isUpload) {
		AddFolderRequestBody requestBody = new AddFolderRequestBody();
		if (isUpload) {
			requestBody.setPath(String.format(BASE_FOLDER_NAME_UPLOAD, randomNumber));
		} else {
			requestBody.setPath(String.format(BASE_FOLDER_NAME_DOWNLOAD, randomNumber));
		}
		long folderId = 0;
		try {
			//The addFolder method of the ResourcesApi returns a ResourceResponse object
			ResourceResponse result = resourcesApi.addFolder(credential.getEvApiKey(), credential.getEvAccessToken(), requestBody);
			// Get new folder id so we can than set notification on it
			Resource data = result.getData();
			if (data != null) {
				folderId = data.getId();
			} else {
				folderId = -1; //set to invalid id to check further
			}
		} catch (ApiException e) {
			// In order to capture the detailed error message from the ExaVault API, we are using a custom ErrorResponse class to parse the ApiException
			System.err.println("Exception when calling ResourcesApi#addFolder => \n" + Utils.customErrorResponse(e));
		}
		return folderId;
	}
}
