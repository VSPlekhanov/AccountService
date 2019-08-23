package com.revolut.accountservice;/*
 * Copyright 2001-2019 by HireRight, Inc. All rights reserved.
 * This software is the confidential and proprietary information
 * of HireRight, Inc. Use is subject to license terms.
 */

import static spark.Spark.get;

public class App
{
	public static void main(String[] args) {
		get("/hello", (req, res) -> "Hello, World!");
	}
}
