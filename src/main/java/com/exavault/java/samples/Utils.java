package com.exavault.java.samples;

import com.exavault.client.ApiException;
import com.exavault.client.api.ResourcesApi;
import com.exavault.client.model.ResourceResponse;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Random;

/**
 * A collection of Utility Methods to be used during File Operations.
 */
public final class Utils {
	//Folder present under resource folder of the local project, Files present under this folder will be uploaded.
	private static final String LOCAL_FOLDER = "upload";
	private static final Random random = new Random();
	//we generate a random folder name with every run of this program
	public static final String BASE_PATH = "/Quick" + "_" + Math.abs(random.nextInt());

	/***
	 * Get all the files from the local folder
	 * @return file list
	 */
	public static File[] getAllLocalFiles() {
		URL url = UploadFiles.class.getClassLoader().getResource(LOCAL_FOLDER);
		if (url != null) {
			String path = url.getPath();
			return new File(path).listFiles();
		}
		return null;
	}

	/**
	 * Uploads a given file
	 *
	 * @param resourcesApi Resource API to be usd
	 * @param credential   Credentials
	 * @param file         file to be uploaded
	 * @return response back from resource API
	 * @throws ApiException request could not be completed normally
	 */
	public static ResourceResponse uploadFile(
		ResourcesApi resourcesApi, Credential credential, File file) throws ApiException {
		//Destination path for the file being uploaded, including the file name.
		String path = BASE_PATH + "/" + file.getName();
		//File size, in bits, of the file being uploaded.
		int fileSize = (int) file.length();
		//Allows a file upload to resume at a certain number of bytes.
		final int offsetBytes = 0;
		//True if upload resume is supported, false if it isn't.
		final boolean resume = false;
		//True if a file with the same name is found in the designated path, should be overwritten. False if different file names should be generated.
		final boolean allowOverwrite = false;

		/*
		 * The uploadFile method of the ResourcesApi class will let us upload a file to our account
		 * <p>
		 * See <a href="https://www.exavault.com/developer/api-docs/V2#operatiom/uploadFile">uploadFile</a>
		 * for the details of this method
		 */

		return resourcesApi.uploadFile(credential.getEvApiKey(), credential.getEvAccessToken(),
			path, fileSize, file, offsetBytes, resume, allowOverwrite);
	}

	//utility method to convert error stack trace to string
	public static String getErrorStack(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
