package com.qcloud.scf.runtime;

public abstract interface Context {
	public abstract String getRequestId();

	public abstract int getTimeLimitInMs();

	public abstract int getMemoryLimitInMb();

	// public abstract String getFunctionName();

	// public abstract String getFunctionVersion();

	// public abstract String getMemoryLimitInMb();

	// public abstract String getTimeLimitInMs();

	// public abstract String getLogGroupName();

	// public abstract String getLogStreamName();

}
