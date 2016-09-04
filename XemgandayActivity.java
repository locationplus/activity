package bluesky.amthucdanang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.CustomQuanAn;
import bluesky.amthucdanang.custom.SessionManager;
import bluesky.amthucdanang.custom.SqlLiteDatabase;
import bluesky.amthucdanang.entity.QuanAn;

public class XemgandayActivity extends ActionBarActivity {
    private ListView listQuaAn;
    SessionManager session;
    private Toolbar toolbar;
    String userName;
    private ArrayList<QuanAn> listquanan;
    public static QuanAn quanAn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xemganday);

        //Access internet
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        userName = info.get(SessionManager.KEY_NAME);
        SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getApplicationContext());


        if (userName != null) {
            ArrayList<QuanAn> listQuanAn = getQuanAn();
            listQuaAn = (ListView) findViewById(R.id.listQuanAn);
            CustomQuanAn custom = new CustomQuanAn(this, listQuanAn);
            listQuaAn.setAdapter(custom);
        } else {

            ArrayList<QuanAn> listQuanAn = sqlLiteDatabase.getCurrentAddress();
            listQuaAn = (ListView) findViewById(R.id.listQuanAn);
            CustomQuanAn custom = new CustomQuanAn(this, listQuanAn);
            listQuaAn.setAdapter(custom);

        }

        quanAn = new QuanAn();
        listQuaAn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userName != null) {
                    listquanan = getQuanAn();
                } else {
                    SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getApplicationContext());
                    listquanan = sqlLiteDatabase.getCurrentAddress();
                }

                quanAn.setTenQuanAn(listquanan.get(position).getTenQuanAn());
                quanAn.setAddress(listquanan.get(position).getAddress());
                quanAn.setEmail(listquanan.get(position).getEmail());
                quanAn.setPhone(listquanan.get(position).getPhone());
                quanAn.setAnh(listquanan.get(position).getAnh());
                quanAn.setQuanAnId(listquanan.get(position).getQuanAnId());
                quanAn.setGiomocua(listquanan.get(position).getGiomocua());
                Intent intent = new Intent(getApplication(), ChitietQuangandayActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xemganday, menu);
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

    public ArrayList<QuanAn> getQuanAn() {


        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodQuanGanDay);


        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(userName);
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        Gson gson = new Gson();
        ArrayList<QuanAn> listQuanAn = new ArrayList<>();
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodQuanGanDay, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                QuanAn quanAn = new QuanAn();
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);

                quanAn.setTenQuanAn(soapItem.getPropertyAsString("quanAnId")
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
    }

}
