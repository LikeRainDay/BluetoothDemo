package mobi.vhly.bluetoothdemo.app.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 16/1/18
 * Email: vhly@163.com
 */
public class DeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<BluetoothDevice> mDevices;

    public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
        mContext = context;
        mDevices = devices;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (mDevices != null) {
            ret = mDevices.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;

        if(convertView != null){
            ret = convertView;
        }else{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ret = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView txtName = (TextView) ret.findViewById(android.R.id.text1);
        TextView txtState = (TextView) ret.findViewById(android.R.id.text2);

        BluetoothDevice device = mDevices.get(position);

        txtName.setText(device.getName());

        int bondState = device.getBondState();
        String state = "未绑定";

        switch (bondState) {
            case BluetoothDevice.BOND_BONDED:
                state = "已绑定";
                break;
            case BluetoothDevice.BOND_NONE:
                state = "未绑定";
                break;
            case BluetoothDevice.BOND_BONDING:
                state = "绑定中";
                break;
        }
        txtState.setText(state);


        return ret;
    }
}
