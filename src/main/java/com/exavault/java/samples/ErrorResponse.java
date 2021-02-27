package com.exavault.java.samples;

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
