package mobi.vhly.bluetoothdemo.app.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public interface Command {

    int getAction();

    boolean isResult();

    void setResult(boolean result);

    void readData(byte[] data);

    byte[] toByteArray();
}
