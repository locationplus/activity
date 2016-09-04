package bluesky.amthucdanang.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.CustomNavigation;
import bluesky.amthucdanang.custom.CustomQuanAn;
import bluesky.amthucdanang.custom.SessionManager;
import bluesky.amthucdanang.entity.QuanAn;
import bluesky.amthucdanang.fragment.NavigationDrawerFragment;


public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ListView listView, listChucNang;
    ExpandableListAdapter listAdapter;
    private ExpandableListView expandableListView;
    private CustomNavigation customNavigation;
    private ListView listQuaAn;
    private ArrayList<QuanAn> listquanan;
    public static QuanAn quanAn;
    private Spinner spChude, spKhuvuc;
    public String khuVuc, chuDe;
    SessionManager session;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Hiện chưa kết nối mạng. Bạn có muốn bật kết nối không ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {


            toolbar = (Toolbar) findViewById(R.id.app_bar); // set toolbar
            setSupportActionBar(toolbar);


            session = new SessionManager(getApplicationContext());
            HashMap<String, String> info = session.getUserDetails();
            userName = info.get(SessionManager.KEY_NAME);

            //Access internet
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            getSupportActionBar().setDisplayShowHomeEnabled(true); // Hiển thị nút home

            NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer); // Show navigation
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

            listChucNang = (ListView) findViewById(R.id.listChucNang);
            int[] prgmImages = {R.drawable.email, R.drawable.place, R.drawable.phone, R.drawable.time, R.drawable.phone};
            if (userName != null) {
                String[] prgmNameList = {"Quán Yêu Thích", "Xem Gần Đây", "Tìm Quanh Đây", "Thông Tin Cá Nhân", "Thoát"};
                customNavigation = new CustomNavigation(this, prgmNameList, prgmImages);
            } else {
                String[] prgmNameList = {"Tìm Quanh Đây", "Xem Gần Đây", "Đăng Ký", "Đăng Nhập"};
                customNavigation = new CustomNavigation(this, prgmNameList, prgmImages);
            }

            listChucNang.setAdapter(customNavigation);

            addItemsOnSpinnerKhuVuc(); // thêm item cho spinner khu vực
            addItemsOnChuDe();  // Them item cho spinnẻ chu de


            khuVuc = valueofDiaChi(spKhuvuc.getSelectedItem().toString());
            chuDe = spChude.getSelectedItem().toString();
            listView = listQuanAn(khuVuc, chuDe);
            quanAn = new QuanAn();

            // set sự kiện click cho item của listview
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    khuVuc = valueofDiaChi(spKhuvuc.getSelectedItem().toString());
                    chuDe = spChude.getSelectedItem().toString();
                    if (khuVuc.equals("All")) {
                        khuVuc = "";
                    }
                    if (chuDe.equals("All")) {
                        chuDe = "";
                    }
                    listquanan = getQuanAn(khuVuc, chuDe);

                    quanAn.setTenQuanAn(listquanan.get(position).getTenQuanAn());
                    quanAn.setAddress(listquanan.get(position).getAddress());
                    quanAn.setEmail(listquanan.get(position).getEmail());
                    quanAn.setPhone(listquanan.get(position).getPhone());
                    quanAn.setAnh(listquanan.get(position).getAnh());
                    quanAn.setQuanAnId(listquanan.get(position).getQuanAnId());
                    quanAn.setGiomocua(listquanan.get(position).getGiomocua());
                    Intent intent = new Intent(getApplication(), ChitietquanActivity.class);
                    startActivity(intent);
                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn thoát khỏi chương trình ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addItemsOnSpinnerKhuVuc() {
        spKhuvuc = (Spinner) findViewById(R.id.spKhuvuc);

        List<String> list = getKhuVuc();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKhuvuc.setAdapter(dataAdapter);


        spKhuvuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String khuvuc1 = valueofDiaChi(String.valueOf(parent.getItemAtPosition(position)));
                String chude1 = spChude.getSelectedItem().toString();

                if (khuvuc1 == "All") {
                    khuvuc1 = "";
                }
                if (chude1 == "All") {
                    chude1 = "";
                }
                listQuanAn(khuvuc1, chude1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    } // add item cho spinner khu vực

    public void addItemsOnChuDe() { // add item cho spinner chủ dề
        khuVuc = valueofDiaChi(spKhuvuc.getSelectedItem().toString());
        spChude = (Spinner) findViewById(R.id.spChude);
        final List<String> list = getChuDe();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChude.setAdapter(dataAdapter);
        spChude.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String khuvuc1 = valueofDiaChi(spKhuvuc.getSelectedItem().toString());
                String chude1 = String.valueOf(parent.getItemAtPosition(position));
                if (khuvuc1 == "All") {
                    khuvuc1 = "";
                }
                if (chude1 == "All") {
                    chude1 = "";
                }
                listQuanAn(khuvuc1, chude1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public ListView listQuanAn(String khuvuc, String chude) { // show dữ liệu lên listview
        ArrayList<QuanAn> listQuanAn = getQuanAn(khuvuc, chude);
        listQuaAn = (ListView) findViewById(R.id.listView1);
        CustomQuanAn custom = new CustomQuanAn(this, listQuanAn);
        listQuaAn.setAdapter(custom);
        return listQuaAn;

    }

    public ArrayList<QuanAn> getQuanAn(String khuvuc, String chude) {
        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodGetQuanAn);

        PropertyInfo khuvucInfo = new PropertyInfo();
        khuvucInfo.setName("khuvuc");
        khuvucInfo.setValue(khuvuc);
        khuvucInfo.setType(String.class);
        request.addProperty(khuvucInfo);

        PropertyInfo chudeInfo = new PropertyInfo();
        chudeInfo.setName("chude");
        chudeInfo.setValue(chude);
        chudeInfo.setType(String.class);
        request.addProperty(chudeInfo);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        String userName = info.get(SessionManager.KEY_NAME);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);

        ArrayList<QuanAn> listQuanAn = new ArrayList<>();

        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodGetQuanAn, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                QuanAn quanAn = new QuanAn();
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                quanAn.setTenQuanAn(soapItem.getPropertyAsString("tenQuanAn")
                        .toString());
                quanAn.setAddress(soapItem.getPropertyAsString("address")
                        .toString());
                quanAn.setEmail(soapItem.getPropertyAsString("email")
                        .toString());
                quanAn.setPhone(soapItem.getPropertyAsString("phone")
                        .toString());
                quanAn.setGiomocua(soapItem.getPropertyAsString("giomocua").toString());
                quanAn.setQuanAnId(soapItem.getPropertyAsString("quanAnId").toString());
                quanAn.setAnh(soapItem.getPropertyAsString("anh").toString());
                listQuanAn.add(quanAn);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        return listQuanAn;
    }// lấy dữ liệu quán ăn dựa trên chủ đề và khu vực

    public List<String> getKhuVuc() {
        List<String> listKhuVuc = new ArrayList<>();
        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodKhuvuc);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        Gson gson = new Gson();
        listKhuVuc.add("All");
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodKhuvuc, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                listKhuVuc.add(soapItem.getPropertyAsString("tenKhuVuc")
                        .toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        return listKhuVuc;
    }

    public List<String> getChuDe() {
        List<String> listChude = new ArrayList<>();

        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodChuDe);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        listChude.add("All");
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodChuDe, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                listChude.add(soapItem.getPropertyAsString("chude")
                        .toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        return listChude;
    }

    public String valueofDiaChi(String value) {
        String result = "";
        if (value.equals("Cẩm Lệ")) {
            result = "CL";
        }
        if (value.equals("Ngũ Hành Sơn")) {
            result = "NHS";
        }
        if (value.equals("Sơn Trà")) {
            result = "ST";
        }
        if (value.equals("Hòa Vang")) {
            result = "HV";
        }
        if (value.equals("Liên Chiểu")) {
            result = "LC";
        }
        if (value.equals("Hải Châu")) {
            result = "HC";
        }


        return result;
    }

    public boolean getConnected() { // check connect internet
        ConnectivityManager connect = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connect
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetworkInfo = connect
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetworkInfo != null && mobileNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }


}
