package com.example.servertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ServerService extends Service {

    private WebServer webServer;
    private static final String TAG = "ServerService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra("PORT", 8080);
        webServer = new WebServer(port);

        try {
            webServer.start();
            Log.d(TAG, "Server started on port: " + port);
        } catch (IOException e) {
            Log.e(TAG, "Error starting server: ", e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webServer != null) {
            webServer.stop();
            Log.d(TAG, "Server stopped");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class WebServer extends NanoHTTPD {

        public WebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String htmlResponse = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Área Touchscreen com Ponteiro</title>\n" +
                    "    <style>\n" +
                    "        .touch-area {\n" +
                    "            width: 100%;\n" +
                    "            height: 100vh;\n" +
                    "            background-color: lightgray;\n" +
                    "            position: relative;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "        .pointer {\n" +
                    "            width: 20px;\n" +
                    "            height: 20px;\n" +
                    "            background-color: red;\n" +
                    "            border-radius: 50%;\n" +
                    "            position: absolute;\n" +
                    "            pointer-events: none;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"touch-area\" id=\"touchArea\">\n" +
                    "        <!-- Ponteiros serão adicionados aqui -->\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <script>\n" +
                    "        const touchArea = document.getElementById('touchArea');\n" +
                    "\n" +
                    "        touchArea.addEventListener('touchstart', handleTouch);\n" +
                    "        touchArea.addEventListener('touchmove', handleTouch);\n" +
                    "        touchArea.addEventListener('touchend', handleTouchEnd);\n" +
                    "\n" +
                    "        function handleTouch(event) {\n" +
                    "            event.preventDefault();\n" +
                    "            const touches = event.touches;\n" +
                    "            touchArea.innerHTML = ''; // Limpa os ponteiros antigos\n" +
                    "\n" +
                    "            for (let i = 0; i < touches.length; i++) {\n" +
                    "                const touch = touches[i];\n" +
                    "                const pointer = document.createElement('div');\n" +
                    "                pointer.classList.add('pointer');\n" +
                    "                pointer.style.left = `${touch.clientX - 10}px`;\n" +
                    "                pointer.style.top = `${touch.clientY - 10}px`;\n" +
                    "                touchArea.appendChild(pointer);\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        function handleTouchEnd(event) {\n" +
                    "            touchArea.innerHTML = ''; // Remove os ponteiros quando o toque termina\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";

            return newFixedLengthResponse(htmlResponse);
        }
    }
}
