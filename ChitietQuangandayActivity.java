package bluesky.amthucdanang.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.CustomAdapter;
import bluesky.amthucdanang.custom.CustomQuanAn;
import bluesky.amthucdanang.custom.SessionManager;
import bluesky.amthucdanang.entity.QuanAn;

/**
 * Created by manhnc on 18/05/2015.
 */
public class ChitietQuangandayActivity extends ActionBarActivity{
    private Button btnLike;
    private Button btnRemove;
    private Toolbar toolbar;
    private ImageView avatar;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ListView lv;
    String userName;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitietquan);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        QuanAn quanAn = XemgandayActivity.quanAn;


        setUpMapIfNeeded();

        int[] prgmImages = {R.drawable.email, R.drawable.place, R.drawable.phone, R.drawable.time};
        String[] prgmNameList = {quanAn.getEmail(), quanAn.getAddress(), quanAn.getPhone(), quanAn.getGiomocua()};


        lv = (ListView) findViewById(R.id.listView);
        lv.removeAllViewsInLayout();
        lv.setAdapter(new CustomAdapter(this, prgmNameList, prgmImages));
        addDiaChi(Common.MethodAddDiaChiGanDay);

        btnRemove = (Button) findViewById(R.id.btnLove);

        if (userName == null) {
            btnRemove.setVisibility(View.GONE);
        }


        boolean result = checkDiaChi(); // check địa chỉ
        if (result) {
            btnRemove.setText("Bỏ Theo Dõi");
        } else {
            btnRemove.setText("Theo Dõi");
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = checkDiaChi();
                if (result) {
                    addDiaChi(Common.MethodRemoveQuanYeuThich);
                    btnRemove.setText("Theo Dõi");

                } else {
                    btnRemove.setText("Bỏ Theo Dõi");
                    addDiaChi(Common.MethodAddQuanYeuThich);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chitietquan, menu);
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
            TextView tv = (TextView) findViewById(R.id.action_settings);
            boolean result = checkDiaChi();
            if (result) {
                tv.setText("Theo Dõi");
                addDiaChi(Common.MethodRemoveQuanYeuThich);
                btnRemove.setText("Theo Dõi");

            } else {
                btnRemove.setText("Bỏ Theo Dõi");
                tv.setText(" Bỏ Theo Dõi");
                addDiaChi(Common.MethodAddQuanYeuThich);
            }
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean addDiaChi(String Method) {
        boolean result = false;
        SoapObject request = new SoapObject(Common.NAMESPACE, Method);
        QuanAn quanAn = XemgandayActivity.quanAn;

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        userName = info.get(SessionManager.KEY_NAME);

        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(userName); /// ten nguoi dung
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);

        PropertyInfo passwordInfo = new PropertyInfo();
        passwordInfo.setName("quanAnId");
        passwordInfo.setValue(quanAn.getQuanAnId());

        passwordInfo.setType(String.class);
        request.addProperty(passwordInfo);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        Gson gson = new Gson();
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Method, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(soapPrimitive.toString());
        } catch (Exception e) {
            System.out.println("--" + e.getMessage());
            // TODO: handle exception
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public boolean checkDiaChi() {
        boolean result = false;
        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodCheckDiaChi);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        userName = info.get(SessionManager.KEY_NAME);

        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(userName);
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);

        QuanAn quanAn = XemgandayActivity.quanAn;

        PropertyInfo passwordInfo = new PropertyInfo();
        passwordInfo.setName("quanAnId");
        passwordInfo.setValue(quanAn.getQuanAnId());
        passwordInfo.setType(String.class);
        request.addProperty(passwordInfo);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodCheckDiaChi, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(soapPrimitive.toString());
        } catch (Exception e) {
            System.out.println("--" + e.getMessage());
            // TODO: handle exception
        }
        return result;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.getUiSettings().setRotateGesturesEnabled(true);
            }
        }
    }

    private void setUpMap() { // set map

        QuanAn quanAn = XemgandayActivity.quanAn;
        HashMap<String, Double> info = getLocationFromAddress(quanAn.getAddress());

        Double tungdo = info.get("TungDo");
        Double hoanhdo = info.get("HoanhDo");
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(tungdo, hoanhdo)).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(new LatLng(tungdo, hoanhdo)).title(quanAn.getTenQuanAn()));
        mMap.getUiSettings().setRotateGesturesEnabled(true);

    }

    public HashMap<String, Double> getLocationFromAddress(String strAddress) { // get tung dộ hoành độ của địa chỉ

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        HashMap<String, Double> vitri = new HashMap<>();

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            vitri.put("TungDo", location.getLatitude() );
            vitri.put("HoanhDo", location.getLongitude() );


        } catch (IOException e) {
            e.printStackTrace();
        }
        return vitri;

    }
}
