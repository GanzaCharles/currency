package techhavoc.currencyconverter;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Settings extends AppCompatActivity {

    RecyclerView.Adapter adapt;
    RecyclerView.LayoutManager layoutManager;
    final DBHelper dbhelp = new DBHelper(this);
    SwipeRefreshLayout swipeContainer;
    ArrayList finalCurrency = new ArrayList();
    String action = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Settings");
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent data = new Intent();
                data.setData(Uri.parse(action));
                setResult(RESULT_OK, data);
                finish();

            }
        });
        
        populateView();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                finalCurrency.clear();
                populateView();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000);

            }
        });

    }

    public void populateView(){

        final HashMap<String, String> currencyNamesArray = dbhelp.currencies();
        final ArrayList<String> currencyIds = dbhelp.getCurrencyIds();

        int count = 0;

        for(HashMap.Entry<String, String> item : currencyNamesArray.entrySet()){

            String key = item.getKey();
            String value = item.getValue();

            CurrencyData data_provider = new CurrencyData(key, value, currencyIds.get(count));
            finalCurrency.add(data_provider);

            count++;

        }

        adapt = new RecyclerAdapter(finalCurrency, Settings.this);

        RecyclerView recyclerViewz = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(Settings.this);
        recyclerViewz.setLayoutManager(layoutManager);
        recyclerViewz.setAdapter(adapt);

    }

    public void removeCurrency(String currencyName){

        dbhelp.deleteCurrency(currencyName);
        action = "true";

    }

    public void rename_Currency(final String id, final String oldName, final String value){

        final Dialog dialog = new Dialog(Settings.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_currency);

        Button save_btn = (Button) dialog.findViewById(R.id.update_btn);
        final EditText currency_name_text_box = (EditText) dialog.findViewById(R.id.currency_name);
        final EditText currency_value_text_box = (EditText) dialog.findViewById(R.id.value);

        currency_name_text_box.setText(oldName);
        currency_value_text_box.setText(value);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String currency_name = currency_name_text_box.getText().toString();
                String currency_value = currency_value_text_box.getText().toString();

                final ArrayList<String> currencyNamesArray = dbhelp.currenciesSymbol();
                boolean checkExistence =  currencyNamesArray.contains(currency_name);

                if (!currency_name.isEmpty() && !currency_name.equals("android_metadata") && !currency_value.isEmpty() && currency_name.length() >= 2) {

                        dbhelp.updateCurrency(oldName, currency_name, currency_value);

                        finalCurrency.clear();
                        populateView();
                        dialog.dismiss();
                        action = "true";

                        currency_name_text_box.startAnimation(animator);

                } else {

                    if(currency_name.isEmpty()){
                        currency_name_text_box.startAnimation(animator);
                    } else if(currency_value.isEmpty()){
                        currency_value_text_box.startAnimation(animator);
                    }

                }

            }
        });

        dialog.show();

    }

    @Override
    protected void onStop() {

        Intent data = new Intent();
        data.setData(Uri.parse(action));
        setResult(RESULT_OK, data);

        super.onStop();
    }

    @Override
    public void onBackPressed() {

        Intent data = new Intent();
        data.setData(Uri.parse(action));
        setResult(RESULT_OK, data);

        super.onBackPressed();
    }
}
