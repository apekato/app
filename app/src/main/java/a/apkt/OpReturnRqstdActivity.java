package a.apkt;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import a.apkt.model.Login;

public class OpReturnRqstdActivity extends AppCompatActivity {

    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_return_rqstd);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));

        TextView tvThanks = (TextView) findViewById(R.id.tv_thanks);

        String titleSection = null;
        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                titleSection = params.getString("titleSection");
            }
        }

        if (titleSection.equals(getResources().getText(R.string.title_section_wall))){
            tvThanks.setText(getResources().getText(R.string.tv_op_return_thanks));
        } else if (titleSection.equals(getResources().getText(R.string.title_section_notarizations))){
            tvThanks.setText(getResources().getText(R.string.tv_notarize_thanks));
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onStop();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_op_return_rqstd, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
