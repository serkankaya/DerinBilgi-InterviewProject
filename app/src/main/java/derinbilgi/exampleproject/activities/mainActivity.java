package derinbilgi.exampleproject.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import derinbilgi.exampleproject.fragments.catalogsFragment;
import derinbilgi.exampleproject.fragments.contactFragment;
import derinbilgi.exampleproject.fragments.aboutFragment;
import derinbilgi.exampleproject.R;
import derinbilgi.exampleproject.adapters.viewPagerAdapter;
import derinbilgi.exampleproject.broadcast_receivers.notificationEventReceiver;

public class mainActivity extends AppCompatActivity {


    //This is our tablayout
    private TabLayout tabLayout;
    public static ProgressDialog proCheck;
    public static ProgressDialog proDownload;
    public static Double getMainLat;
    public static Double getMainLng;
    public static Boolean disableLocation=false;

    //This is our viewPager
    private ViewPager viewPager;

    //Fragments

    derinbilgi.exampleproject.fragments.catalogsFragment catalogsFragment;
    derinbilgi.exampleproject.fragments.aboutFragment aboutFragment;
    derinbilgi.exampleproject.fragments.contactFragment contactFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing viewPager
        notificationEventReceiver.setupAlarm(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager)
    {
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        catalogsFragment =new catalogsFragment();
        aboutFragment =new aboutFragment();
        contactFragment=new contactFragment();

        adapter.addFragment(catalogsFragment,"Catalogs");
        adapter.addFragment(aboutFragment,"About");
        adapter.addFragment(contactFragment,"Contact");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (disableLocation){
            finish();
            disableLocation=false;
        }
    }
}
