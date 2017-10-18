package com.lsh.payment.api.model.baseVo;

public class QueryResponse extends BaseResponse {

    private QueryContent content;

    public QueryContent getContent() {
        return content;
    }

    public void setContent(QueryContent content) {
        this.content = content;
    }
}
