package com.example.response;

import java.util.List;

public class Result {

    private List<HerokuResponse> response = null;

    public List<HerokuResponse> getResponse() {
        return response;
    }

    public void setResponse(List<HerokuResponse> response) {
        this.response = response;
    }
}
