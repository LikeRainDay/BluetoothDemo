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
public class EchoCommand extends AbstractCommand {

    public static final int ACTION = 1;

    private String mContent;

    private boolean mBack;

    public EchoCommand(String content) {
        super(ACTION);
        mContent = content;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public boolean isBack() {
        return mBack;
    }

    public void setBack(boolean back) {
        mBack = back;
    }

    @Override
    protected void readContent(DataInputStream din) throws IOException {
        mContent = din.readUTF();
        mBack = din.readBoolean();
    }

    @Override
    protected void writeContent(DataOutputStream dout) throws IOException {
        if (mContent == null) {
            mContent = "";
        }
        dout.writeUTF(mContent);
        dout.writeBoolean(mBack);
    }
}
