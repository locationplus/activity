package bluesky.amthucdanang.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Random;

import bluesky.amthucdanang.R;
import bluesky.amthucdanang.common.Common;
import bluesky.amthucdanang.custom.AlertDialogManager;
import bluesky.amthucdanang.custom.GmailSender;
import bluesky.amthucdanang.custom.SessionManager;

public class ResetActivity extends ActionBarActivity {
    private TextView tvUserName, tvEmail;
    private Button btnReset;
    SessionManager sessionManager;
    AlertDialogManager alert;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar); // extend ActionBarActivity
        toolbar.getBackground().setAlpha(15);
        toolbar.setTitleTextColor(R.color.primary_dark_material_dark);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendEmail();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset, menu);
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

    public void sendEmail() { //SEND EMAIL TO RESET PASSWORD
        int newPass = randomPassword();
        tvEmail = (TextView) findViewById(R.id.email);
        if (checkReset()) {
            try {
                GmailSender sender = new GmailSender("amthucdanang2015@gmail.com", "digon319");
                sender.sendMail("Reset Mật Khẩu",
                        "Chào bạn. Bạn vừa yêu cầu thay đổi mật khẩu. Mật khẩu hiện tại của bạn là: " + newPass,
                        "amthucdanang2015@gmail.com",
                        tvEmail.getText().toString());//send email
                resetpw(String.valueOf(newPass));
                alert.showAlertDialog(ResetActivity.this,"Reset Thành Công","Vui lòng kiểm tra email và đăng nhập lại",false);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
        } else {
            alert.showAlertDialog(ResetActivity.this,"Reset Thất Bại","Vui lòng kiểm tra email hoặc tên đăng nhập",false);
        }

    }

    public int randomPassword() {
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((999999 - 100000) + 1) + 100000;

        return randomNum;
    }

    public boolean checkReset() {

        boolean result = false;
        alert = new AlertDialogManager();
        tvUserName = (TextView) findViewById(R.id.userName);
        tvEmail = (TextView) findViewById(R.id.email);

        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodCheckEmail);

        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(tvUserName.getText().toString());
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);

        PropertyInfo emailInfo = new PropertyInfo();
        emailInfo.setName("email");
        emailInfo.setValue(tvEmail.getText().toString());
        emailInfo.setType(String.class);
        request.addProperty(emailInfo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        Gson gson = new Gson();
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodCheckEmail, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(soapPrimitive.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }

        return result;
    }

    public void resetpw(String newPass) { //reset password

        tvUserName = (TextView) findViewById(R.id.userName);

        SoapObject request = new SoapObject(Common.NAMESPACE, Common.MethodResetPw);

        PropertyInfo usernameInfo = new PropertyInfo();
        usernameInfo.setName("username");
        usernameInfo.setValue(tvUserName.getText().toString());
        usernameInfo.setType(String.class);
        request.addProperty(usernameInfo);

        PropertyInfo emailInfo = new PropertyInfo();
        emailInfo.setName("password");
        emailInfo.setValue(newPass);
        emailInfo.setType(String.class);
        request.addProperty(emailInfo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Define version Envelope
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Common.URL);
        try {
            httpTransportSE.debug = true;
            httpTransportSE.call(Common.NAMESPACE + Common.MethodResetPw, envelope);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO: handle exception
        }

    }
}
