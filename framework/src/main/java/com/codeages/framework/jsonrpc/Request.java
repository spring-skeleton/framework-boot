package com.codeages.framework.jsonrpc;

import lombok.Data;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id = String.valueOf(ThreadLocalRandom.current().nextDouble(10000, 10000000));
	private String jsonrpc = "2.0";
	private String method;
	private T[] params;
}
