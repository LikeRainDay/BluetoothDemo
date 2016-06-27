package mobi.vhly.bluetoothdemo.app.communication;

import mobi.vhly.bluetoothdemo.app.command.Command;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/19
 * Email: vhly@163.com
 */
public abstract class AbstractCommandProcessor implements CommandProcessor {

    @Override
    public Command process(Command command) {
        Command ret = null;
        if (command != null) {
            if(command.isResult()){
                processResponse(command);
            }else{
                ret = processRequest(command);
                ret.setResult(true);
            }
        }
        return ret;
    }

    /**
     * 处理请求
     * @param request
     * @return
     */
    protected abstract Command processRequest(Command request);

    /**
     * 处理响应
     * @param response
     */
    protected abstract void processResponse(Command response);
}
