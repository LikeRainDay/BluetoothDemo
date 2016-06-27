package mobi.vhly.bluetoothdemo.app.communication;

import mobi.vhly.bluetoothdemo.app.command.Command;
import mobi.vhly.bluetoothdemo.app.command.ExitCommand;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class BlueClient extends Thread {

    private boolean mRunning;

    private Queue<Command> mCommandQueue;

    private CommandTransport mCommandTransport;

    private CommandProcessor mCommandProcessor;

    public BlueClient(CommandTransport transport, CommandProcessor processor) {
        mCommandTransport = transport;
        mCommandQueue = new LinkedList<Command>();
        mCommandProcessor = processor;
    }

    public void sendCommand(Command command) {
        if (command != null) {
            mCommandQueue.offer(command);
        }
    }

    public void shutdown() {
        mRunning = false;
    }

    @Override
    public void run() {
        mRunning = true;
        try {
            while (mRunning) {
                Command command = mCommandQueue.poll();
                if (command != null) {
                    mCommandTransport.sendCommand(command);

                    Command resultCommand = mCommandTransport.readCommand();

                    if (resultCommand instanceof ExitCommand) {
                        mRunning = false;
                        break;
                    } else if (mCommandProcessor != null) {
                        resultCommand = mCommandProcessor.process(resultCommand);
                        if (resultCommand != null && resultCommand.isResult()) {
                            mCommandTransport.sendCommand(resultCommand);
                        }
                    }


                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCommandTransport.shutdown();
        }

    }
}
