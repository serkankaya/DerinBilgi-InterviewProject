package derinbilgi.exampleproject.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import derinbilgi.exampleproject.activities.pdfViewer;
import derinbilgi.exampleproject.models.catalogCode;
import java.io.File;
import java.util.List;
import derinbilgi.exampleproject.R;

public class catalogCodeAdapter extends RecyclerView.Adapter<catalogCodeAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<catalogCode> my_data;


    public catalogCodeAdapter(Context context, List<catalogCode> my_data) {
        this.context = context;
        this.my_data = my_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogcard,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/DerinBilgiPdf/"+my_data.get(position).getCatalogCodes().toString()+".pdf");
        final String code=my_data.get(position).getCatalogCodes().toString();
        System.out.println(Environment.getExternalStorageDirectory().toString() + "/DerinBilgiPdf/"+my_data.get(position).getCatalogCodes().toString()+".pdf");
            holder.pdfViewer.fromFile(file).pages(0).load();
            holder.pdfViewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(), pdfViewer.class);
                    intent.putExtra("oneActivatePdfFileCode",code);
                    v.getContext().startActivity(intent);
                }
            });


    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    @Override
    public void onClick(View v) {

    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        public PDFView pdfViewer;
        public ViewHolder(View itemView) {
            super(itemView);
            pdfViewer = (PDFView) itemView.findViewById(R.id.pdfViewCard);

        }
    }

}
