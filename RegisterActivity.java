package bluesky.amthucdanang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.AlertDialogManager;
import bluesky.amthucdanang.custom.SessionManager;

public class RegisterActivity extends ActionBarActivity {
    private TextView txtUsername, txtPassword, txtEmail, txtPhone, txtAddress;
    private Button btnRegister;
    AlertDialogManager alert;
    SessionManager sessionManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUsername = (TextView) findViewById(R.id.userName);
        txtPassword = (TextView) findViewById(R.id.password);
        txtEmail = (TextView) findViewById(R.id.email);
        txtPhone = (TextView) findViewById(R.id.phone);
        txtAddress = (TextView) findViewById(R.id.tvAddress);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        sessionManager = new SessionManager(getApplicationContext());


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Register()){
                    Intent intent  = new Intent(getApplicationContext(),MainActivity.class);
                    sessionManager.createLoginSession(txtUsername.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    alert = new AlertDialogManager();
                    alert.showAlertDialog(RegisterActivity.this, "Đăng Ký Thất Bại", "Hãy kiểm tra lại dữ liệu", false);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public boolean Register() {
        boolean result = false;
    SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodRegister);

    PropertyInfo usernameInfo = new PropertyInfo();
    usernameInfo.setName("username");
    usernameInfo.setValue(txtUsername.getText().toString());
    usernameInfo.setType(String.class);
    request.addProperty(usernameInfo);


    PropertyInfo passwordInfo = new PropertyInfo();
    passwordInfo.setName("password");
    passwordInfo.setValue(txtPassword.getText().toString());
    passwordInfo.setType(String.class);
    request.addProperty(passwordInfo);


    PropertyInfo emailInfo = new PropertyInfo();
    emailInfo.setName("email");
    emailInfo.setValue(txtEmail.getText().toString());
    emailInfo.setType(String.class);
    request.addProperty(emailInfo);


    PropertyInfo phoneInfo = new PropertyInfo();
    phoneInfo.setName("phone");
    phoneInfo.setValue(txtPhone.getText().toString());
    phoneInfo.setType(String.class);
    request.addProperty(phoneInfo);


    PropertyInfo addressInfo = new PropertyInfo();
    addressInfo.setName("address");
    addressInfo.setValue(txtAddress.getText().toString());
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
        httpTransportSE.call(Common.NAMESPACE + Common.MethodRegister, envelope);
        SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
        result = Boolean.parseBoolean(soapPrimitive.toString());
    } catch (Exception e) {
        System.out.println("--" + e.getMessage());
        // TODO: handle exception
    }
    return result;
}
}
