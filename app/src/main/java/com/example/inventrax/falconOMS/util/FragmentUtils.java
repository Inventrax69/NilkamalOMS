package com.example.inventrax.falconOMS.util;

/**
 * Created by nareshp on 05/01/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    private static FragmentManager fragmentManager;
    private static FragmentTransaction fragmentTransaction;

    /**
     * To add fragment to the container
     *
     * @param activity          current FragmentActivity reference
     * @param fragmentContainer fragment container which holds the specified fragment
     * @param fragment          fragment added to the specified container
     */
    public static void addFragment(FragmentActivity activity, int fragmentContainer, Fragment fragment) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.add(fragmentContainer, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    /**
     * To add fragment to the container with back stack option
     *
     * @param activity          current FragmentActivity reference
     * @param fragmentContainer fragment container which holds the specified fragment
     * @param fragment          fragment added to the specified container
     */
    public static void addFragmentWithBackStack(FragmentActivity activity, int fragmentContainer, Fragment fragment) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.add(fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    /**
     * To replace fragment for the specified container
     *
     * @param activity          current FragmentActivity reference
     * @param fragmentContainer fragment container which holds the specified fragment
     * @param fragment          fragment added to the specified container
     */
    public static void replaceFragment(FragmentActivity activity, int fragmentContainer, Fragment fragment) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(fragmentContainer, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    /**
     * To replace fragment for the specified container with back stack option
     *
     * @param activity          current FragmentActivity reference
     * @param fragmentContainer fragment container which holds the specified fragment
     * @param fragment          fragment added to the specified container
     */
    public static void replaceFragmentWithBackStack(FragmentActivity activity, int fragmentContainer, Fragment fragment) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    public static void replaceFragmentWithBackStackWithBundle(FragmentActivity activity, int fragmentContainer, Fragment fragment, Bundle bundle) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(fragmentContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    public static void replaceFragmentWithBundle(FragmentActivity activity, int fragmentContainer, Fragment fragment, Bundle bundle) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(fragmentContainer, fragment);
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    /**
     * To remove fragment from the container
     *
     * @param activity current FragmentActivity reference
     * @param fragment fragment added to the specified container
     */
    public static void removeFragment(FragmentActivity activity, Fragment fragment) {

        try {
            fragmentManager = activity.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {

        }
    }

    public static void clearBackStack(FragmentActivity activity) {
        fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


}