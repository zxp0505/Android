package com.sample.performance;

public class TooDeepViewStackException extends RuntimeException
{
	private static final long serialVersionUID = 9145361450217030369L;

	public TooDeepViewStackException(String detailMessage)
	{
		super(detailMessage);
	}

}
