package org.kllbff.neofit;

import java.io.IOException;

public interface NeoCallback {
    public void onResponse(NeoCall call, NeoResponse response) throws IOException;
    public void onFailure(NeoCall call, Throwable throwable);
}
