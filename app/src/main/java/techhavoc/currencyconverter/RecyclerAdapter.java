package techhavoc.currencyconverter;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * Created by Ghost Karl on 12/06/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList<CurrencyData> currencies = new ArrayList<>();
    Context ctx;

    public RecyclerAdapter(ArrayList<CurrencyData> currencies, Context ctx){

        this.currencies = currencies;
        this.ctx = ctx;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new RecyclerViewHolder(view, ctx, currencies);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        CurrencyData CurrencyData = currencies.get(position);
        holder.currencyName.setText(CurrencyData.getcurrencyName());
        holder.toUSD.setText(CurrencyData.getUSD() + " = $1");
        holder.cu_id.setText(CurrencyData.getCuID());

        final String currencyName = CurrencyData.getcurrencyName();
        final String currencyValue = CurrencyData.getUSD();
        final String cu_id = CurrencyData.getCuID();

        holder.currencyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu chooser = new PopupMenu(ctx, holder.currencyView);
                chooser.getMenuInflater().inflate(R.menu.pop_menu, chooser.getMenu());

                chooser.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String it = (String) item.getTitle();
                        switch (it){
                            case "Edit":

                                renameCurrency(cu_id, currencyName, currencyValue);

                                break;

                            case "Delete":

                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                                builder.setTitle("Are you sure?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        removeCurrency(position, currencyName);

                                    }
                                });


                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                                break;

                        }
                        return false;
                    }
                });
                chooser.show(); //show the pop up menu

            }
        });

        holder.optionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu chooser = new PopupMenu(ctx, holder.optionsView);
                chooser.getMenuInflater().inflate(R.menu.pop_menu, chooser.getMenu());

                chooser.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String it = (String) item.getTitle();
                        switch (it){
                            case "Edit":

                                renameCurrency(cu_id, currencyName, currencyValue);

                                break;

                            case "Delete":

                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                                builder.setTitle("Are you sure?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        removeCurrency(position, currencyName);

                                    }
                                });


                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                                break;

                        }
                        return false;
                    }
                });
                chooser.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView optionsView;
        RelativeLayout currencyView;
        TextView currencyName, toUSD, cu_id;
        ArrayList<CurrencyData> currencies = new ArrayList<>();
        Context ctx;

        RecyclerViewHolder(View itemView, Context ctx, ArrayList<CurrencyData> currencies) {

            super(itemView);

            optionsView = (ImageView) itemView.findViewById(R.id.options);

            this.currencies = currencies;
            this.ctx = ctx;

            currencyView = (RelativeLayout) itemView.findViewById(R.id.currencyView);
            currencyName = (TextView) itemView.findViewById(R.id.playlist);
            cu_id = (TextView) itemView.findViewById(R.id.cu_id);
            toUSD = (TextView) itemView.findViewById(R.id.tousd);

        }

    }

    private void removeCurrency(int pos, String currencyName){

        currencies.remove(pos);
        this.notifyItemRemoved(pos);
        ((Settings) ctx).removeCurrency(currencyName);

    }

    private void renameCurrency(String id, String oldName, String value){

        ((Settings) ctx).rename_Currency(id, oldName, value);

    }

}
