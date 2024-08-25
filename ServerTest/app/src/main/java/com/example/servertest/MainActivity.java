package com.example.servertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText portEditText;
    private Button startButton;
    private Button stopButton;
    private TextView ipHostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialize os componentes
        portEditText = findViewById(R.id.Port_number);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        ipHostTextView = findViewById(R.id.IP_host);

        // Obtenha e defina o endereço IP automaticamente
        String ipHost = getIPAddress();
        ipHostTextView.setText(ipHost);

        // Configurações para o botão "Start"
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String port = portEditText.getText().toString();
                if (!port.isEmpty()) {
                    try {
                        int portNumber = Integer.parseInt(port);
                        Intent intent = new Intent(MainActivity.this, ServerService.class);
                        intent.putExtra("PORT", portNumber);
                        startService(intent);
                        Toast.makeText(MainActivity.this, "Server started on IP " + ipHost + " and port " + port, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid port number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a port number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurações para o botão "Stop"
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ServerService.class));
                Toast.makeText(MainActivity.this, "Server stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para obter o endereço IP do dispositivo
    private String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "IP not found";
    }
}
