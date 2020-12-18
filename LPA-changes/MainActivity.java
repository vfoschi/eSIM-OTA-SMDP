package com.example.LPA_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.beanit.jasn1.ber.ReverseByteArrayOutputStream;
import com.beanit.jasn1.util.HexConverter;
import com.example.LPA_app.pedefinitions.PEAKAParameter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TelephonyManager telephonyManager;
    private CarrierConfigManager CarrierConfigManager;

    private TextView _textview = null;
    private ScrollView _scrollview = null;
    private EditText _edittext = null;
    private EditText _profileID = null;
    private EditText _imeiID = null;
    private EditText _location = null;
    private Switch _active = null;
    static final String ISD_R_AID = "A0000005591010FFFFFFFF8900000100";
    static final String ISD_P_AID = "D2760001180002FF34100089C0026E01";
    public static final String ACTION_EUICC_SUPPORT_APP_OTA_STATUS_CHANGED = "com.google.euiccpixel.telephony.ACTION_EUICC_SUPPORT_APP_OTA_STATUS_CHANGED";
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static final int CLA_STORE_DATA = 128;
    private static final String COMMAND_GET_OS_VERSION = "DF31";
    private static final String EXTRA_OTA_STATUS = "ota_status";
    private static final String EXTRA_SIM_STATE = "ss";
    private static final String FAULTY_OMAPI_BINARY_VERSION = "000100000000";
    private static final String FAULTY_OMAPI_ITL_VERSION = "00010006";
    private static final String FAULTY_OMAPI_OS_VERSION = "00010000";
    private static final int INS_GET_DATA = 202;
    private static final int INS_STORE_DATA = 226;
    private static final String ITL_VERSION_0 = "00000001,00000003,00010001";
    private static final int MAX_APDU_DATA_LEN = 255;
    private static final int MAX_EXTENDED_APDU_DATA_LEN = 65535;
    private static final String MOCK_SIM_ISSUE_FIX_LOCATION = "mock-sim-fix-command";
    private static final int P1_GET_DATA_INTERM = 0;
    private static final int P1_OTA_LAST_COMMAND = 144;
    private static final int P1_STORE_DATA_INTERM = 16;
    private static final int P2_GET_DATA_INTERM = 254;
    private static final int RESPONSE_RESET_MODEM = 25345;
    private static final String SIM_STATE_ABSENT = "ABSENT";
    private static final String SIM_STATE_UNKNOWN = "UNKNOWN";
    private static final int TAG_BINARY_VERSION = 178;
    private static final int TAG_CTX_1 = 129;
    private static final int TAG_CTX_2 = 130;
    private static final int TAG_CTX_4 = 132;
    private static final int TAG_CTX_6 = 134;
    private static final int TAG_CTX_COMP_1 = 161;
    private static final int TAG_CTX_COMP_2 = 162;
    private static final int TAG_CTX_COMP_3 = 163;
    private static final int TAG_INITIALISE_SECURE_CHANNEL = 48931;
    private static final int TAG_ITL_VERSION = 179;
    private static final int TAG_OS_VERSION = 177;
    private static final int TAG_OTA_IMAGE = 48950; // 0xBF63
    private static final int TAG_SEQ_0 = 48;
    private static final String euiccFullFlashImageV0OldLocation = "/product/priv-app/EuiccSupportPixel/esim0.img";
    private static final String euiccImagV0OldLocation = "/product/priv-app/EuiccSupportPixel/esim.img";
    private static final String euiccImagV1OldLocation = "/product/priv-app/EuiccSupportPixel/esim2.img";
