package mobi.vhly.bluetoothdemo.app.command;

import org.bitbucket.vhly.blackfire.utils.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class AbstractCommandFactory implements CommandFactory {

    @Override
    public Command createCommand(int action) {
        Command ret = null;
        switch (action) {
            case EchoCommand.ACTION:
                ret = new EchoCommand("Echo");
                break;
        }
        return ret;
    }

    public Command getCommandFromData(byte[] data) {
        Command ret = null;
        if (data != null && data.length >= (Integer.SIZE >> 3)) {
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(bin);
            try {
                int action = din.readInt();
                ret = createCommand(action);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                StreamUtil.close(din);
                din = null;
                StreamUtil.close(bin);
                bin = null;
            }
        }
        return ret;
    }
}
