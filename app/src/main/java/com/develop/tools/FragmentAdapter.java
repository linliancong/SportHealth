package com.develop.tools;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.develop.sporthealth.HomeSy;
import com.develop.sporthealth.InteractSy;
import com.develop.sporthealth.MeSy;
import com.develop.sporthealth.PlanSy;

/**
 * Created by Administrator on 2017/11/29.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT=4;
    private final int PAGER_1=0;
    private final int PAGER_2=1;
    private final int PAGER_3=2;
    private final int PAGER_4=3;

    private Context context;

    private HomeSy homeSy=null;
    private PlanSy planSy=null;
    private InteractSy interactSy=null;
    private MeSy meSy=null;


    public FragmentAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context=context;

        homeSy=new HomeSy(context);
        planSy=new PlanSy(context);
        interactSy=new InteractSy(context);
        meSy=new MeSy(context);

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position)
        {
            case PAGER_1:
                fragment=homeSy;
                break;
            case PAGER_2:
                fragment=planSy;
                break;
            case PAGER_3:
                fragment=interactSy;
                break;
            case PAGER_4:
                fragment=meSy;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