//    String smdp_server_address = "wing3.cs.ucla.edu";
    String smdp_server_address = "10.0.2.2:5000";
    String queue_server_address = "10.0.2.2:5001";
    String mOsVersion = null;
    String mBinaryVersion = null;
    String mItlVersion = null;
    String profileToBeDelete;
    int currentProfile = 0;

    ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(1000);

    private static final byte[] CMD_IMSI = new byte[]{(byte) 0xA0, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x6F, (byte) 0x07};
    private static final byte[] ISD_AID = new byte[]{(byte) 0x00, (byte) 0xCA, (byte) 0x00, (byte) 0x5A};
    private Context mContext;
    private int mNextCmdIndex;
    Asn1Node mOtaImage;
    private boolean mInited = false;
    PassThroughLogicalChannel channel;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);

        _textview = findViewById(R.id.text);
        _scrollview = findViewById(R.id.scroll);
        _edittext = findViewById(R.id.plain_text_input);
        _profileID = findViewById(R.id.profileID);
        _imeiID = findViewById(R.id.imeiID);
        _location = findViewById(R.id.location);
        _active = findViewById(R.id.active);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sample APDU commands
                sendCommand(channel, "00A40004023F00");
                sendCommand(channel, "00a4040410a0000000871002ffffffff8907090000");
                sendCommand(channel, "00A40004026F07");
                sendCommand(channel, "00B0000009");
            }
        });

        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendCommand(channel, String.valueOf(_edittext.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button initAuth = findViewById(R.id.init_button);
        initAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initiateAuthentication();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button downloadProfile = findViewById(R.id.download_button);
        downloadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadProfile(String.valueOf(_profileID.getText()));
                    Log.d("button", "download");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button updateProfile = findViewById(R.id.update_button);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String profileID = String.valueOf(_profileID.getText());
                    String imei = String.valueOf(_imeiID.getText());
                    String location = String.valueOf(_location.getText());
                    boolean active =_active.isChecked();
                    updateProfile(profileID, imei, location, active);
                    Log.d("button", "update");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button rebootSIM = findViewById(R.id.reboot_button);
        rebootSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "8011000000"); // REFRESH
                Log.d("button", "sim reset");
            }
        });

        Button localInstallButton = findViewById(R.id.local_install);
        localInstallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "00A70000290829430191348767650644092A26DCEBA00503CF739EC097EEC8D74B739DAB27C6CEAB9BA5C98B7D67");
                updateSimButtons(sendCommand(channel, "00A8000000"));
                Log.d("button", "Local SIM Profile Install Test");
            }
        });

        Button stkMenuButton = findViewById(R.id.stk_menu);
        stkMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "8010000014FFFFFFFF1F0000DFD7030A000000000600000000");
                Log.d("button", "Trigger sim STK");
            }
        });

        Button selectSIM1 = findViewById(R.id.profile1);
        selectSIM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "00A9010000"); // switch profile
                updateSimButtons(sendCommand(channel, "00A8000000")); // profile status
                Log.d("button", "select sim 1");
            }
        });
        selectSIM1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                profileToBeDelete = "1";
                deleteProfileDialogue(view);
                return false;
            }
        });

        Button selectSIM2 = findViewById(R.id.profile2);
        selectSIM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "00A9020000"); // switch profile
                updateSimButtons(sendCommand(channel, "00A8000000")); // profile status
                Log.d("button", "select sim 2");
            }
        });
        selectSIM2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                profileToBeDelete = "2";
                deleteProfileDialogue(view);
                return false;
            }
        });

        Button selectSIM3 = findViewById(R.id.profile3);
        selectSIM3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(channel, "00A9030000"); // switch profile
                updateSimButtons(sendCommand(channel, "00A8000000")); // profile status
                Log.d("button", "select sim 3");
            }
        });
        selectSIM3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                profileToBeDelete = "3";
                deleteProfileDialogue(view);
                return false;
            }
        });


        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CarrierConfigManager = (CarrierConfigManager) this.getSystemService(Context.CARRIER_CONFIG_SERVICE);
        logText("starts");
        mContext = getApplicationContext();

        try {
            channel = PassThroughLogicalChannel.open(UiccChannelApiFactory.getUiccChannelApiForEmbeddedEuicc(this.mContext), ISD_R_AID);
        } catch (UiccChannelException e) {
            Log.e("open channel error", e.toString());
        }

        Button carrier_latency = findViewById(R.id.latency);
        carrier_latency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarrierSelection cs = new CarrierSelection(getBaseContext(),channel,"P");
                cs.start();
            }
        });

        Button carrier_throughput = findViewById(R.id.throughput);
        carrier_throughput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarrierSelection cs = new CarrierSelection(getBaseContext(),channel,"D");
                cs.start();
            }
        });

