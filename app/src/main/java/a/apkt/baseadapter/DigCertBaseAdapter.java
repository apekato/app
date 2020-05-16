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
import a.apkt.model.DigCert;

public class DigCertBaseAdapter extends BaseAdapter {
    private View view;
    private List<DigCert> digCertList;
    private Activity activity;

    public DigCertBaseAdapter(Activity activity, List<DigCert> digCertList) {
        this.activity = activity;
        this.digCertList = digCertList;
    }

    @Override
    public int getCount() {
        return digCertList.size();
    }

    @Override
    public Object getItem(int position) {
        return digCertList.get(position);
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
            view = inflater.inflate(R.layout.list_dig_cert, null);
        } else {
            view = convertView;
        }
        setTextViews(position);
        return view;
    }

    public void setTextViews(int position) {
        DigCert digCert = digCertList.get(position);


//        TextView tvNameTitle = (TextView) view.findViewById(R.id.tv_name_title);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
//        TextView tvIdTitle = (TextView) view.findViewById(R.id.tv_id_title);
        TextView tvCpf = (TextView) view.findViewById(R.id.tv_cpf);
//        TextView tvExpirationTitle = (TextView) view.findViewById(R.id.tv_expiration_date_title);
        TextView tvExpirationDate = (TextView) view.findViewById(R.id.tv_expiration_date);

//        tvNameTitle.setText(activity.getResources().getText(R.string.tv_name_title));
        tvName.setText(digCert.getName());
//        tvIdTitle.setText(activity.getResources().getText(R.string.tv_cpf_title));
        tvCpf.setText(digCert.getCpf());
//        tvExpirationTitle.setText(activity.getResources().getText(R.string.tv_expiration_date_title));
//        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
//        String expirationDate = ft.format(digCert.getExpirationDate());
        tvExpirationDate.setText(digCert.getExpirationDate().toLocaleString());
    }
}
