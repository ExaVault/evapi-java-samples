package com.exavault.java.samples;

/**
 * This class is the representation of the APIException error response
 * Contains the status code, error code and the detail about the error message.
 */
public class ErrorResponse {
	private final int statusCode;
	private final String errorCode;
	private final String errorDetail;

	public ErrorResponse(int statusCode, String errorCode, String errorDetail) {
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}

	@Override
	public String toString() {
		return "StatusCode=" + statusCode +
				", ErrorCode='" + errorCode + '\'' +
				", ErrorDetail='" + errorDetail + '\'' ;
	}

}
