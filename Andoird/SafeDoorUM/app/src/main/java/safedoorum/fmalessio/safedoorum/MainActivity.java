package safedoorum.fmalessio.safedoorum;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private boolean door_closed;
    private ImageView lock_state_img;
    private Button lock_state_btn;
    private Button bluetooth_connect_btn;
    private Button view_bluetooth_list_btn;

    private final String DEVICE_ADDRESS = "98:D3:71:FD:41:6D"; // MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice device;
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set views
        lock_state_img = findViewById(R.id.lock_state_img);
        lock_state_btn = findViewById(R.id.lock_state_btn);
        bluetooth_connect_btn = findViewById(R.id.bluetooth_connect_btn);
        view_bluetooth_list_btn = findViewById(R.id.view_bluetooth_list_btn);

        // set vars
        door_closed = true;

        // events
        addListeners();
    }

    private void addListeners() {
        lock_state_img.setOnClickListener(changeLockStateListener());
        lock_state_btn.setOnClickListener(changeLockStateListener());
        bluetooth_connect_btn.setOnClickListener(bluetoothConnectionListener());
        view_bluetooth_list_btn.setOnClickListener(viewBluetoothDevicesListener());
    }

    private View.OnClickListener changeLockStateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socket == null) {
                    Toast.makeText(getApplicationContext(), "No se ha conectado ningún dispositivo", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    if (door_closed) {
                        socket.getOutputStream().write("a".getBytes());
                        lock_state_img.setImageResource(R.drawable.safe_door_um_unlocked);
                        lock_state_btn.setText(R.string.close_door);
                        door_closed = false;
                    } else {
                        socket.getOutputStream().write("b".getBytes());
                        lock_state_img.setImageResource(R.drawable.safe_door_um_locked);
                        lock_state_btn.setText(R.string.open_door);
                        door_closed = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener bluetoothConnectionListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initializeBT()) {
                    connectBT();
                    // beginListenForData();
                }
            }
        };
    }

    private View.OnClickListener viewBluetoothDevicesListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btList = new Intent(MainActivity.this, BluetoothListActivity.class);
                startActivity(btList);
                // TODO: return activity result
                // https://developer.android.com/training/basics/intents/result?hl=es-419
            }
        };
    }

    // Initializes bluetooth module
    public boolean initializeBT() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if the device supports bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Checks if bluetooth is enabled.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        // Searching our bluetooth module
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Primero debe emparejar el bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    return true;
                }
            }
        }

        Toast.makeText(getApplicationContext(), "No se ha encontrado el dispositvo especificado", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean connectBT() {
        try {
            // Creating a socket with the BT device
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Conectado correctamente", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO: disconnect the bluetooth
        super.onDestroy();
    }

}
