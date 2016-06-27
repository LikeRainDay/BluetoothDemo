package mobi.vhly.bluetoothdemo.app.communication;

import mobi.vhly.bluetoothdemo.app.command.Command;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/19
 * Email: vhly@163.com
 */
public interface CommandProcessor {

    /**
     * Process Command request or response
     * @param command
     * @return
     */
    Command process(Command command);

}
