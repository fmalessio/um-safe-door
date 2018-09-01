package safedoorum.fmalessio.safedoorum;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothListActivity extends AppCompatActivity {

    // Attributes
    static final int REQUEST_ACTIVATE_BT = 1;
    private final String STRING_BT_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    // UI
    private ListView listView;
    private ArrayAdapter aAdapter;
    private BluetoothAdapter myBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI refences
        listView = findViewById(R.id.listView);

        setUIConfigs();

        startMyBluetooth();
    }

    // UI configurations
    private void setUIConfigs() {
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetooth = (BluetoothDevice) parent.getAdapter().getItem(position);
                sendDeviceInfoAndClose(bluetooth);
            }
        });
    }

    /**
     * // UUID
     * BT_DEVICE_ADDRESS = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
     * // MAC Address, String
     * DEVICE_ADDRESS = "98:D3:71:FD:41:6D";
     *
     * @param bluetooth
     */
    private void sendDeviceInfoAndClose(BluetoothDevice bluetooth) {
        Intent resultData = new Intent();
        resultData.putExtra("BT_DEVICE_ADDRESS", bluetooth.getAddress());
        resultData.putExtra("BT_DEVICE_UUID", STRING_BT_UUID);

        setResult(Activity.RESULT_OK, resultData);
        finish();
    }

    private boolean startMyBluetooth() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if my bluetooth exists
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth no soportado", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Checks if my bluetooth is enabled
        if (!myBluetoothAdapter.isEnabled()) {
            // myBluetoothAdapter.startDiscovery();
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, REQUEST_ACTIVATE_BT);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            listBluetoothDevices();
        }

        return true;
    }

    private void listBluetoothDevices() {
        Set<BluetoothDevice> devices = myBluetoothAdapter.getBondedDevices();

        ArrayList<BluetoothDevice> list = new ArrayList();
        for (BluetoothDevice device : devices) {
            list.add(device);
            // list.add("Name: "+ device.getName() + "MAC Address: " + device.getAddress());
        }
        aAdapter = new ArrayAdapter(getApplicationContext(), R.layout.sdum_simple_list_item, list);
        listView.setAdapter(aAdapter);
    }

    /**
     * Put CANCELED result if user press back
     */
    @Override
    public void onBackPressed() {
        cancelBTListActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACTIVATE_BT) {
            if (resultCode == RESULT_OK) {
                listBluetoothDevices();
            } else {
                Toast.makeText(getApplicationContext(), "BT cancelado (code:" + requestCode + ")", Toast.LENGTH_LONG).show();
                cancelBTListActivity();
            }
        }
    }

    private void cancelBTListActivity() {
        Intent resultData = new Intent();
        setResult(Activity.RESULT_CANCELED, resultData);
        finish();
    }

}
