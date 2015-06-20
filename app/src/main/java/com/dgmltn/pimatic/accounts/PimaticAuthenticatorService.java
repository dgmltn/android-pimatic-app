package com.dgmltn.pimatic.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PimaticAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        PimaticAccountAuthenticator authenticator = new PimaticAccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
