
package bupt.mxly.healthcare.connect;

import android.content.ContextWrapper;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

import bupt.mxly.healthcare.db.DBAdapter;
import bupt.mxly.healthcare.db.DataInfo;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {

    private Socket socket = null;
    private Handler handler;

    public ChatManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    private InputStream iStream;
    private OutputStream oStream;
    private static final String TAG = "ChatHandler";
    private DBAdapter dbAdapter;
    private String userPhone = null;
    private final String FILE_NAME = "config.ini";

    @Override
    public void run() {
        try {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            handler.obtainMessage(WiFiServiceDiscoveryActivity.MY_HANDLE, this) .sendToTarget();

            while (true) {
                try {
                    // Read from the InputStream
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                        break;
                    }

                    String readMessage = new String(buffer);
                    // 数据格式：yyyy-mm-dd HH:MM:SS*TYPE*data
                    String[] healthData = readMessage.split("@");
                    try {
                        dbAdapter = new DBAdapter();
                        DataInfo dataInfo = new DataInfo();
                        dataInfo.setCollectTime(strToDate(healthData[0]));
                        dataInfo.setDataType(healthData[1]);
                        dataInfo.setHealthData(healthData[2]);
                        loadPreferencesFile();
                        dataInfo.setUserId(userPhone);
                        System.out.println(userPhone);
                        dbAdapter.insertDataInfo(dataInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Send the obtained bytes to the UI Activity
                    Log.d(TAG, "Rec:" + String.valueOf(buffer));
                    handler.obtainMessage(WiFiServiceDiscoveryActivity.MESSAGE_READ,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public static java.sql.Date strToDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }

    private void loadPreferencesFile() throws IOException {
        try {
            File file = new File(FILE_NAME);
            if(!file.exists()){
                throw new RuntimeException("要读取的文件不存在");
            }
            FileInputStream fis = new FileInputStream(FILE_NAME);
            if (fis.available() == 0) {
                return;
            }
            byte[] readBytes = new byte[fis.available()];
            while (fis.read(readBytes) != -1) {
            }
            userPhone = new String(readBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
