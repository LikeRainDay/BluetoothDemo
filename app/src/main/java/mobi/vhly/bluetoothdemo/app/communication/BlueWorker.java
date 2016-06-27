package mobi.vhly.bluetoothdemo.app.communication;

import android.util.Log;
import mobi.vhly.bluetoothdemo.app.command.Command;
import mobi.vhly.bluetoothdemo.app.command.ExitCommand;
import mobi.vhly.bluetoothdemo.app.command.UnknownCommand;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class BlueWorker extends Thread {


    public static final String TAG = "BlueWorker";
    private CommandTransport mCommandTransport;

    private CommandProcessor mCommandProcessor;

    public BlueWorker(CommandTransport transport, CommandProcessor processor, ThreadGroup group) {
        super(group, "BlueWorker " + System.currentTimeMillis());
        mCommandTransport = transport;
        mCommandProcessor = processor;
    }

    @Override
    public void run() {
        try {
            if (mCommandTransport != null) {
                while (true) {
                    Command command = mCommandTransport.readCommand();

                    Log.d(TAG, "read command " + command);

                    if (command != null) {
                        Command resultCommand = null;
                        if (command instanceof ExitCommand) {
                            mCommandTransport.sendCommand(command);
                            break;
                        } else if (mCommandProcessor != null) {
                            resultCommand = mCommandProcessor.process(command);
                        } else {
                            Log.e(TAG, "Unknown command");
                            resultCommand =  new UnknownCommand();
                            resultCommand.setResult(true);
                        }

                        if (resultCommand != null) {
                            mCommandTransport.sendCommand(resultCommand);
                        }
                        resultCommand = null;
                        command = null;
                    }
                }
            } else {
                Log.e(TAG, "CommandTransport must be set!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCommandTransport.shutdown();
        }
    }


}
