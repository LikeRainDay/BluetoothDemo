package mobi.vhly.bluetoothdemo.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import mobi.vhly.bluetoothdemo.app.command.AbstractCommandFactory;
import mobi.vhly.bluetoothdemo.app.command.Command;
import mobi.vhly.bluetoothdemo.app.command.EchoCommand;
import mobi.vhly.bluetoothdemo.app.command.ExitCommand;
import mobi.vhly.bluetoothdemo.app.communication.AbstractCommandProcessor;
import mobi.vhly.bluetoothdemo.app.communication.BlueClient;
import mobi.vhly.bluetoothdemo.app.communication.CommandTransport;

import java.io.IOException;
import java.util.UUID;

public class ClientActivity extends AppCompatActivity {

    private BlueClient mClient;
    private CommandTransport mTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        final Intent intent = getIntent();

        if (intent.hasExtra(Constants.EXTRA_BLUETOOTH_DEVICE)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice device = intent.getParcelableExtra(Constants.EXTRA_BLUETOOTH_DEVICE);
                    try {
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID_STR));
                        socket.connect();
                        mTransport = new CommandTransport(socket, new AbstractCommandFactory());
                        mClient = new BlueClient(mTransport, new MyCommandProcessor());
                        mClient.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (mTransport != null) {
            try {
                mTransport.sendCommand(new ExitCommand());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mClient.shutdown();
            mTransport = null;
        }
        super.onDestroy();
    }

    public void btnSendEcho(View view) {
        if (mTransport != null) {
            try {
                mTransport.sendCommand(new EchoCommand("HelloWorld"));
                Command command = mTransport.readCommand();
                if (command instanceof EchoCommand) {
                    EchoCommand echoCommand = (EchoCommand) command;
                    String content = echoCommand.getContent();
                    Toast.makeText(this, "content: " + content, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyCommandProcessor extends AbstractCommandProcessor {
        @Override
        protected Command processRequest(Command request) {
            return null;
        }

        @Override
        protected void processResponse(Command response) {
            if (response != null) {
                if (response instanceof EchoCommand) {
                    EchoCommand echoCommand = (EchoCommand) response;
                    String content = echoCommand.getContent();
                    Log.d("ClientProcessor", "content response ok : " + content);
                }
            }
        }
    }
}
