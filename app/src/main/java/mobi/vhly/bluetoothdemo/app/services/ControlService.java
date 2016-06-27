package mobi.vhly.bluetoothdemo.app.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import mobi.vhly.bluetoothdemo.app.Constants;
import mobi.vhly.bluetoothdemo.app.MyServerCommandProcessor;
import mobi.vhly.bluetoothdemo.app.communication.BlueServer;

public class ControlService extends Service {


    public ControlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BlueServer mBlueServer;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (defaultAdapter != null) {
            mBlueServer = new BlueServer(defaultAdapter, "BTServer", Constants.UUID_STR);
            mBlueServer.setCommandProcessor(new MyServerCommandProcessor());
            mBlueServer.start();
        }
    }

    @Override
    public void onDestroy() {

        if (mBlueServer != null) {
            mBlueServer.shutdown();
            mBlueServer = null;
        }

        super.onDestroy();
    }
}
