package mobi.vhly.bluetoothdemo.app.command;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public interface CommandFactory {
    /**
     * 根据action创建Command对象;
     * @param action
     * @return
     */
    Command createCommand(int action);

    Command getCommandFromData(byte[] data);

}
