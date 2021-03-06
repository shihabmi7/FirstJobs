package com.bitmakers.firstjobs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bitmakers.firstjobs.adapter_pkg.FavouriteAdapter;
import com.bitmakers.firstjobs.app_data.AppData;
import com.bitmakers.firstjobs.app_data.AppUrl;
import com.bitmakers.firstjobs.app_data.XInternetServices;
import com.bitmakers.firstjobs.database.DBActions;
import com.bitmakers.firstjobs.database.DBAdapter;
import com.bitmakers.firstjobs.jsonparser.JSON;
import com.bitmakers.firstjobs.jsonparser.JSONParser;
import com.bitmakers.firstjobs.model_class.JobFavouriteList;

public class SavedJobActivity extends AppCompatActivity {

    ListView lv;
    private DBActions dbAction;
    private FavouriteAdapter fAdapter;

    SharedPreferences pref;
    String token;
    //private ArrayList<News> contentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getApplicationContext().getSharedPreferences("FirstJob_Login", 0);
        token = pref.getString("user_token", "");


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.mipmap.jobportal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAction = new DBActions(getApplicationContext());

        lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDetailsWeb(AppData.newsFavListt.get(position).getId(), token, true);
            }
        });
        getLoadDataFromWeb();

    }


    public void getLoadDataFromWeb() {

        if (XInternetServices.isNetworkAvailable(SavedJobActivity.this)) {
            class  LoadHomeData extends AsyncTask<Void,Void,Void> {
                ProgressDialog progressDialog;
                //&page=0&token=5762323e6a49d00bd73de8bd"
                @Override
                protected Void doInBackground(Void... params) {
                    Cursor c = dbAction.getRows();

                    if (c.moveToFirst()) {
                        do {
                            // Process the data:
                            int row_id = c.getInt(DBAdapter.COL_ROWID);

                            String job_id = c.getString(c.getColumnIndex(DBAdapter.KEY_ID));
                            String summary = c.getString(DBAdapter.COL_SUMMARY);
                            String details = c.getString(DBAdapter.COL_DETAILS);
                            String expire = c.getString(DBAdapter.COL_EXPIRE);
                            String job_dist = c.getString(DBAdapter.COL_JOB_DIST);
                            String job_exp = c.getString(DBAdapter.COL_JOB_EXP);
                            String job_status  = c.getString(DBAdapter.COL_JOB_STATUS);
                            String city = c.getString(DBAdapter.COL_CITY);
                            String type = c.getString(DBAdapter.COL_TYPE);
                            String name = c.getString(DBAdapter.COL_NAME);
                            String country = c.getString(DBAdapter.COL_JOB_COUNTRY);
                            String salary = c.getString(DBAdapter.COL_SALARY);
                            String img = c.getString(DBAdapter.COL_IMG);
                            String skill = c.getString(DBAdapter.COL_SKILL);

                            AppData.newsFavListt.add(new JobFavouriteList(row_id, job_id,summary, details, expire, job_dist,
                                    job_exp, job_status, city, type, name,
                                    country, salary, img, skill));

                        } while (c.moveToNext());
                    }

                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    AppData.newsFavListt.clear();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (AppData.newsFavListt.size() == 0) {
//                        emptyListTextView.setVisibility(View.VISIBLE);
                    } else {
//                        emptyListTextView.setVisibility(View.GONE);
                        fAdapter = new FavouriteAdapter(SavedJobActivity.this, AppData.newsFavListt);
                        lv.setAdapter(fAdapter);
                    }

                    super.onPostExecute(aVoid);
                }
            }
            new LoadHomeData().execute();
        } else {
            android.support.v7.app.AlertDialog.Builder builder1;
            builder1 = new android.support.v7.app.AlertDialog.Builder(SavedJobActivity.this);
            builder1.setMessage("Please check your internet connection");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity) SavedJobActivity.this).finish();
                        }
                    });
            android.support.v7.app.AlertDialog alert11 = builder1.create();
            alert11.show();

        }

    }



    public void getDetailsWeb(final String jobId, final String token, final boolean showProgress) {

        if (XInternetServices.isNetworkAvailable(SavedJobActivity.this)) {
            class  LoadHomeData extends AsyncTask<Void,Void,Void>{
                ProgressDialog progressDialog;

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        AppData.finalDeatilsResultFromServer = new JSONParser().thePostRequest(
                                AppUrl.dtailsJob+"&id_job="+jobId+"&token="+token, "");
                        AppData.jobDetailsLists= new JSON().parseDetailsNews(AppData.finalDeatilsResultFromServer );

                        System.out.println("WWWWWW >>>"+AppData.jobDetailsLists.get(0).getCompany_info().getCom_name());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    if (showProgress == true) {
                        progressDialog = ProgressDialog.show(SavedJobActivity.this, "",
                                "Loading. Please Wait...");
                        progressDialog.setCancelable(false);
                    }
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (showProgress == true) {
                        progressDialog.dismiss();
                    }

                    // startActivity(new );
                    startActivity(new Intent(SavedJobActivity.this, JobDetailsActivity.class));

                    super.onPostExecute(aVoid);
                }
            }
            new LoadHomeData().execute();
        } else {
            android.support.v7.app.AlertDialog.Builder builder1;
            builder1 = new android.support.v7.app.AlertDialog.Builder(SavedJobActivity.this);
            builder1.setMessage("Please check your internet connection");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((Activity) SavedJobActivity.this).finish();
                        }
                    });
            android.support.v7.app.AlertDialog alert11 = builder1.create();
            alert11.show();

        }

    }


}
