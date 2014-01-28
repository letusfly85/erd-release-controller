package com.jellyfish85.erd.release.controller.bean

class GeneralValidateBean {

    private Boolean result  = false
    private String  message = ""

    public setResults(Boolean _result, String _message){
        this.result  = _result
        this.message = _message
    }

    public Boolean getResult() {
        return this.result
    }

    public String getMessage() {
        return this.message
    }

    public GeneralValidateBean() {

    }

}
