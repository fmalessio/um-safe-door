package safedoorum.fmalessio.safedoorum;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // Activities result codes
    static final int PICK_BLUETOOHT_DEVICE = 1;
    private boolean door_closed;
    // UI
    private ImageView lock_state_img;
    private Button lock_state_btn;
    private Button bluetooth_connect_btn;
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set views
        lock_state_img = findViewById(R.id.lock_state_img);
        lock_state_btn = findViewById(R.id.lock_state_btn);
        bluetooth_connect_btn = findViewById(R.id.bluetooth_connect_btn);

        // set vars
        door_closed = true;

        // events
        addListeners();
    }

    private void addListeners() {
        lock_state_img.setOnClickListener(changeLockStateListener());
        lock_state_btn.setOnClickListener(changeLockStateListener());
        // bluetooth_connect_btn.setOnClickListener(bluetoothConnectionListener());
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

    /**
     * https://developer.android.com/training/basics/intents/result?hl=es-419
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_BLUETOOHT_DEVICE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String btAddress = data.getStringExtra("BT_DEVICE_ADDRESS");
                    String btUUID = data.getStringExtra("BT_DEVICE_UUID");
                    connectBT(btAddress, btUUID);
                }
            }
        }
    }

    /**
     * BT Adapter is already validated and enabled
     */
    public boolean connectBT(String btAddress, String btUUID) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        BluetoothDevice device = null;
        for (BluetoothDevice iterator : bondedDevices) {
            if (iterator.getAddress().equals(btAddress)) {
                device = iterator;
            }
        }

        if (device == null) {
            Toast.makeText(getApplicationContext(),
                    "Se ha perdido la conexión con el dispositivo", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            // Creating a socket with the BT device
            UUID portUUID = UUID.fromString(btUUID);
            socket = device.createRfcommSocketToServiceRecord(portUUID);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bluetooth_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_view_devices: {
                Intent btList = new Intent(MainActivity.this, BluetoothListActivity.class);
                startActivityForResult(btList, PICK_BLUETOOHT_DEVICE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
