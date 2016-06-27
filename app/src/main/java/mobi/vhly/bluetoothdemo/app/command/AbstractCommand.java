package mobi.vhly.bluetoothdemo.app.command;

import org.bitbucket.vhly.blackfire.utils.StreamUtil;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public abstract class AbstractCommand implements Command {

    protected int mAction;

    protected boolean mResult;

    public AbstractCommand(int action) {
        mAction = action;
    }

    @Override
    public int getAction() {
        return mAction;
    }

    @Override
    public boolean isResult() {
        return mResult;
    }

    public void setResult(boolean result) {
        mResult = result;
    }

    protected abstract void readContent(DataInputStream din) throws IOException;

    @Override
    public void readData(byte[] data) {
        if (data != null) {
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(bin);

            try {
                int action = din.readInt();

                if (action == mAction) {

                    mResult = din.readBoolean();

                    readContent(din);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                StreamUtil.close(din);
                din = null;
                StreamUtil.close(bin);
                bin = null;
            }
        }
    }

    protected abstract void writeContent(DataOutputStream dout) throws IOException;

    @Override
    public byte[] toByteArray() {
        byte[] ret = null;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeInt(mAction);

            dout.writeBoolean(mResult);

            writeContent(dout);

            ret = bout.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(dout);
            dout = null;
            StreamUtil.close(bout);
            bout = null;
        }


        return ret;
    }
}
