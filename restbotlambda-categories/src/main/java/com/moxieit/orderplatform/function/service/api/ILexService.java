package com.moxieit.orderplatform.function.service.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.response.LexResponse;

public interface ILexService {

	public LexResponse serveLex(ILexDTO lexDTO, Context context);

}
