package com.kookyapps.gpstankertracking.Utils;

public class HeadersUtil {
    String authorization;
    public HeadersUtil(){
        this.authorization = null;
    }
    public HeadersUtil(String authorization){

        this.authorization = "Bearer " + authorization;
    }
    public HeadersUtil(String content_type, String authorization){

        this.authorization = "Bearer " + authorization;
    }
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
