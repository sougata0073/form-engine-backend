package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.QuestionResponseAddReq;

public abstract class ResponseManager<QR extends QuestionResponseAddReq> {

    public abstract void create(QR response);

}
