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
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

class BuilderRequest {
	public String pkh;
	public String[] utxos;
}

public class Builder implements HttpFunction {
	private static final Gson gson = new Gson();

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		BuilderRequest body = gson.fromJson(request.getReader(), BuilderRequest.class);
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		for (int i = 0; i < body.utxos.length; i++) {
			inputs.add(TransactionInput.builder().transactionId(body.utxos[i]).build());
		}
		TransactionBody tbody = TransactionBody.builder().inputs(inputs).build();
		Transaction.TransactionBuilder txBuilder = Transaction.builder().body(tbody);
		BufferedWriter writer = response.getWriter();
		writer.write(txBuilder.build().serializeToHex());
	}
}
