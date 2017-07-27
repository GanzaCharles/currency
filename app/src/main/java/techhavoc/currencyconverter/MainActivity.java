package techhavoc.currencyconverter;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

/* this is the main class that controls other classes and the UI of the app */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    Button saveCurrency;
    ImageButton addNewCurrency;
    EditText currency_value, exchange_rate1, exchange_rate2;
    TextView first_currency_name, second_currency_name;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    Spinner first_currency_spinner, second_currency_spinner;
    final DBHelper dbhelp = new DBHelper(this);
    double rate1;
    double rate2;
    String firstSelectedCurrency, secondSelectedCurrency;
    String currencyValue2;
    String rateToUSD, rateToUSD2;
    String amount;
    RelativeLayout firstView, secondView;
    int activeView = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_content);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        saveCurrency = (Button) findViewById(R.id.save_btn);
        addNewCurrency = (ImageButton) findViewById(R.id.addNew);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        first_currency_spinner = (Spinner) findViewById(R.id.first_currency_spinner);
        second_currency_spinner = (Spinner) findViewById(R.id.second_currency_spinner);
        currency_value = (EditText) findViewById(R.id.amount);
        exchange_rate1 = (EditText) findViewById(R.id.exchange_rate1);
        exchange_rate2 = (EditText) findViewById(R.id.exchange_rate2);
        first_currency_name = (TextView) findViewById(R.id.first_text);
        second_currency_name = (TextView) findViewById(R.id.second_text);
        firstView = (RelativeLayout) findViewById(R.id.relative1);
        secondView = (RelativeLayout) findViewById(R.id.relative2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setTitle("Currency Converter");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addNewCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newCurrencyDialog();

            }
        });

        exchange_rate1.setText("1.0");
        exchange_rate2.setText("1.0");

        amount = currency_value.getText().toString();

        populateSpinners();

        firstView.setBackgroundResource(R.drawable.active);
        secondView.setBackgroundResource(R.drawable.listback_normal);

        first_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                firstSelectedCurrency = first_currency_spinner.getSelectedItem().toString();
                rateToUSD = dbhelp.getCurrencyValue(firstSelectedCurrency);
                currencyValue2 = first_currency_spinner.getSelectedItem().toString();
                convertCurrency();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        second_currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                secondSelectedCurrency = second_currency_spinner.getSelectedItem().toString();
                currencyValue2 = dbhelp.getCurrencyValue(secondSelectedCurrency);
                rateToUSD2 = dbhelp.getCurrencyValue(secondSelectedCurrency);
                exchange_rate2.setText(currencyValue2);
                convertCurrency();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* here we set the text change listeners which help in calculating the amount of currency as the user types */

        currency_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                        if(firstSelectedCurrency.equals(secondSelectedCurrency)){

                            first_currency_name.setText(s);
                            second_currency_name.setText(s);

                        } else {

                            if(!exchange_rate2.getText().toString().isEmpty()){

                                rate1 = Double.parseDouble(exchange_rate1.getText().toString());
                                rate2 = Double.parseDouble(exchange_rate2.getText().toString());

                                if(!s.toString().equals("")){

                                    amount = s.toString();
                                    double amount_d1 = Double.parseDouble(amount);
                                    double amountFinal = Double.parseDouble(rateToUSD2);
                                    String finalAmount;

                                    if(rateToUSD.equals("1.0") || rateToUSD.equals("1")){

                                        if(Double.parseDouble(rateToUSD2) >= 1){

                                            amount_d1 = amount_d1 * Double.parseDouble(rateToUSD2);
                                            finalAmount = new BigDecimal(amount_d1).stripTrailingZeros().toPlainString();

                                            second_currency_name.setText(s);
                                            first_currency_name.setText(finalAmount);

                                        } else {

                                            amount_d1 = amount_d1 / Double.parseDouble(rateToUSD2);
                                            finalAmount = new BigDecimal(amount_d1).stripTrailingZeros().toPlainString();

                                            second_currency_name.setText(s);
                                            first_currency_name.setText(finalAmount);

                                        }

                                    } else {

                                        if(Double.parseDouble(rateToUSD) >= 1){

                                            amount_d1 = amount_d1 * Double.parseDouble(rateToUSD);
                                            amountFinal = amount_d1 / amountFinal;
                                            finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();

                                            second_currency_name.setText(s);
                                            first_currency_name.setText(finalAmount.substring(0, finalAmount.lastIndexOf(".") + 3));

                                        } else {

                                            amount_d1 = amount_d1 / Double.parseDouble(rateToUSD);
                                            amountFinal = amount_d1 / amountFinal;
                                            finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();

                                            second_currency_name.setText(s);
                                            first_currency_name.setText(finalAmount.substring(0, finalAmount.lastIndexOf(".") + 3));

                                        }


                                    }

                                } else {
                                    first_currency_name.setText("0");
                                    second_currency_name.setText("0");
                                }

                            } else {

                                if(exchange_rate1.getText().toString().isEmpty()){

                                    Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                    exchange_rate1.startAnimation(animator);

                                }
                                if(exchange_rate2.getText().toString().isEmpty()){

                                    Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                    exchange_rate2.startAnimation(animator);

                                }

                            }

                        }
            }
        });

    }

    /*
    * this method converts the currency like this: if the operation is not from USD to x-currency, we first convert it to USD,
    * then convert the currency to the final currency using its rate to USD
    */
    public void convertCurrency(){

        switch (activeView){
            case 1:

                if(firstSelectedCurrency.equals(secondSelectedCurrency)){

                    first_currency_name.setText(amount);
                    second_currency_name.setText(amount);

                } else {

                    if(!exchange_rate2.getText().toString().isEmpty()){

                        rate1 = Double.parseDouble(exchange_rate1.getText().toString());
                        rate2 = Double.parseDouble(exchange_rate2.getText().toString());

                        if(!amount.isEmpty()){
                            double amount_d1 = Double.parseDouble(amount);
                            double amountFinal = Double.parseDouble(rateToUSD2);
                            String finalAmount;

                            if(rateToUSD.equals("1.0") || rateToUSD.equals("1")){

                                if(Double.parseDouble(rateToUSD2) >= 1){

                                    amount_d1 = amount_d1 * Double.parseDouble(rateToUSD2);
                                    finalAmount = new BigDecimal(amount_d1).stripTrailingZeros().toPlainString();

                                    second_currency_name.setText(amount);
                                    first_currency_name.setText(finalAmount);

                                } else {

                                    amount_d1 = amount_d1 / Double.parseDouble(rateToUSD2);
                                    finalAmount = new BigDecimal(amount_d1).stripTrailingZeros().toPlainString();

                                    second_currency_name.setText(amount);
                                    first_currency_name.setText(finalAmount);

                                }

                            } else {

                                if(Double.parseDouble(rateToUSD) >= 1){
                                    //multiply

                                    amount_d1 = amount_d1 * Double.parseDouble(rateToUSD);
                                    amountFinal = amount_d1 / amountFinal;
                                    finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();

                                } else {
                                    //divide
                                    amount_d1 = amount_d1 / Double.parseDouble(rateToUSD);
                                    amountFinal = amount_d1 / amountFinal;
                                    finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();
                                }

                                //String finalAmount = convertCurrency(amount_d1, rate1, rate2);

                                second_currency_name.setText(amount);
                                first_currency_name.setText(finalAmount.substring(0, finalAmount.lastIndexOf(".") + 3));

                            }

                        } else {
                            first_currency_name.setText("0");
                            second_currency_name.setText("0");
                        }

                    } else {

                        if(exchange_rate1.getText().toString().isEmpty()){

                            Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                            exchange_rate1.startAnimation(animator);

                        }
                        if(exchange_rate2.getText().toString().isEmpty()){

                            Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                            exchange_rate2.startAnimation(animator);

                        }

                    }

                }

                break;

            case 2:

                if(firstSelectedCurrency.equals(secondSelectedCurrency)){

                    first_currency_name.setText(amount);
                    second_currency_name.setText(amount);

                } else {

                    if(!exchange_rate2.getText().toString().isEmpty()){

                        rate1 = Double.parseDouble(exchange_rate1.getText().toString());
                        rate2 = Double.parseDouble(exchange_rate2.getText().toString());

                        if(!amount.isEmpty()){
                            double amount_d1 = Double.parseDouble(amount);
                            String finalAmount; /* = convertCurrency(amount_d1, rate1, rate2);*/

                            double amountFinal = Double.parseDouble(rateToUSD);

                            if(Double.parseDouble(rateToUSD2) >= 1){
                                //multiply

                                amount_d1 = amount_d1 * Double.parseDouble(rateToUSD2);
                                amountFinal = amount_d1 / amountFinal;
                                finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();


                            } else {
                                //divide
                                amount_d1 = amount_d1 / Double.parseDouble(rateToUSD2);
                                amountFinal = amount_d1 / amountFinal;
                                finalAmount = new BigDecimal(amountFinal).stripTrailingZeros().toPlainString();

                            }

                            first_currency_name.setText(amount);
                            second_currency_name.setText(finalAmount.substring(0, finalAmount.lastIndexOf(".") + 3));

                        } else {
                            first_currency_name.setText("0");
                            second_currency_name.setText("0");
                        }

                    } else {

                        if(exchange_rate1.getText().toString().isEmpty()){

                            Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                            exchange_rate1.startAnimation(animator);

                        }
                        if(exchange_rate2.getText().toString().isEmpty()){

                            Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                            exchange_rate2.startAnimation(animator);

                        }

                    }

                }

                break;
        }

    }

    /*
    * method to display a new currency dialog. accepts the currency name and its rate to USD
    */
    public void newCurrencyDialog(){

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_currency);

        Button save_btn = (Button) dialog.findViewById(R.id.save_btn);
        final EditText currency_name_text_box = (EditText) dialog.findViewById(R.id.currency_name);
        final EditText currency_value_text_box = (EditText) dialog.findViewById(R.id.value);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                String currency_name = currency_name_text_box.getText().toString();
                String currency_value = currency_value_text_box.getText().toString();
                final ArrayList<String> currencyNamesArray = dbhelp.currenciesSymbol();

                boolean checkExistence =  currencyNamesArray.contains(currency_name);

                if (!currency_name.isEmpty() && !currency_name.equals("android_metadata") && !currency_value.isEmpty() && currency_name.length() >= 2) {

                    if(!checkExistence){
                        dbhelp.insertCurrency(currency_name, currency_value);
                        populateSpinners();
                        first_currency_name.setText("0");
                        second_currency_name.setText("0");
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "That currency already exists!", Toast.LENGTH_SHORT).show();
                        currency_name_text_box.startAnimation(animator);
                    }

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

    // method to load data into the spinners. can be called anytime to update them
    public void populateSpinners(){

        final ArrayList<String> currencyNamesArray = dbhelp.currenciesSymbol();
        ArrayAdapter<String> currencyNames = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                currencyNamesArray);

        currencyNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        first_currency_spinner.setAdapter(currencyNames);
        second_currency_spinner.setAdapter(currencyNames);

    }

    //check the intent result from the settings activity, this helps determine whether or not we should update the spinners (when currencies are edited or deleted)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String returnedData = data.getData().toString();

                switch (returnedData){
                    case "true":
                        populateSpinners();
                        break;

                    case "false":
                        break;
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // start the settings activity when the user clicks on the Settings menu item in the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.settings:

                Intent im = new Intent(MainActivity.this, Settings.class);
                im.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(im, 1);

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }
}
