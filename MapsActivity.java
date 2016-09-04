package bluesky.amthucdanang.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.GPSService;
import bluesky.amthucdanang.entity.Address;
import bluesky.amthucdanang.entity.QuanAn;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap;
    private Toolbar toolbar;// Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            askUserToOpenGPS();
        } else {
            setUpMapIfNeeded();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            askUserToOpenGPS();
        } else {
            setUpMapIfNeeded();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        List<Address> list = new ArrayList<>();
        GPSService mGPSService = new GPSService(getApplicationContext());
        mGPSService.getLocation();
        double latitude = mGPSService.getLatitude();
        double longitude = mGPSService.getLongitude();
        List<android.location.Address> addresses = getAddress(latitude, longitude);

        List<Address> addresses1 = getLocationFromAddress();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Address").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        for (int i = 0; i < addresses1.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(addresses1.get(i).getLats(), addresses1.get(i).getLongs())).title(addresses1.get(i).getTenQuan()));
        }
    }

    public String valueofDiaChi(String value) {
        String result = "";
        if (("Cẩm Lệ").equals(value)) {
            result = "CL";
        }
        if (("Ngũ Hành Sơn").equals(value)) {
            result = "NHS";
        }
        if (("Sơn Trà").equals(value)) {
            result = "ST";
        }
        if (("Hòa Vang").equals(value)) {
            result = "HV";
        }
        if (("Liên Chiểu").equals(value)) {
            result = "LC";
        }
        if (("Hải Châu").equals(value)) {
            result = "HC";
        }
        if (("Thanh Khê").equals(value)) {
            result = "";
        }

        return result;
    }

    public List<android.location.Address> getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<android.location.Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public ArrayList<QuanAn> getQuanAn() {
        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodGetQuanAn);
        GPSService mGPSService = new GPSService(getApplicationContext());
        mGPSService.getLocation();
        double latitude = mGPSService.getLatitude();
        double longitude = mGPSService.getLongitude();
        List<android.location.Address> addresses = getAddress(latitude, longitude);
        PropertyInfo khuvucInfo = new PropertyInfo();
        khuvucInfo.setName("khuvuc");
        khuvucInfo.setValue(valueofDiaChi(addresses.get(0).getSubAdminArea()));


        khuvucInfo.setType(String.class);
        request.addProperty(khuvucInfo);

        PropertyInfo chudeInfo = new PropertyInfo();
        chudeInfo.setName("chude");
        chudeInfo.setValue("");
        chudeInfo.setType(String.class);
        request.addProperty(chudeInfo);


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
                listQuanAn.add(quanAn);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        return listQuanAn;
    }// lấy dữ liệu quán ăn dựa trên khu vuc

    public List<Address> getLocationFromAddress() { // get tung dộ hoành độ của địa chỉ

        Geocoder coder = new Geocoder(this);
        List<android.location.Address> address;
        List<Address> listAddress = new ArrayList<>();
        List<QuanAn> listQuanAn = getQuanAn();
        for (int i = 0; i < listQuanAn.size(); i++) {
            Address address1 = new Address();
            try {
                address = coder.getFromLocationName(listQuanAn.get(i).getAddress(), 5);
                if (address == null) {
                    return null;
                }
                android.location.Address location = address.get(0);
                address1.setLongs(location.getLongitude());
                address1.setLats(location.getLatitude());
                address1.setTenQuan(listQuanAn.get(i).getTenQuanAn());
                listAddress.add(address1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listAddress;

    }

    public void askUserToOpenGPS() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting Dialog Title
        mAlertDialog.setTitle("Location not available, Open GPS?")
                .setMessage("Activate GPS to use use location services?")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

}

