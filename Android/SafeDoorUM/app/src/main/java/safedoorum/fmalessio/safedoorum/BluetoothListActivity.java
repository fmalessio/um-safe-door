package safedoorum.fmalessio.safedoorum;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import java.util.UUID;

public class BluetoothListActivity extends AppCompatActivity {

    // Attributes
    // TODO: obtener este valor de el item
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    // Layout
    private ListView listView;
    private ArrayAdapter aAdapter;
    // private final String DEVICE_ADDRESS = "98:D3:71:FD:41:6D"; // MAC Address of Bluetooth Module
    private BluetoothSocket socket;
    private ArrayList<String> mDeviceList = new ArrayList<>();
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

    private void sendDeviceInfoAndClose(BluetoothDevice bluetooth) {
        Intent resultData = new Intent();
        resultData.putExtra("BT_DEVICE_ADDRESS", bluetooth.getAddress());
        // TODO: bluetooth.getUuids();
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
            startActivityForResult(enableAdapter, 0);

            // TODO: leer resultado de startActivityForResult, cargar la lista nuevamente
            // sino cerrar activity

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            listBluetoothDevices(); // TODO: agregar esto en el resultado del BT despertador
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
        aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(aAdapter);
    }

    @Override
    public void onBackPressed() {
        // TODO: back is crashing the app
    }

}
