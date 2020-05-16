package a.apkt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import a.apkt.model.DigCert;
import a.apkt.service.StaticVars;
import a.apkt.service.UtilsService;

public class DigCertActivity extends AppCompatActivity {

    private Activity activity = this;
    private ActionBar actionBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MenuItem actionDeleteItem;

    private DigCert digCert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig_cert);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
// Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setEnabled(false);

        String digCertJson = null;

        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                digCertJson = params.getString("digCertJson");
            }
        }

        digCert = new Gson().fromJson(digCertJson, DigCert.class);
        setTextViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dig_cert, menu);
        actionDeleteItem = menu.findItem(R.id.action_delete);
//        actionDeleteItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            File file = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);
            List<DigCert> digCertList = (List<DigCert>) UtilsService.getObjectFromFile(file);
            Iterator iterator = digCertList.iterator();
            int index = 0;
            DigCert digCertAux = null;
            while (iterator.hasNext()){
                digCertAux = (DigCert) iterator.next();
                if (digCertAux.getFileName().equals(digCert.getFileName())){
                    digCertList.remove(index);
                    break;
                }
                index ++;
            }
            UtilsService.saveObjectInternalStorage(activity, digCertList, StaticVars.DIGCERTLIST);
            Intent returnIntent = new Intent();
            activity.setResult(activity.RESULT_OK, returnIntent);
            activity.finish();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTextViews() {
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        TextView tvCpf = (TextView) findViewById(R.id.tv_cpf);
        TextView tvExpirationDate = (TextView) findViewById(R.id.tv_tv_expiration);

        tvName.setText(digCert.getName());
        tvCpf.setText(digCert.getCpf());
        tvExpirationDate.setText(digCert.getExpirationDate().toLocaleString());

    }
}
