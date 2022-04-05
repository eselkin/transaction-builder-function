package com.loxe.transactions;

import com.bloxbean.cardano.client.api.helper.TransactionBuilder;
import com.bloxbean.cardano.client.plutus.api.PlutusObjectConverter;
import com.bloxbean.cardano.client.transaction.spec.BytesPlutusData;
import com.bloxbean.cardano.client.transaction.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.transaction.spec.PlutusData;
import com.bloxbean.cardano.client.transaction.spec.Redeemer;
import com.bloxbean.cardano.client.transaction.spec.RedeemerTag;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.TransactionBody;
import com.bloxbean.cardano.client.transaction.spec.TransactionInput;
import com.bloxbean.cardano.client.transaction.spec.TransactionBody.TransactionBodyBuilder;
import com.bloxbean.cardano.client.transaction.util.CostModelConstants;
import com.bloxbean.cardano.client.transaction.util.ScriptDataHashGenerator;
import com.bloxbean.cardano.client.util.HexUtil;
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

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;

class BuilderRequest {
	public String pkh;
	public String[] utxos;
}

public class Builder implements HttpFunction {
	private static final Gson gson = new Gson();

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
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


 		BuilderRequest body = gson.fromJson(request.getReader(), BuilderRequest.class);
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		for (int i = 0; i < body.utxos.length; i++) {
			inputs.add(TransactionInput.builder().transactionId(body.utxos[i]).build());
		}
		ArrayList<PlutusData> pd = new ArrayList<PlutusData>();
		ArrayList<Redeemer> rl = new ArrayList<Redeemer>();
		ConstrPlutusData data = ConstrPlutusData.of(1,
                BytesPlutusData.of(HexUtil.decodeHexString(body.pkh)));
		pd.add(data);
		byte[] sdh = ScriptDataHashGenerator.generate(rl, pd, CostModelConstants.LANGUAGE_VIEWS);
		TransactionBody tbody = TransactionBody.builder().inputs(inputs).scriptDataHash(sdh).build();
		Transaction.TransactionBuilder txBuilder = Transaction.builder().body(tbody);
		BufferedWriter writer = response.getWriter();
		writer.write(txBuilder.build().serializeToHex());
	}
}
