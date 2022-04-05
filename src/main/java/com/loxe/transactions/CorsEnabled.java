package com.loxe.transactions;

import com.bloxbean.cardano.client.api.helper.TransactionBuilder;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.TransactionBody;
import com.bloxbean.cardano.client.transaction.spec.TransactionInput;
import com.bloxbean.cardano.client.transaction.spec.TransactionBody.TransactionBodyBuilder;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class CorsEnabled implements HttpFunction {
  // corsEnabled is an example of setting CORS headers.
  // For more information about CORS and CORS preflight requests, see
  // https://developer.mozilla.org/en-US/docs/Glossary/Preflight_request.
  @Override
  public void service(HttpRequest request, HttpResponse response)
      throws IOException {
    // Set CORS headers
    //   Allows GETs from any origin with the Content-Type
    //   header and caches preflight response for 3600s
    response.appendHeader("Access-Control-Allow-Origin", "*");

    if ("OPTIONS".equals(request.getMethod())) {
      response.appendHeader("Access-Control-Allow-Methods", "GET");
      response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
      response.appendHeader("Access-Control-Max-Age", "3600");
      response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
      return;
    }

    // Handle the main request.
    BufferedWriter writer = response.getWriter();
    writer.write("CORS headers set successfully!");
  }
}


