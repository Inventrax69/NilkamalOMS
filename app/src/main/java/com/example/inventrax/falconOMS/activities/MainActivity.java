package com.example.inventrax.falconOMS.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.fragments.CartFragment;
import com.example.inventrax.falconOMS.fragments.HomeFragmentHints;
import com.example.inventrax.falconOMS.fragments.ProfileFragment;
import com.example.inventrax.falconOMS.fragments.SearchFragment;
import com.example.inventrax.falconOMS.logout.LogoutUtil;
import com.example.inventrax.falconOMS.util.AndroidUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    Toolbar toolbar;
    BottomNavigationView navigation;
    private CoordinatorLayout coordinatorLayout;
    private LogoutUtil logoutUtil;


    ActionBar actionBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    loadFragment(new HomeFragmentHints());
                    return true;

                case R.id.navigation_bag:

                    return true;

                case R.id.navigation_cart:
                    loadFragment(new CartFragment());
                    return true;

                case R.id.navigation_search:
                    loadFragment(new SearchFragment());
                    return true;

                case R.id.navigation_more:
                    loadFragment(new ProfileFragment());
                    return true;

            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        Log.d("ABCDE", "Refreshed token:" + FirebaseInstanceId.getInstance().getToken());

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snack_bar_action_layout);

        setSupportActionBar(toolbar);

        // gets the tool bar icon and handles click event
        toolbar.setLogo(ContextCompat.getDrawable(MainActivity.this, R.drawable.nilkamal));
        View logoView = AndroidUtils.getToolbarLogoIcon(toolbar);
        if (logoView != null)
            logoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new ProfileFragment());
                }
            });

        // gets the action bar support to enable features
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // load the store fragment by default
        loadFragment(new HomeFragmentHints());


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);       // removes bottom navigation bar animation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logoutUtil = new LogoutUtil();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Log.i( "ddere","Extra:" + extras.getString("whattodo") );
        }


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // to show or hide bottom navigation bar from different fragments
    public void SetNavigationVisibiltity(boolean b) {
        if (b) {
            navigation.setVisibility(View.VISIBLE);
        } else {
            navigation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout: {

                logoutUtil.doLogout(MainActivity.this);
            }
            break;

            /*case R.id.action_about: {

                //FragmentUtils.replaceFragmentWithBackStack(this, R.id.container_body, new AboutFragment());
            }
            break;*/
            case R.id.action_profile: {

                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new ProfileFragment());
            }
            break;

            case R.id.action_notification: {

                Toast.makeText(MainActivity.this, "Notifications pressed", Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.action_cart: {

                FragmentUtils.replaceFragmentWithBackStack(this, R.id.container, new CartFragment());
            }
            break;

            case android.R.id.home: {

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                onBackPressed();

            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        /*if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }*/

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }


}

class BottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
    static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }
}



