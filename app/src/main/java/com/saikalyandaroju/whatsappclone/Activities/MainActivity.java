package com.saikalyandaroju.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.VersionedPackage;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.saikalyandaroju.whatsappclone.Fragments.ChatsFragment;
import com.saikalyandaroju.whatsappclone.R;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    MaterialToolbar toolbar;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    MenuItem item;
    boolean serachview = false;

    MutableLiveData<String> search = new MutableLiveData<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);

        setSupportActionBar(toolbar);
        viewPager2 = findViewById(R.id.viewPager);
        initPrefrences();

        viewPager2.setAdapter(new ViewPageAdaper(this));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("CHATS");
                        break;
                    case 1:
                        tab.setText("PEOPLE");
                        break;

                }
            }
        });
        tabLayoutMediator.attach();
        int[] tabIcons = {
                R.drawable.ic_baseline_chat_bubble_24,
                R.drawable.ic_baseline_people_24
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        int tab_icon_color1 = ContextCompat.getColor(getApplicationContext(), R.color.white);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tab_icon_color1, PorterDuff.Mode.SRC_IN);
        int tab_icon_color = ContextCompat.getColor(getApplicationContext(), R.color.gray);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tab_icon_color, PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tab_icon_color = ContextCompat.getColor(getApplicationContext(), R.color.white);
                tab.getIcon().setColorFilter(tab_icon_color, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tab_icon_color = ContextCompat.getColor(getApplicationContext(), R.color.gray);
                tab.getIcon().setColorFilter(tab_icon_color, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (!sharedPreferences.getBoolean("batteryOptimizationDialogShown", false)) {
            batteryOptimization();
        }

    }

    private void initPrefrences() {
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void batteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery Optimization is required for extra features,its optional,it can interrupt background srvices");
                builder.setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivityForResult(intent, 100);
                        editor.putBoolean("batteryOptimizationDialogShown", true).apply();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("batteryOptimizationDialogShown", true).apply();
                        dialog.dismiss();
                    }
                }).create().show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            batteryOptimization();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!serachview) {
            Timber.i("normal view");

            getMenuInflater().inflate(R.menu.settings, menu);
        } else {
            Timber.i("search view");


            getMenuInflater().inflate(R.menu.search, menu);
            MenuItem item = menu.findItem(R.id.searchbar);
            item.expandActionView();
            final androidx.appcompat.widget.SearchView mSearchView = (androidx.appcompat.widget.SearchView) item.getActionView();
            mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mSearchView.setOnQueryTextListener((androidx.appcompat.widget.SearchView.OnQueryTextListener) MainActivity.this);
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    serachview = false;
                    supportInvalidateOptionsMenu();
                    search.postValue("");

                    return true;
                }
            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;


            case R.id.search:
                Log.i("check", "called");
                serachview = true;
                supportInvalidateOptionsMenu();


                break;
        }
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Timber.i(newText);
        if (!TextUtils.isEmpty(newText)) {
            search.postValue(newText);
        } else {
            search.postValue("");
        }
        return true;
    }


    public LiveData<String> getSearch() {
        return search;
    }
}
