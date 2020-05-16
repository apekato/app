package a.apkt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import a.apkt.asynctask.VerifyNotarizationAsyncTask;
import a.apkt.model.Tx;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.ProjService;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;

public class VerifyNotarizationActivity extends AppCompatActivity {

    private Activity activity = this;
    private ActionBar actionBar;
    private String verifyNotarizationTxJson;
    private String dataHexHashString;
    private Tx verifyNotarizationTx;
    private boolean isDataHexEqual;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_notarization);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        // END_INCLUDE (change_colors)

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    initiateRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                verifyNotarizationTxJson = params.getString("verifyNotarizationTxJson");
                if (verifyNotarizationTxJson != null){
                    verifyNotarizationTx = new Gson().fromJson(verifyNotarizationTxJson, Tx.class);
                    dataHexHashString = params.getString("dataHexHashString");
                }
                isDataHexEqual = params.getBoolean("isDataHexEqual");
            }
        }
        setTextViews();
    }

    public void setTextViews() {

        TextView tv_verify_notarization = (TextView) findViewById(R.id.tv_verify_notarization);
        TextView tvTxId = (TextView) findViewById(R.id.tv_tx_id);
        TextView tvTxIdTitle = (TextView) findViewById(R.id.tv_tx_id_title);
        TextView tvText = (TextView) findViewById(R.id.tv_text);
        TextView tvTextTitle = (TextView) findViewById(R.id.tv_text_title);
        TextView tvTimestamp = (TextView) findViewById(R.id.tv_timestamp);
        TextView tvTimestampTitle = (TextView) findViewById(R.id.tv_timestamp_title);
        tvTxId.setText(verifyNotarizationTx.getHash().toString());
        tvText.setText(dataHexHashString);
        if (isDataHexEqual){
            if (verifyNotarizationTx.getConfirmed() != null){
                tv_verify_notarization.setText(getResources().getText(R.string.tv_verify_notarization_confirmed));
                String dateInString = StringUtils.parseStringToDate(verifyNotarizationTx.getConfirmed());
                tvTimestamp.setText(dateInString);
            } else {
                tv_verify_notarization.setText(getResources().getText(R.string.tv_verify_notarization_waiting_confirmation));
                tvTimestamp.setText(getResources().getText(R.string.tv_timestamp));
            }
        } else{
            tv_verify_notarization.setText(getResources().getText(R.string.tv_verify_notarization_doc_not_found));
            tvTxId.setVisibility(View.GONE);
            tvTxIdTitle.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            tvTextTitle.setVisibility(View.GONE);
            tvTimestamp.setVisibility(View.GONE);
            tvTimestampTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isDataHexEqual){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_op_return_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_proof) {
            // Proof website:
            // examples:
            //https://blockchain.info/tx/2db207f008822f90c6e67a21179a2da44b043ebef3d3854f26efb9ffde6aeef8
            //https://www.blocktrail.com/BTC/tx/2db207f008822f90c6e67a21179a2da44b043ebef3d3854f26efb9ffde6aeef8
            //http://chainflyer.bitflyer.jp/Transaction/2db207f008822f90c6e67a21179a2da44b043ebef3d3854f26efb9ffde6aeef8
            //https://www.smartbit.com.au/tx/2db207f008822f90c6e67a21179a2da44b043ebef3d3854f26efb9ffde6aeef8

            if (ProjService.NETWORK_TYPE == ProjService.BlockCypherNetworkType.MAIN){
                String[] sites = new String[]{"Blockchain.info", "Smartbit", "SoChain", "chainFlyer"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(sites, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://blockchain.info/tx/" + verifyNotarizationTx.getHash())));
                                break;
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.smartbit.com.au/tx/" + verifyNotarizationTx.getHash())));
                                break;
                            case 2:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chain.so/tx/BTC/" + verifyNotarizationTx.getHash())));
                                break;
                            case 3:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://chainflyer.bitflyer.jp/Transaction/" + verifyNotarizationTx.getHash())));
                                break;
                        }

                    }
                });
                builder.setTitle("Proof");
                builder.show();
            }else if (ProjService.NETWORK_TYPE == ProjService.BlockCypherNetworkType.TEST3){
                String[] sites = new String[]{"SoChain"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(sites, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chain.so/tx/BTCTEST/" + verifyNotarizationTx.getHash())));
                                break;
                        }

                    }
                });
                builder.setTitle("Proof");
                builder.show();
            }


            return true;
        }
        if (id == R.id.action_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);

            String timestamp = null;
            StringBuilder message = new StringBuilder();
            if (verifyNotarizationTx.getConfirmed() != null){
                timestamp = StringUtils.parseStringToDate(verifyNotarizationTx.getConfirmed());
            } else {
                timestamp = getResources().getText(R.string.tv_timestamp).toString();
            }
            message.append(getResources().getText(R.string.tv_tx_id_title));
            message.append(" ");
            message.append(verifyNotarizationTx.getHash());
            message.append("\n\n");
            message.append(getResources().getText(R.string.tv_document_certificate_title));
            message.append(" ");
            message.append(dataHexHashString);
            message.append("\n\n");
            message.append(getResources().getText(R.string.tv_timestamp_title));
            message.append(" ");
            message.append(timestamp);
            message.append("\n\n");
            message.append(getResources().getText(R.string.search));
            message.append(" ");
            message.append("https://www.blocktrail.com/BTC/tx/");
            message.append(verifyNotarizationTx.getHash());

            intent.putExtra(Intent.EXTRA_TEXT, message.toString());
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // BEGIN_INCLUDE (initiate_refresh)
    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() throws IOException {
        if (CheckConnectivity.checkNow(this)) {
            VerifyNotarizationAsyncTask verifyNotarizationAsyncTask = new VerifyNotarizationAsyncTask(activity, null, verifyNotarizationTx.getHash());
            verifyNotarizationAsyncTask.execute();
        }else {
            UserMsgService.showDialog(this, R.string.alertdialog_no_connectivity_title,
                    R.string.alertdialog_no_connectivity);
        }
    }
    // END_INCLUDE (initiate_refresh)

    // BEGIN_INCLUDE (refresh_complete)
    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */
    public void onRefreshComplete() {
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }
    // END_INCLUDE (refresh_complete)

    public void setTx(Tx verifyNotarizationTx) {
        this.verifyNotarizationTx = verifyNotarizationTx;
        setTextViews();
        onRefreshComplete();
    }

}