//        try {
//            updateSimButtons(sendCommand(channel, "00A8000000"));
//        } catch (Exception e) {
//            Log.e("profile status error, please check if the sim is a jcard-esim", e.toString());
//        }
    }

    public void getauth() {
        Class<? extends TelephonyManager> telephonyManagerClass = telephonyManager.getClass();
        try {
            Method method = telephonyManagerClass.getDeclaredMethod("getIccAuthentication", int.class, int.class, String.class);
            method.invoke(this.telephonyManager, 2, 129, "abcdef");
            Log.d("LPA-app", "getIccAuthentication call");

        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(TAG, "getIccAuthentication: " + e.getLocalizedMessage(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            t.printStackTrace();
        }
    }

    public void deleteProfileDialogue(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure to delete this profile?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendCommand(channel, "00A60" + profileToBeDelete + "0000");
                updateSimButtons(sendCommand(channel, "00A8000000"));
                Toast.makeText(MainActivity.this, "Profile Deleted!", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Cancel Profile Deletion", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public void updateSimButtons(String profileStatus) {
        Button selectSIM1 = findViewById(R.id.profile1);
        Button selectSIM2 = findViewById(R.id.profile2);
        Button selectSIM3 = findViewById(R.id.profile3);

        updateSimButton(1, selectSIM1, profileStatus.charAt(3));
        updateSimButton(2, selectSIM2, profileStatus.charAt(7));
        updateSimButton(3, selectSIM3, profileStatus.charAt(11));

    }

    private void updateSimButton(int profileID, Button simbutton, char status) {
        switch (status) {
            case '1':
                simbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.PaleGreen)));
                simbutton.setEnabled(true);
                currentProfile = profileID;
                break;
            case '2':
                simbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Gold)));
                simbutton.setEnabled(true);
                break;
            case '3':
                simbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Tomato)));
                simbutton.setEnabled(false);
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String sendCommand(PassThroughLogicalChannel channel, String s) {
        if (s.equals("")) {
            return null;
        }
        int cla = Integer.parseInt(s.substring(0, 2), 16);
        int ins = Integer.parseInt(s.substring(2, 4), 16);
        int p1 = Integer.parseInt(s.substring(4, 6), 16);
        int p2 = Integer.parseInt(s.substring(6, 8), 16);
        int p3 = Integer.parseInt(s.substring(8, 10), 16);
        String data = s.substring(10);
        String resp = null;
        logText("Sent command: " + "cla: " + cla + "; ins " + ins + "; p1 " +
                p1 + "; p2 " + p2 + "; p3 " + p3 + "; data " + data + "\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resp = channel.transmitApdu(cla, ins, p1, p2, p3, data);
            logText(resp);
        }
        return resp;
    }

    private void logText(String message) {
        Log.d(TAG, "------------ message " + message + "\n");
        try {
            _scrollview.post(new Runnable() {
                public void run() {
                    _scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            _textview.append(message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Setting testMode configuration. If set as testMode, the connection will skip certification check
    private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        Log.w("OKHTTP", "Ignore Ssl Certificate");
        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.w("OKHTTP", "Exception while configuring IgnoreSslCertificate" + e, e);
        }
        return builder;
    }


    OkHttpClient okHttpClient = configureToIgnoreCertificate(new OkHttpClient.Builder()).build();

    public void doGet(String apiUrl) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(apiUrl).build();
        executeRequest(request);
    }

    // This function decode the simple profile downloaded from SM-DP+ and install it into the eSIM applet
    private void processProfileResp(String responseData) throws IOException {
        Log.w("processProfileResp", "processProfileResp received responseData: " + responseData);
//        try {
//            JSONObject jsonObject = new JSONObject(responseData);
//            // JSONObject IMSI = jsonObject.getJSONObject("IMSI");
//            String imsi = jsonObject.getString("IMSI");
//            String aka = jsonObject.getString("AKA");
//            // String aka = "303aa0058000810106a131a12f8001018101018210000102030405060708090a0b0c0d0e0f83100102030405060708090a0b0c0d0e0f008603010203";
//            InputStream is = new ByteArrayInputStream(HexConverter.fromShortHexString(aka));
//            PEAKAParameter p_decoded = new PEAKAParameter();
//            try {
//                p_decoded.decode(is);
//                System.out.println("\nDecoded structure:");
//                String key = p_decoded.getAlgoConfiguration().getAlgoParameter().getKey().toString();
//                String opc = p_decoded.getAlgoConfiguration().getAlgoParameter().getOpc().toString();
//                sendCommand(channel, "00A7000029" + imsi + opc + key);
//                updateSimButtons(sendCommand(channel, "00A8000000"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//            Log.w("processProfileResp", "processProfileResp Json decode failure" + e, e);
//        }

    }

    private void executeRequest(Request request) {
        Log.w("request", "executeRequest");
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure!!!");
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String responseString = response.body().string();
                Log.d("onResponse", responseString);
                processProfileResp(responseString);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI
                    }
                });
            }
        });
    }

    public void doPost(String mBaseUrl) {
        //Build RequestBody
        FormBody.Builder formBodeBuilder = new FormBody.Builder();
        RequestBody requestBody = formBodeBuilder.add("username", "test")
                .add("password", "test")
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "login").post(requestBody).build();
        executeRequest(request);
    }

    public void doPostString(String mBaseUrl, String postString) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), postString);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();
        executeRequest(request);
    }

    //post File
    //need to add WRITE_EXTERNAL_STORAGE
    public void doPostFile(String mBaseUrl, String filename) {
        // filename = "test.jpg";
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        if (!file.exists()) {
            Log.e("mTAG", file.getAbsolutePath() + "not exist!");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();
        executeRequest(request);
    }

    //post upload file
    public void doUpload(String mBaseUrl, String filename) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        if (!file.exists()) {
            Log.e("mTAG", file.getAbsolutePath() + "not exist!");
            return;
        }
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        RequestBody requestBody = multipartBodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("username", "test")
                .addFormDataPart("password", "test")
                .addFormDataPart("mPhoto", "upload.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl).post(requestBody).build();
        executeRequest(request);
    }

    public void doDownload(String mBaseUrl) {
        // Build Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(mBaseUrl).build();
        //Request to Call
        Call call = okHttpClient.newCall(request);
        //Run the Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream is = response.body().byteStream();
                int len = 0;
                File file = new File(Environment.getExternalStorageDirectory(), "test1.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[128];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //update UI
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initiateAuthentication() throws IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("euiccInfo1", "vyA1ggMCAgCpFgQUgTcPUSXQsdQI1MOyMubSXnlb6/uqFgQUgTcPUSXQsdQI1MOyMubSXnlb6/s=");
            jsonObject.put("smdpAddress", smdp_server_address);
            jsonObject.put("euiccChallenge", "IiHgFiXFK1goQu1dhkWsSQ==");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        //Build RequestBody
        FormBody.Builder formBodeBuilder = new FormBody.Builder();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://" + smdp_server_address + "/gsma/rsp2/es9plus/initiateAuthentication")
                .addHeader("Host", smdp_server_address)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Admin-Protocol", "gsma/rsp/v2.2.0")
                .addHeader("Content-Length", "171")
                .addHeader("Accept-Language", "en-us")
                .addHeader("Accept-Encoding", "gzip, deflate,br")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .post(body).build();
        executeRequest(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void downloadProfile(String profileID) throws IOException {
        System.out.println("inside downloadProfile!!!!!! it's" + profileID);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", "downloadProfile");
//            jsonObject.put("profileID", profileID);
//            jsonObject.put("key", "IiHgFiXFK1goQu1dhkWsSQ==");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        //Build RequestBody
//        FormBody.Builder formBodeBuilder = new FormBody.Builder();
        Request.Builder builder = new Request.Builder();
//        Request request = builder.url("https://" + smdp_server_address + "/profileDownload")
        Request request = builder.url("http://" + queue_server_address + "/queue/retrieve/" + profileID)
//                .addHeader("Host", smdp_server_address)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("X-Admin-Protocol", "gsma/rsp/v2.2.0")
//                .addHeader("Content-Length", "171")
//                .addHeader("Accept-Language", "en-us")
//                .addHeader("Accept-Encoding", "gzip, deflate,br")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
                .get().build();
        executeRequest(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateProfile(String profileID, String imei, String loc, boolean active) throws IOException {
        System.out.println("inside updateProfile!!!!!!");
        JSONObject jsonObject = new JSONObject();
//        Random r = new Random(System.currentTimeMillis());
//        int rand = r.nextInt(5);
//        String[] locs = new String[] {"90024", "90028", "90032", "90036", "90040", "90044"};
//        String[] imeis = new String[] {"35609204079266", "35609204079255", "35609204079277", "35609204079244", "35609204079233", "35609204079222"};

        try {
            jsonObject.put("imsi", profileID);
            jsonObject.put("imei", imei);
            jsonObject.put("location", loc);
            jsonObject.put("active", active ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        //Build RequestBody
//        FormBody.Builder formBodeBuilder = new FormBody.Builder();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://" + smdp_server_address + "/api/db/updateuser")
//                .addHeader("Host", smdp_server_address)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("X-Admin-Protocol", "gsma/rsp/v2.2.0")
//                .addHeader("Content-Length", "171")
//                .addHeader("Accept-Language", "en-us")
//                .addHeader("Accept-Encoding", "gzip, deflate,br")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
                .post(body).build();
//        System.out.println("request is:");
//        System.out.println(request);
        executeRequest(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void fixMockSimError() throws UiccChannelException, IOException {
        Throwable th;
        Throwable th2;
        Throwable th3 = null;
        UiccChannelApi uiccChannelApiForEmbeddedEuicc = UiccChannelApiFactory.getUiccChannelApiForEmbeddedEuicc(this.mContext);
        AssetManager assets = this.mContext.getAssets();
        PassThroughLogicalChannel open;
        //open = PassThroughLogicalChannel.open(uiccChannelApiForEmbeddedEuicc, ISD_R_AID);
        String fileName = "apdu.apdu";
        String path = Environment.getExternalStorageDirectory() + "/" + fileName;
        File file = new File(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        open = PassThroughLogicalChannel.open(uiccChannelApiForEmbeddedEuicc, ISD_P_AID);
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assets.open(MOCK_SIM_ISSUE_FIX_LOCATION)));
        Log.d("apdu-main", "Start to execute 0 164 4 0");
        open.execApdu(0, 164, 4, 0, bufferedReader.readLine());
        Log.d("apdu-main", "Start to execute 128 80 63 0");
        open.execApdu(128, 80, 63, 0, bufferedReader.readLine());
        // CLA = 0x84, INS = 0x82 EXTERNAL AUTHENTICATION
        open.execApdu(TAG_CTX_4, TAG_CTX_2, 1, 0, bufferedReader.readLine());
        open.execApdu(128, 80, 63, 0, bufferedReader.readLine());
        open.execApdu(TAG_CTX_4, TAG_CTX_2, 1, 0, bufferedReader.readLine());
        open.execApdu(128, 80, 63, 0, bufferedReader.readLine());
        open.execApdu(TAG_CTX_4, TAG_CTX_2, 1, 0, bufferedReader.readLine());

        String readLine = bufferedReader.readLine();
        while (true) {
            String str = readLine;
            if (str == null) {
                break;
            }
            readLine = bufferedReader.readLine();
            if (readLine != null) {
                open.execApdu(TAG_CTX_4, INS_STORE_DATA, 8, this.mNextCmdIndex, str);
            } else {
                open.execApdu(TAG_CTX_4, INS_STORE_DATA, 136, this.mNextCmdIndex, str);
            }
            this.mNextCmdIndex++;
        }
        bufferedReader.close();
        open.closeChannel();
    }

    public String getImageVersion(Asn1Node asn1Node) {
        if (asn1Node != null) {
            try {
                Log.d("apdu-ota", String.format("image OsVersion = %s, image BinaryVersion = %s", Asn1Converter.bytesToHex(asn1Node.getChild(48, TAG_CTX_COMP_1, 129).asBytes()), Asn1Converter.bytesToHex(asn1Node.getChild(48, TAG_CTX_COMP_1, TAG_CTX_2).asBytes())));
                StringBuilder stringBuilder = new StringBuilder();
                //stringBuilder.append(Asn1Converter.bytesToHex(r2.asBytes()));
                //stringBuilder.append(Asn1Converter.bytesToHex(asn1Node.asBytes()));
                return stringBuilder.toString();
            } catch (InvalidAsn1DataException | TagNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Asn1Node readOtaImageFile(List<String> list) throws IOException, InvalidAsn1DataException {
        FileReader fileReader;
        for (String fileReader2 : list) {
            try {
                fileReader = new FileReader(fileReader2);
                if (fileReader != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    char[] cArr = new char[2048];
                    int read;
                    do {
                        read = fileReader.read(cArr);
                        if (read != -1) {
                            stringBuilder.append(cArr, 0, read);
                            continue;
                        }
                    } while (read != -1);
                    fileReader.close();
                    return new Asn1Decoder(stringBuilder.toString()).nextNode();
                }

                break;
            } catch (FileNotFoundException unused) {
                //OtaLog.get().mo7363e("File %s doesn't exist.", fileReader2);
            }
        }
        //fileReader = new FileReader(Environment.getExternalStorageDirectory()+"/"+"esim-full-v0.img");
        //Log.d("esim",Environment.getExternalStorageDirectory()+"/"+"esim-full-v0.img" );
        throw new FileNotFoundException("All the OTA images don't exist.");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readEuiccVersionNumberBackup() {
        UiccChannelApi uiccChannelApiForEmbeddedEuicc = UiccChannelApiFactory.getUiccChannelApiForEmbeddedEuicc(this.mContext);
        UiccLogicalChannel open = null;
        try {
            open = UiccLogicalChannel.open(uiccChannelApiForEmbeddedEuicc, ISD_P_AID);
        } catch (UiccChannelException e) {
            e.printStackTrace();
        }
        for (int i = 30; i < 100; i++) {
            try {
                Asn1Node parseResponse = parseResponse(open.execApdu(128, INS_GET_DATA, 0, P2_GET_DATA_INTERM, "DF" + i));
                if (parseResponse == null) {
                    Log.d("apdu-parse-resp", i + " null response");
                }
                if (parseResponse != null) {
                    Log.d("apdu-parse-resp", parseResponse.toHex());
                }
                this.mOsVersion = parseResponse.getChild(TAG_OS_VERSION, new int[0]).toHex().substring(4);
                this.mBinaryVersion = parseResponse.getChild(TAG_BINARY_VERSION, new int[0]).toHex().substring(4);
                this.mItlVersion = parseResponse.getChild(TAG_ITL_VERSION, new int[0]).toHex().substring(4);
                Log.d("apdu_euicc", String.format("OsVersion = %s, BinaryVersion = %s, ItlVersion = %s", this.mOsVersion, this.mBinaryVersion, this.mItlVersion));
                if (open != null) {
                    open.close();
                }
            } catch (InvalidAsn1DataException | TagNotFoundException | UiccChannelException e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readEuiccVersionNumber() {
        UiccChannelApi uiccChannelApiForEmbeddedEuicc = UiccChannelApiFactory.getUiccChannelApiForEmbeddedEuicc(this.mContext);
        UiccLogicalChannel open = null;
        try {
            open = UiccLogicalChannel.open(uiccChannelApiForEmbeddedEuicc, ISD_P_AID);
        } catch (UiccChannelException e) {
            e.printStackTrace();
        }
        for (int i = 30; i < 100; i++) {
            try {
                Asn1Node parseResponse = parseResponse(open.execApdu(128, INS_GET_DATA, 0, P2_GET_DATA_INTERM, "DF" + i));
                if (parseResponse == null) {
                    Log.d("apdu-parse-resp", i + " null response");
                }
                if (parseResponse != null) {
                    Log.d("apdu-parse-resp", parseResponse.toHex());
                }
                if (open != null) {
                    open.close();
                }
            } catch (UiccChannelException e) {
                e.printStackTrace();
            }
        }
    }

    private Asn1Node parseResponse(String str) {
        Asn1Decoder asn1Decoder = new Asn1Decoder(str);
        if (asn1Decoder.hasNextNode()) {
            try {
                return asn1Decoder.nextNode();
            } catch (InvalidAsn1DataException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot parse response: ");
                stringBuilder.append(str);
            }
        }
        Log.d("apdu-asn1", "parse response reported null");
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadOtaImageInternal(boolean z) {
        Throwable th;
        PassThroughLogicalChannel open;
        try {
            open = PassThroughLogicalChannel.open(UiccChannelApiFactory.getUiccChannelApiForEmbeddedEuicc(this.mContext), ISD_P_AID);
            try {
                this.mNextCmdIndex = 0;
                Asn1Node child = this.mOtaImage.getChild(TAG_OTA_IMAGE, new int[0]);
                Asn1Node child2 = child.getChild(TAG_INITIALISE_SECURE_CHANNEL, new int[0]);
                Asn1Node child3 = child.getChild(TAG_CTX_COMP_3, new int[0]);
                List children = child3.getChildren(TAG_CTX_6);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(child.getHeadAsHex());
                stringBuilder.append(child2.toHex());
                execStoreData(open, stringBuilder.toString(), false, z);
                // write data corresponding to 0xA2 tag
                execStoreData(open, child.getChild(TAG_CTX_COMP_2, new int[0]).toHex(), false, z);
                execStoreData(open, child3.getHeadAsHex(), false, z);
                int size = children.size();
                int i = 0;
                while (i < size) {
                    execStoreData(open, ((Asn1Node) children.get(i)).toHex(), i == size + -1, z);
                    i++;
                }
                if (open != null) {
                    open.close();
                }
            } catch (UiccChannelException e) {
                if (!z) {
                    if (e.getApduStatus() == RESPONSE_RESET_MODEM) {
                        open.close();
                        //rebootSimCard(2);
                        loadOtaImageInternal(true);
                        if (open != null) {
                            open.close();
                        }
                        return;
                    }
                }
                throw e;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (UiccChannelException e3) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Error loading image: ");
            stringBuilder2.append(e3.getMessage());
            //throw new OtaException(true, 10, stringBuilder2.toString(), e3);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void execStoreData(PassThroughLogicalChannel passThroughLogicalChannel, String str, boolean z, boolean z2) throws UiccChannelException {
        String str2 = str;
        int i = 255;
        int i2 = i * 2;
        int length = str.length() / 2;
        if (length == 0) {
            i = 1;
        } else {
            i = ((length + i) - 1) / i;
        }
        int i3 = 0;
        int i4 = 1;
        while (i4 < i) {
            int i5 = i3 + i2;
            //OtaLog.get().mo7,368v("LOAD OTA IMAGE: transmit %d: %s", Integer.valueOf(this.mNextCmdIndex), str2.substring(i3, i5));
            Log.d("store data", String.format("LOAD OTA IMAGE: transmit %d: %s", Integer.valueOf(this.mNextCmdIndex), str2.substring(i3, i5)));
            passThroughLogicalChannel.execApdu(128, INS_STORE_DATA, 16, this.mNextCmdIndex, str2.substring(i3, i5));
            this.mNextCmdIndex = (this.mNextCmdIndex + 1) % 256;
            i4++;
            i3 = i5;
        }
        Log.d("store data", String.format("LOAD OTA IMAGE: transmit %d: %s", Integer.valueOf(this.mNextCmdIndex), str2.substring(i3)));
        passThroughLogicalChannel.execApdu(128, INS_STORE_DATA, z ? P1_OTA_LAST_COMMAND : 16, this.mNextCmdIndex, str2.substring(i3));
        this.mNextCmdIndex = (this.mNextCmdIndex + 1) % 256;
    }

}
