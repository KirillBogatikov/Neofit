package org.cuba.neofit;

public interface NeoCallback {
    public void onResponse(NeoCall call, NeoResponse response);
    public void onFailure(NeoCall call, Throwable throwable);
}
