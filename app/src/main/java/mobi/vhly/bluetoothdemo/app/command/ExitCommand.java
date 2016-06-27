package mobi.vhly.bluetoothdemo.app.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class ExitCommand extends AbstractCommand {

    public static final int ACTION = 2;

    public ExitCommand() {
        super(ACTION);
    }

    @Override
    protected void readContent(DataInputStream din) throws IOException {

    }

    @Override
    protected void writeContent(DataOutputStream dout) throws IOException {

    }
}
