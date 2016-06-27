package mobi.vhly.bluetoothdemo.app.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import mobi.vhly.bluetoothdemo.app.command.AbstractCommandFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class BlueServer extends Thread {

    private BluetoothServerSocket mServerSocket;
    private boolean mRunning;

    private ThreadGroup mSocketGroup;

    private CommandProcessor mCommandProcessor;

    public BlueServer(BluetoothAdapter bluetoothAdapter, String name, String uuid) {
        if (bluetoothAdapter != null && name != null && uuid != null) {
            try {
                BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, UUID.fromString(uuid));
                mServerSocket = serverSocket;
                mSocketGroup = new ThreadGroup("BlueClients");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCommandProcessor(CommandProcessor commandProcessor) {
        mCommandProcessor = commandProcessor;
    }

    public void shutdown() {
        mRunning = false;
    }

    @Override
    public void run() {
        if (mServerSocket != null && mCommandProcessor != null) {
            mRunning = true;
            try {
                while (mRunning) {
                    BluetoothSocket socket = mServerSocket.accept();
                    CommandTransport transport = new CommandTransport(socket, new AbstractCommandFactory());

                    BlueWorker client = new BlueWorker(transport, mCommandProcessor, mSocketGroup);
                    client.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mSocketGroup.destroy();

            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mServerSocket = null;

            mSocketGroup = null;
        }
    }
}
