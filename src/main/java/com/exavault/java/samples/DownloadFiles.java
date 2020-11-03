package com.exavault.java.samples;

import com.exavault.client.ApiClient;
import com.exavault.client.ApiException;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.model.Resource;
import com.exavault.client.model.ResourceCollectionResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.exavault.java.samples.Utils.getErrorStack;

/**
 * To use this sample, add your credentials to a file named {@code credentials.properties} which is located in {@code resources} folder at root level of your project.
 * <p>
 * <ul>
 *   <li>Your API key will be the {@code exavault.api.Key}</li>
 *   <li>Your access token will be {@code exavault.access.token}</li>
 *   <li>Your account URL will be the address you should use for the API endpoint</li>
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
 * This sample will use {@link com.exavault.client.api.ResourcesApi} to download all of the CSV upload found within a folder tree
 * <p>
 * We are demonstrating the use of the ResourcesApi, which can be used to manage upload and folders in your account
 * <p>An example output of this program would look like this:</p>
 * <p>
 * For this demo, we want to download all of the {@code CSV} files located within a certain folder.
 * <p>
 * Your account comes pre-loaded with a folder tree named {@code "Sample Files and Folders"}
 * which contains a folder tree containing many samples.
 * If you have renamed, deleted or moved this folder, this demo script will not work.
 * </p>
 * <pre>
 * Found 3 CSV files to download
 * /Sample Files and Folders/Sample Folder Structures/Clients/Alpha/sample data.csv
 * /Sample Files and Folders/Sample Folder Structures/Clients/Beta/sample sales data.csv
 * /Sample Files and Folders/sample_customer_data.csv
 * File(s) downloaded to /path/download.zip
 * </pre>
 */
public class DownloadFiles {
	//Resource identifier to get resources for. Can be path/id/name. Required if hash and path are null
	private static final String SAMPLE_FILES_AND_FOLDERS = "/Sample Files and Folders";
	private static final String PREFIX = "download-";
	private static final String ZIP = ".zip";
	private static final String CSV = ".csv";
	private static Credential credential;
	private static ResourcesApi resourcesApi;
	private static final Random result = new Random();

	public static void main(String[] args) {
		//Construct ApiClient and Credentials from evAccessToken, evApiKey and apiUrl.
		ApiClient apiClient = ApiClientHelper.initApiClient();
		credential = ApiClientHelper.getCredential();
		resourcesApi = new ResourcesApi(apiClient);
		List<String> allResourceIds = getAllResourceIds();
		if (allResourceIds.size() == 0) {
			System.out.println("Found no files to download");
		} else {
			downloadContent(allResourceIds);
		}
	}

	/**
	 * Now that we used the ResourcesApi to gather all of the IDs of the resources that
	 * matched our search, we will use the DownloadApi to download multiple files
	 * <p>
	 * The body of the result is the binary content of our file(s),
	 * We write that content into a single file, named with .zip if there were multiple files
	 * downloaded or just named .csv if not (since we were storing csvs)
	 *
	 * @param allResourceIds all resources
	 */
	private static void downloadContent(List<String> allResourceIds) {
		try {
			//If zipping multiple upload, the name of the zip file to create and download
			String downloadName;
			//Used when downloading multiple upload so url will be pulled till zip file is created
			final boolean polling = false;
			//Reference to the previously created zip for polling operation
			String pollingZipName = null;
			int random = Math.abs(result.nextInt());
			if (allResourceIds.size() > 1) {
				downloadName = PREFIX + random + ZIP;
			} else {
				downloadName = PREFIX + random + CSV; //only one file
			}
			File fileContent = resourcesApi.download(credential.getEvApiKey(), credential.getEvAccessToken(),
				allResourceIds, downloadName, polling, pollingZipName);
			saveToLocalFile(fileContent, downloadName);
		} catch (ApiException | IOException e) {
			System.err.println("Exception when calling ResourcesApi#download" + getErrorStack(e));
		}
	}

	/**
	 * copy the downloaded file to your local machine
	 * By default downloaded file will be copied to your project's main folder location
	 */
	private static void saveToLocalFile(File fileContent, String downloadName) throws IOException {
		Files.copy(Paths.get(fileContent.getAbsolutePath()), Paths.get(downloadName));
		System.out.println("File(s) downloaded to " + System.getProperty("user.dir") + File.separator + downloadName);
	}

	/**
	 * Now  we can download only .csv upload
	 * <p>
	 * We are using name param for searching only .csv upload
	 *
	 * @return list of resource id's with only .csv
	 */
	private static List<String> getAllResourceIds() {
		/*
		 * Endpoint support multiple sort fields by allowing array of sort params. Sort fields should be applied in the order specified
		 * The sort order for each sort field is ascending unless it is prefixed with a minus (“-“), in which case it will be descending
		 */
		final String sort = "name";
		//Determines which item to start on for pagination. Use zero (0) to start at the beginning of the list
		final int offset = 0;
		/*
		 * The number of upload to limit the result. Cannot be set higher than 100
		 * If you have more than one hundred upload in your directory,
		 * make multiple calls to **getResourceList**, incrementing the **offset** parameter, above
		 */
		final int limit = 50;
		//Optional param to get only folder resources
		String type = null;
		//Comma separated list of relationships to include in response. Possible values are `share`, `notification`, `directFile`, `parentNode`.
		String include = null;
		//Text to match resource names
		final String name = "*.csv";
		List<String> resources = new ArrayList<>();
		try {
			/*
			 * See <a href="https://www.exavault.com/developer/api-docs/V2#operation/listResources">listResources</a>
			 * for the response schema
			 */
			ResourceCollectionResponse result = resourcesApi.listResources(credential.getEvApiKey(), credential.getEvAccessToken(),
				SAMPLE_FILES_AND_FOLDERS, sort, offset, limit, type, name, include);
			System.out.println("Found " + result.getReturnedResults() + " CSV files to download");
			//get the data variable of the ResourceCollectionResponse response object
			List<Resource> data = result.getData();
			// it will be used to download only these upload
			for (Resource item : data) {
				resources.add("id:" + item.getId());
				System.out.println(item.getAttributes().getPath());
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling ResourcesApi#listResources" + getErrorStack(e));
		}
		return resources;
	}
}
