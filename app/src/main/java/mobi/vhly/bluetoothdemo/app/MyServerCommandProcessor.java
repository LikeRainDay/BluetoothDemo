package mobi.vhly.bluetoothdemo.app;

import android.content.Context;
import android.util.Log;
import mobi.vhly.bluetoothdemo.app.command.Command;
import mobi.vhly.bluetoothdemo.app.command.EchoCommand;
import mobi.vhly.bluetoothdemo.app.command.UnknownCommand;
import mobi.vhly.bluetoothdemo.app.communication.AbstractCommandProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/19
 * Email: vhly@163.com
 */
public class MyServerCommandProcessor extends AbstractCommandProcessor {

    public static final String TAG = "ServerProcessor";

    @Override
    protected Command processRequest(Command request) {
        Command ret = null;
        if (request != null) {

            if(request instanceof EchoCommand){
                EchoCommand echoCommand = (EchoCommand) request;
                String content = echoCommand.getContent();
                Log.d(TAG, "Echo Command request : " + content);
                ret = echoCommand;
            }

        }
        return ret;
    }

    @Override
    protected void processResponse(Command response) {
        if (response != null) {
            if(response instanceof UnknownCommand){
                Log.w(TAG, "response unknown");
            }
        }
    }
}
