package bluesky.amthucdanang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.AlertDialogManager;
import bluesky.amthucdanang.custom.SessionManager;

public class LoginActivity extends ActionBarActivity {
    private TextView tvUserName;
    private TextView tvPassword;
    private TextView tvReset;
    private Button btnLogin;
    SessionManager sessionManager;
    AlertDialogManager alert;
    private Toolbar toolbar;

    String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUserName = (TextView) findViewById(R.id.userName);
        tvPassword = (TextView) findViewById(R.id.password);
        tvReset = (TextView) findViewById(R.id.reset);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        toolbar.getBackground().setAlpha(15);
        toolbar.setTitleTextColor(R.color.primary_dark_material_dark);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Access internet
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // create method login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert = new AlertDialogManager();
                userName = tvUserName.getText().toString();
                password = tvPassword.getText().toString();
                SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodLogin); // khai báo đối tượng để gửi lên webservice

                PropertyInfo usernameInfo = new PropertyInfo();
                usernameInfo.setName("username");
                usernameInfo.setValue(userName);
                usernameInfo.setType(String.class);
                request.addProperty(usernameInfo);

                PropertyInfo passwordInfo = new PropertyInfo();
                passwordInfo.setName("password");
                passwordInfo.setValue(password);
                passwordInfo.setType(String.class);
                request.addProperty(passwordInfo);

                sessionManager = new SessionManager(getApplicationContext());

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11); //
                // Define version Envelope
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
                Gson gson = new Gson();
                try {
                    httpTransportSE.debug = true;
                    httpTransportSE.call(Common.NAMESPACE + Common.MethodLogin, envelope);
                    SoapObject soapArray = (SoapObject) envelope.getResponse();
                    String username = "";
                    for (int i = 0; i < soapArray.getPropertyCount(); i++) {
                        SoapObject soapItem = (SoapObject) soapArray.getProperty(i);
                        username = soapItem.getPropertyAsString("userName")
                                .toString();
                        sessionManager.createLoginSession(username);
                    }
                    if (username.equals("") || username.equals(null)) {
                        alert.showAlertDialog(LoginActivity.this, "Đăng Nhập Thất Bại", "Sai Mã Đăng Nhập/Mật Khẩu", false);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    System.out.println(envelope.getResponse().toString());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    // TODO: handle exception
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void ResetPass(View v) {
        Intent intent = new Intent(this, ResetActivity.class);
        startActivity(intent);
    }

    public void Register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


}
