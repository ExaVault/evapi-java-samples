package com.evault.java.samples.account;

/**
 * Entity to keep variables to connect to {@link com.exavault.client.ApiClient}
 */

public class Credential {

	//API key
	private final String evApiKey;
	//Access Token
	private final String evAccessToken;
	//Account URL
	private final String apiUrl;

	public Credential(String evApiKey, String evAccessToken, String apiUrl) {
		this.evApiKey = evApiKey;
		this.evAccessToken = evAccessToken;
		this.apiUrl = apiUrl;
	}

	public String getEvApiKey() {
		return evApiKey;
	}

	public String getEvAccessToken() {
		return evAccessToken;
	}

	public String getApiUrl() {
		return apiUrl;
	}
}
