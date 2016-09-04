package bluesky.amthucdanang.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.AlertDialogManager;
import bluesky.amthucdanang.custom.SessionManager;
import bluesky.amthucdanang.entity.QuanAn;
import bluesky.amthucdanang.entity.Thongtincanhan;

public class ThongtincanhanActivity extends ActionBarActivity {
    SessionManager session;
    private String userName;
    private TextView taikhoan,sodt,password,email,address;
    private Button btnUpdate;
    private AlertDialogManager alert;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtincanhan);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taikhoan =  (TextView) findViewById(R.id.username);
        sodt     =  (TextView) findViewById(R.id.sodt);
        password =  (TextView) findViewById(R.id.password);
        email    =  (TextView) findViewById(R.id.email);
        address  =  (TextView) findViewById(R.id.address);
        btnUpdate=  (Button)    findViewById(R.id.btnUpdate);
        ArrayList<Thongtincanhan> thongtincanhanList = getThongtincanhan();
        for (int i=0;i<thongtincanhanList.size();i++){
            taikhoan.setText(thongtincanhanList.get(i).getUserName());
            sodt.setText(thongtincanhanList.get(i).getPhone());
            email.setText(thongtincanhanList.get(i).getEmail());
            address.setText(thongtincanhanList.get(i).getAddress());
            password.setText(thongtincanhanList.get(i).getPassword());
        }
        password.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateThongtincanhan()){
                    finish();
                    startActivity(getIntent());
                    alert = new AlertDialogManager();
                    alert.showAlertDialog(ThongtincanhanActivity.this,"Thành Công","Cập nhập dữ liệu thành công",false);
                }else{
                    alert = new AlertDialogManager();
                    alert.showAlertDialog(ThongtincanhanActivity.this,"Thất Bại","Cập nhập dữ liệu thất bai",false);
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thongtincanhan, menu);
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

    public ArrayList<Thongtincanhan> getThongtincanhan() {

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> info = session.getUserDetails();
        userName = info.get(SessionManager.KEY_NAME);

        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodGetThongTinCaNhan);


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
        ArrayList<Thongtincanhan> thongtincanhans = new ArrayList<>();
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodGetThongTinCaNhan, envelope);
            SoapObject soapArray = (SoapObject) envelope.getResponse();
            for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                Thongtincanhan thongTincanhan = new Thongtincanhan();
                SoapObject soapItem = (SoapObject) soapArray.getProperty(i);

                thongTincanhan.setUserName(soapItem.getPropertyAsString("userName")
                        .toString());
                thongTincanhan.setPassword(soapItem.getPropertyAsString("password")
                        .toString());
                thongTincanhan.setEmail(soapItem.getPropertyAsString("email")
                        .toString());
                thongTincanhan.setPhone(soapItem.getPropertyAsString("phone")
                        .toString());
                thongTincanhan.setAddress(soapItem.getPropertyAsString("address").toString());
                thongtincanhans.add(thongTincanhan);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }
        return thongtincanhans;
    }
    public boolean updateThongtincanhan() {
        boolean result = false;
        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodUpdateUser);

        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(taikhoan.getText().toString());
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);


        PropertyInfo passwordInfo = new PropertyInfo();
        passwordInfo.setName("password");
        passwordInfo.setValue(password.getText().toString());
        passwordInfo.setType(String.class);
        request.addProperty(passwordInfo);


        PropertyInfo emailInfo = new PropertyInfo();
        emailInfo.setName("email");
        emailInfo.setValue(email.getText().toString());
        emailInfo.setType(String.class);
        request.addProperty(emailInfo);


        PropertyInfo phoneInfo = new PropertyInfo();
        phoneInfo.setName("phone");
        phoneInfo.setValue(sodt.getText().toString());
        phoneInfo.setType(String.class);
        request.addProperty(phoneInfo);


        PropertyInfo addressInfo = new PropertyInfo();
        addressInfo.setName("address");
        addressInfo.setValue(address.getText().toString());
        addressInfo.setType(String.class);
        request.addProperty(addressInfo);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodUpdateUser, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(soapPrimitive.toString());
        } catch (Exception e) {
            System.out.println("--" + e.getMessage());
            // TODO: handle exception
        }
        return result;
    }

}
