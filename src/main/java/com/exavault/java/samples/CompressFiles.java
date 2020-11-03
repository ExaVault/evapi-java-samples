package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.model.Body10;
import com.exavault.client.model.ResourceResponse;

import java.io.File;
import java.util.Collections;
import java.util.List;

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
 * This sample will use {@link com.exavault.client.api.ResourcesApi} to compress upload
 * <p>
 * We are demonstrating the use of the {@link com.exavault.client.api.ResourcesApi}, which is used for file operations (upload, download, delete etc)
 * <p>
 * For this demo, we'll create a new folder(if not exist) and upload some upload into it. Then we'll compress all
 * upload into a new zip file in the folder<p>
 * <p>An example output of this program would look like this:</p>
 * <pre>
 * Uploaded files at:  sample_compress_1604022300
 * Created archive at /compress-sample.zip
 * </pre>
 */
public class CompressFiles {

	private static final String ARCHIVE_NAME = "compress-sample.zip";
	private static Credential credential;
	private static ResourcesApi resourcesApi;

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl
		ApiClient apiClient = ApiClientHelper.initApiClient();
		credential = ApiClientHelper.getCredential();
		resourcesApi = new ResourcesApi(apiClient);
		upload();
		System.out.println("Uploaded files at: " + BASE_PATH);
		compress();
	}

	//upload routine
	private static void upload() {
		try {
			File[] files = getAllLocalFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					//upload only files, no directories
					if (file.isFile()) {
						uploadFile(resourcesApi, credential, file);
					}
				}
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling ResourcesApi#uploadFile" + getErrorStack(e));
		}
	}

	/**
	 * Now we will compress all the uploaded files
	 * <p>
	 * See <a href="https://www.exavault.com/developer/api-docs/V2#operation/compressFiles">compressFiles</a>
	 * for the request body schema
	 */
	private static void compress() {
		Body10 body = new Body10();
		//base folder to take files from to compress, use the same folder where we uploaded files to in above call
		List<String> resources = Collections.singletonList(BASE_PATH);
		//set compressed archive name
		body.setArchiveName(ARCHIVE_NAME);
		body.setResources(resources);
		body.setParentResource("/");
		try {
			/*
			 * The compressFiles method of the ResourcesApi returns a ResourceResponse object
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operation/compressFiles">compressFiles</a>
			 * for the details of the response object
			 */

			ResourceResponse result = resourcesApi.compressFiles(credential.getEvApiKey(), credential.getEvAccessToken(), body);
			validateAndPrint(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling ResourcesApi#compressFiles" + getErrorStack(e));
		}
	}

	//validate response code and print the metrics on the console
	private static void validateAndPrint(ResourceResponse result) {
		if (result.getResponseStatus() == RESPONSE_CODE_201) {
			System.out.println("Created archive at " + result.getData().getAttributes().getPath());
		}
	}
}
