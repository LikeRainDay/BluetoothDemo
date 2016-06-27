package mobi.vhly.bluetoothdemo.app.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import mobi.vhly.bluetoothdemo.app.command.AbstractCommandFactory;
import mobi.vhly.bluetoothdemo.app.command.Command;
import mobi.vhly.bluetoothdemo.app.command.CommandFactory;
import org.bitbucket.vhly.blackfire.utils.StreamUtil;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class CommandTransport {


    public static final int DIGEST_NONE = 0;
    public static final int DIGEST_CRC = 1;
    public static final int DIGEST_MD5 = 2;
    public static final int DIGEST_SHA1 = 3;
    public static final int MAGIC = 0xFF99CC00;
    public static final String TAG = "CommandTransport";
    private CRC32 mCrc32;
    private MessageDigest mDigestMd5;
    private MessageDigest mDigestSha1;

    private CommandFactory mCommandFactory;

    private DataInputStream mInputStream;
    private DataOutputStream mOutputStream;

    private BluetoothSocket mSocket;

    public CommandTransport(BluetoothSocket socket, CommandFactory commandFactory) {

        mSocket = socket;

        if (commandFactory == null) {
            mCommandFactory = new AbstractCommandFactory();
        } else {
            mCommandFactory = commandFactory;
        }

        try {
            OutputStream outputStream = mSocket.getOutputStream();
            mOutputStream = new DataOutputStream(outputStream);

            InputStream inputStream = mSocket.getInputStream();
            mInputStream = new DataInputStream(inputStream);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        mCommandFactory = commandFactory;
    }

    public Command readCommand() throws Exception {
        Command ret = null;
        byte[] data = readData(mInputStream);
        if (data != null) {
            if (mCommandFactory != null) {
                ret = mCommandFactory.getCommandFromData(data);
                if (ret != null) {
                    ret.readData(data);
                }
            }
        }
        return ret;
    }

    public void sendCommand(Command command) throws Exception {
        if (command != null) {
            byte[] data = command.toByteArray();
            sendData(mOutputStream, data, DIGEST_SHA1);
        }
    }

    public void sendData(DataOutputStream dout, byte[] data, int digestType) throws IOException {

        Log.d(TAG, "sendData " + dout);

        if (dout != null && data != null) {

            dout.writeInt(MAGIC);

            int dataLen = data.length;

            dout.writeInt(dataLen);

            if (dataLen > 0) {

                dout.write(data);

                dout.write(digestType);

                switch (digestType) {
                    case DIGEST_NONE:
                        break;
                    case DIGEST_CRC:
                        if (mCrc32 == null) {
                            mCrc32 = new CRC32();
                        }
                        mCrc32.reset();

                        mCrc32.update(data);
                        long calcValue = mCrc32.getValue();
                        dout.writeLong(calcValue);
                        break;
                    case DIGEST_MD5:

                        if (mDigestMd5 == null) {
                            try {
                                mDigestMd5 = MessageDigest.getInstance("MD5");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        mDigestMd5.reset();
                        mDigestMd5.update(data);
                        byte[] digest = mDigestMd5.digest();
                        dout.write(digest);
                        digest = null;

                        break;
                    case DIGEST_SHA1:
                        if (mDigestSha1 == null) {
                            try {
                                mDigestSha1 = MessageDigest.getInstance("SHA1");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        mDigestSha1.reset();

                        mDigestSha1.update(data);

                        digest = mDigestSha1.digest();

                        dout.write(digest);

                        digest = null;

                        break;
                }
            }
            dout.flush();
        }

    }

    public byte[] readData(DataInputStream din) throws IOException {

        Log.d(TAG, "readData " + din);

        byte[] ret = null;
        int magic = din.readInt(); // 0 - 3  int magic must is 0xFF99CC00
        if (magic == MAGIC) {

            int dataLen = din.readInt();

            if (dataLen > 0) {

                byte[] data = new byte[dataLen];

                din.readFully(data);

                boolean verified = false;

                int digestType = din.read();
                switch (digestType) {
                    case DIGEST_NONE:
                        verified = true;
                        break;
                    case DIGEST_CRC:
                        if (mCrc32 == null) {
                            mCrc32 = new CRC32();
                        }
                        mCrc32.reset();

                        mCrc32.update(data);
                        long calcValue = mCrc32.getValue();
                        long l = din.readLong();
                        verified = calcValue == l;
                        break;
                    case DIGEST_MD5:

                        if (mDigestMd5 == null) {
                            try {
                                mDigestMd5 = MessageDigest.getInstance("MD5");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        mDigestMd5.reset();
                        mDigestMd5.update(data);
                        byte[] digest = mDigestMd5.digest();

                        byte[] md5 = new byte[digest.length];
                        din.readFully(md5);

                        verified = Arrays.equals(digest, md5);

                        md5 = null;

                        digest = null;

                        break;
                    case DIGEST_SHA1:
                        if (mDigestSha1 == null) {
                            try {
                                mDigestSha1 = MessageDigest.getInstance("SHA1");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        mDigestSha1.reset();

                        mDigestSha1.update(data);

                        digest = mDigestSha1.digest();

                        byte[] sha1 = new byte[digest.length];
                        din.readFully(sha1);

                        verified = Arrays.equals(digest, sha1);

                        sha1 = null;
                        digest = null;

                        break;
                }

                if (verified) {
                    ret = data;
                }
            }
        }
        return ret;
    }

    public void shutdown() {
        StreamUtil.close(mOutputStream);
        mOutputStream = null;
        StreamUtil.close(mInputStream);
        mInputStream = null;
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
