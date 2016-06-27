package mobi.vhly.bluetoothdemo.app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mobi.vhly.bluetoothdemo.app.adapters.DeviceAdapter;
import mobi.vhly.bluetoothdemo.app.services.ControlService;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = "Bluetooth";
    private boolean mBluetoothPermission;
    private boolean mBluetoothAdminPermission;

    private List<BluetoothDevice> mDeviceNames;
    private BaseAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mStartServer;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                if (intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    Log.d(TAG, "onReceive Device " + device);

                    int bondState = device.getBondState();
                    if (bondState == BluetoothDevice.BOND_NONE) {
                        String name = device.getName();
                        mDeviceNames.add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String uuid = UUID.randomUUID().toString();//能够产生全球唯一的字符串标识符

        Log.d(TAG, "UUID = " + uuid);


        int callingPid = Binder.getCallingPid();

        int callingUid = Binder.getCallingUid();

        int permission = checkPermission(Manifest.permission.BLUETOOTH, callingPid, callingUid);

        mDeviceNames = new ArrayList<BluetoothDevice>();
        mAdapter = new DeviceAdapter(this, mDeviceNames);

        ListView listView = (ListView) findViewById(R.id.device_list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);


        if (permission == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= 23) {
                // TODO: 检查权限
                boolean shouldShowRequestPermissionRationale =
                        shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH);

                if (shouldShowRequestPermissionRationale) {
                    requestPermissions(
                            new String[]{
                                    Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN
                            },
                            998
                    );
                }

            } else {
                // TODO: 普通使用蓝牙权限
            }
        } else {
            mBluetoothPermission = true;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBluetoothPermission) {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter != null) {

                boolean enabled = mBluetoothAdapter.isEnabled();

                if (enabled) {
                    // TODO: process
                    loadDevices();
                } else {
                    // TODO: Show open
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 999);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 998) { // for Bluetooth
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mBluetoothPermission = true;
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mBluetoothAdminPermission = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult " + data);
                loadDevices();
            }
        }
    }

    private void loadDevices() {

        if (!mStartServer) {
            Intent intent = new Intent(this, ControlService.class);
            startService(intent);
            mStartServer = true;
        }

        Set<BluetoothDevice> bondedDevices =
                mBluetoothAdapter.getBondedDevices();

        mDeviceNames.clear();

        if (bondedDevices != null && bondedDevices.size() > 0) {
            for (BluetoothDevice bondedDevice : bondedDevices) {

                Log.d(TAG, "loadDevices device = " + bondedDevice);

                String name = bondedDevice.getName();
                mDeviceNames.add(bondedDevice);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            scanDevices();
        }
    }

    private void scanDevices() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice bluetoothDevice = mDeviceNames.get(position);
        try {
            mBluetoothAdapter.cancelDiscovery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, ClientActivity.class);
        intent.putExtra(Constants.EXTRA_BLUETOOTH_DEVICE, bluetoothDevice);

        startActivity(intent);

    }

    public void btnEnableDiscover(View view) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent, 997);
    }
}
