package safedoorum.fmalessio.safedoorum;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothListActivity extends AppCompatActivity {

    // Layout
    private ListView listView;
    private ArrayAdapter aAdapter;
    // Attributes
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
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

        if (startMyBluetooth()) {
            listBluetoothDevices();
        }
    }

    // UI configurations
    private void setUIConfigs() {
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetooth = (BluetoothDevice) parent.getAdapter().getItem(position);
                connectDevices(bluetooth);
            }
        });
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

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void listBluetoothDevices() {
        Set<BluetoothDevice> devices = myBluetoothAdapter.getBondedDevices();

        // Searching our bluetooth module
        if (devices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Primero debe emparejar el bluetooth", Toast.LENGTH_SHORT).show();
        }

        ArrayList<BluetoothDevice> list = new ArrayList();
        for (BluetoothDevice device : devices) {
            list.add(device);
            // list.add("Name: "+ device.getName() + "MAC Address: " + device.getAddress());
        }
        aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(aAdapter);
    }

    public boolean connectDevices(BluetoothDevice bluetooth) {
        try {
            // Creating a socket with the BT device
            socket = bluetooth.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Conectado correctamente a " + bluetooth.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error conectando a " + bluetooth.getName(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
