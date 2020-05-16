package a.apkt.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import a.apkt.R;
import a.apkt.model.OpReturn.OpReturnType;
import a.apkt.model.OpReturn;

public class OpReturnBaseAdapter extends BaseAdapter {
    private View view;
    private List<OpReturn> opTxReturnList;
    private Activity activity;

    public OpReturnBaseAdapter(
            Activity activity,
            List<OpReturn> opTxReturnList
    ) {
        this.activity = activity;
        this.opTxReturnList = opTxReturnList;
    }


    @Override
    public int getCount() {
        return opTxReturnList.size();
    }

    @Override
    public Object getItem(int position) {
        return opTxReturnList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //set white background color
        parent.setBackgroundColor(activity.getResources().getColor(R.color.white));

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_op_return, null);
        } else {
            view = convertView;
        }
        setTextViews(position);
        return view;
    }

    public void setTextViews(int position) {

        OpReturn opReturn = opTxReturnList.get(position);

        TextView tvTextTitle = (TextView) view.findViewById(R.id.tv_text_title);
        TextView tvText = (TextView) view.findViewById(R.id.tv_text);

        if (opReturn.getType().equals(OpReturnType.OP_RETURN_TYPE_TEXT)){
            tvTextTitle.setText(activity.getResources().getText(R.string.tv_message_title));
            tvText.setText(opReturn.getText());
        } else if (opReturn.getType().equals(OpReturnType.OP_RETURN_TYPE_NOTARIZATION)){
            tvTextTitle.setText(activity.getResources().getText(R.string.tv_document_certificate_title));
            tvText.setText(opReturn.getText());
        }
    }
}


