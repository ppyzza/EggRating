package com.eggdigital.android.eggrating;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eggdigital.eggrating.R;

import java.util.Date;

import static com.eggdigital.android.eggrating.Constants.EGGRATING_AGAIN_STATUS;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_FIRST_DIALOG;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_LASTSAVE_VERSION;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_LAUNCH_DATE;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_LAUNCH_TIMES;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_STATE_VERSION;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_STORE_DIALOG;
import static com.eggdigital.android.eggrating.Constants.EGGRATING_THANKS_DIALOG;
import static com.eggdigital.android.eggrating.Constants.PREF_NAME;
import static com.eggdigital.android.eggrating.Constants.TAG_NAME;

/**
 * An Android Library For Rating Application on Play Store
 * it's can set title, message, color and etc by yourself.
 */

public class EggRating {

    private Activity mActivity;
    private Configuration mConfiguration;
    private Context mContext;
    private boolean mDebugMode = false;
    private TextView mTextViewTitle;
    private TextView mTextViewDesc;
    private RatingBar mRatingBar;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private int firstTimeShow = 10;
    private int retryTimeShow = 10;


    /**
     * an Method that set debug mode for show dialog for testing
     */
    public void setmDebugMode(boolean mDebugMode) {
        this.mDebugMode = mDebugMode;
    }

    /**
     * An interface that contains the callback used by the AlertDialog
     * <p>
     * @return tag EGGRATING_FIRST_DIALOG, EGGRATING_THANKS_DIALOG, EGGRATING_STORE_DIALOG;
     * @see Configuration
     */
    public interface OnSelectCallBack {
        void onPositive(String tag);
        void onNegative(String tag);
    }

    /**
     * an Constructor Method.
     */
    public EggRating(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * an Configuration Method.
     */
    public Configuration getmConfiguration() {
        return mConfiguration;
    }


    /**
     * an Initial Method.
     */
    public void initial(Context context) {
        mContext = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        // If it is the first launch, save the date in shared preference.
//        if (pref.getLong(EGGRATING_INSTALL_DATE, 0) == 0L) {
//            storeInstallDate(context);
//        }
        getVersionCode(mContext);
        storeLaunchDate(editor);
        mConfiguration = new Configuration(firstTimeShow, retryTimeShow);


    }

    /**
     * an Method clear launchTime after display first dialog or user click cancel.
     */
    private void clearLaunchTimes(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt(EGGRATING_LAUNCH_TIMES, 0);
        editor.apply();
    }

    /**
     * an Method rate again condition after user click cancel.
     */
    private void setAgainRate(Context context) {
        if(mConfiguration.mTryAgain) {
            pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(EGGRATING_AGAIN_STATUS, true);
            editor.apply();
        }
    }

    /**
     * an Method check condition before show dialog.
     */
    private boolean couldBeShowRateDialog() {

        if(mDebugMode) {
            return true;
        } else {
            int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
            boolean statusAgain = pref.getBoolean(EGGRATING_AGAIN_STATUS, false);
            if (!statusAgain) {
                if (launchTimesReal >= mConfiguration.mCriteriaLaunchTimes) {
                    clearLaunchTimes(mContext);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (launchTimesReal >= mConfiguration.mCriteriaLaunchTimesReTry) {
                    clearLaunchTimes(mContext);
                    return true;
                }
                return false;
            }
        }
    }

    /**
     * an Method check condition before show dialog.
     */
    private void showAlertThanks(final OnSelectCallBack mOnSelectCallBack) {
        int titleId = mConfiguration.mTitleThanksId != 0 ? mConfiguration.mTitleThanksId : R.string.rateus_text_thanks_title;
        int messageId = mConfiguration.mTitleThanksDescId != 0 ? mConfiguration.mTitleThanksDescId : R.string.rateus_text_thanks_description;
        int okId = mConfiguration.mYesButtonId != 0 ? mConfiguration.mYesButtonId : R.string.rateus_text_thanks_ok;

        final AlertDialog.Builder mAlert = new AlertDialog.Builder(mActivity);
        mAlert.setCancelable(true);
        mAlert.setTitle(titleId);
        mAlert.setMessage(messageId);
        mAlert.setPositiveButton(okId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mOnSelectCallBack.onPositive(EGGRATING_THANKS_DIALOG);
            }
        });
        mAlert.show();

    }

    /**
     * an Method show dialog PlayStore.
     */
    private void showAlertPlayStore(final OnSelectCallBack mOnSelectCallBack) {
        int titleId = mConfiguration.mTitleStoreId != 0 ? mConfiguration.mTitleStoreId : R.string.rateus_text_store_title;
        int messageId = mConfiguration.mDescStoreId != 0 ? mConfiguration.mDescStoreId : R.string.rateus_text_store_description;
        int okId = mConfiguration.mYesButtonId != 0 ? mConfiguration.mYesButtonId : R.string.rateus_text_store_ok;
        int cancelId = mConfiguration.mCancelButton != 0 ? mConfiguration.mCancelButton : R.string.rateus_text_store_cancel;

        final AlertDialog.Builder mAlert = new AlertDialog.Builder(mActivity);
        mAlert.setCancelable(true);
        mAlert.setTitle(titleId);
        mAlert.setMessage(messageId);
        mAlert.setPositiveButton(okId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                storeVersionCode(mActivity);
                String appPackage = mActivity.getPackageName();
                String url = "https://play.google.com/store/apps/details?id=" + appPackage;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(intent);
                mOnSelectCallBack.onPositive(EGGRATING_STORE_DIALOG);
            }
        });
        mAlert.setNegativeButton(cancelId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mOnSelectCallBack.onNegative(EGGRATING_STORE_DIALOG);
            }
        });
        mAlert.show();

    }

    /**
     * Returns an Interface callback.
     * onPositive button click and onNegative button click and it's should be show by condition of days by initial value.
     * <p>
     * This method always returns when user click button.
     * @return callback
     */
    public void showAlertRateUS(final OnSelectCallBack mOnSelectCallBack) {
        if(mConfiguration.getmCriteriaLaunchTimes() == 0) {
            Log.d(TAG_NAME, mActivity.getString(R.string.rateus_more_than_zero));
        } else {
            if (couldBeShowRateDialog()) {
                clearLaunchTimes(mContext);
                LayoutInflater inflater = mActivity.getLayoutInflater();
                @SuppressLint("InflateParams")
                View dialogView = inflater.inflate(R.layout.dialog_rateus, null);
                int titleId = mConfiguration.mTitleId != 0 ? mConfiguration.mTitleId : R.string.rateus_text_title;
                int messageId = mConfiguration.mMessageId != 0 ? mConfiguration.mMessageId : R.string.rateus_text_description;
                int cancelId = mConfiguration.mCancelButton != 0 ? mConfiguration.mCancelButton : R.string.rateus_text_cancel;
                int okId = mConfiguration.mYesButtonId != 0 ? mConfiguration.mYesButtonId : R.string.rateus_text_ok;
                int colorBorderId = mConfiguration.mColorHighlight != 0 ? mConfiguration.mColorHighlight : R.color.rateus_color_hilight;
                int colorHilightId = mConfiguration.mColorBorder != 0 ? mConfiguration.mColorBorder : R.color.rateus_color_border;
                int colorNonHilightId = mConfiguration.mColorNormal != 0 ? mConfiguration.mColorNormal : R.color.rateus_color_normal;
                final float rating = mConfiguration.getmRating() != 0 ? mConfiguration.getmRating() : 4;

                final AlertDialog.Builder mAlert = new AlertDialog.Builder(mActivity);
                mRatingBar = (RatingBar) dialogView.findViewById(R.id.rateus_ratingbar);
                mTextViewTitle = (TextView) dialogView.findViewById(R.id.rateus_title);
                mTextViewDesc = (TextView) dialogView.findViewById(R.id.rateus_desc);

                mTextViewTitle.setText(titleId);
                mTextViewTitle.setTypeface(null, Typeface.BOLD);
                mTextViewDesc.setText(messageId);

                LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, colorHilightId), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(ContextCompat.getColor(mContext, colorBorderId), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext, colorNonHilightId), PorterDuff.Mode.SRC_ATOP);

                mAlert.setView(dialogView);
                mAlert.setCancelable(false);
                mAlert.setPositiveButton(okId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mOnSelectCallBack.onPositive(EGGRATING_FIRST_DIALOG);
                        if (mRatingBar.getRating() >= rating) {
                            storeVersionCode(mContext);
                            showAlertPlayStore(mOnSelectCallBack);
                        } else {
                            if (mConfiguration.mTryAgain) {
                                setAgainRate(mContext);
                            } else {
                                storeVersionCode(mContext);
                            }
                            showAlertThanks(mOnSelectCallBack);
                        }
                    }
                });
                mAlert.setNegativeButton(cancelId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mConfiguration.mTryAgain) {
                            setAgainRate(mContext);
                        }
                        mOnSelectCallBack.onNegative(EGGRATING_FIRST_DIALOG);
                    }
                });
                mAlert.show();
            }
        }
    }

//    private static void storeInstallDate(final Context context) {
//        SharedPreferences.Editor editor = pref.edit();
//        Date installDate = new Date();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            PackageManager packMan = context.getPackageManager();
//            try {
//                PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
//                installDate = new Date(pkgInfo.firstInstallTime);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        editor.putLong(EGGRATING_INSTALL_DATE, installDate.getTime());
//        editor.apply();
//        Log.d(TAG_NAME, "first install"+ installDate.toString());
//    }

    public void spamLaunchDate() {
        SharedPreferences.Editor editor = pref.edit();
        storeLaunchDate(editor);
        Log.d(TAG_NAME, "status"+mDebugMode+":");
    }

    /**
     * a Method storeLaunchDate call and save count time of launch and count increase 1 per day.
     * @param editor
     */
    private void storeLaunchDate(SharedPreferences.Editor editor) {
        boolean versionStatus = pref.getBoolean(EGGRATING_STATE_VERSION, false);
        Date launchDate = new Date();
        Log.d(TAG_NAME, "" + DateFormat.format("yyyy-MM-dd", launchDate));
        String dateCompare = DateFormat.format("yyyy-MM-dd", launchDate).toString();
        String datePref = pref.getString(EGGRATING_LAUNCH_DATE, "");

        if(!versionStatus) {
            if (!dateCompare.equals(datePref)) {
                int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
                launchTimesReal++;
                editor.putString(EGGRATING_LAUNCH_DATE, dateCompare);
                editor.putInt(EGGRATING_LAUNCH_TIMES, launchTimesReal);
                Log.d(TAG_NAME, "Launch times" + launchTimesReal);
                editor.commit();
            } else {
                if(mDebugMode) {
                    int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
                    launchTimesReal++;
                    editor.putString(EGGRATING_LAUNCH_DATE, dateCompare);
                    editor.putInt(EGGRATING_LAUNCH_TIMES, launchTimesReal);
                    Log.d(TAG_NAME, "Launch times; \"" + launchTimesReal);
                    editor.commit();
                } else {
                    int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
                    Log.d(TAG_NAME, "Launch times; \""+ launchTimesReal);
                }
            /*
            int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
            Log.d(TAG_NAME, "Launch times; \""+ launchTimesReal);
            */
            }
        } else {

            if(mDebugMode) {
                editor.putInt(EGGRATING_LAUNCH_TIMES, 0);
                editor.putBoolean(EGGRATING_STATE_VERSION, false);
                editor.commit();
                int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
                launchTimesReal++;
                editor.putString(EGGRATING_LAUNCH_DATE, dateCompare);
                editor.putInt(EGGRATING_LAUNCH_TIMES, launchTimesReal);
                Log.d(TAG_NAME, "Launch times; \"" + launchTimesReal);
                editor.commit();
            }

            if(getVersionCode(mContext)) {
                editor.putInt(EGGRATING_LAUNCH_TIMES, 0);
                editor.putBoolean(EGGRATING_STATE_VERSION, false);
                editor.commit();
                int launchTimesReal = pref.getInt(EGGRATING_LAUNCH_TIMES, 0);
                launchTimesReal++;
                editor.putString(EGGRATING_LAUNCH_DATE, dateCompare);
                editor.putInt(EGGRATING_LAUNCH_TIMES, launchTimesReal);
                Log.d(TAG_NAME, "Launch times; \"" + launchTimesReal);
                editor.commit();
            }
        }
    }


    /**
     * a Method storeVersioncode after click rating and redirect to Google play store.
     * @param context
     */
    private static void storeVersionCode(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG_NAME, ""+pref.getInt(EGGRATING_LASTSAVE_VERSION, 0));
        int versionCode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            PackageManager packMan = context.getPackageManager();
            try {
                PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
                versionCode = pkgInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor editor2 = pref.edit();
        editor2.putInt(EGGRATING_LASTSAVE_VERSION, versionCode);
        editor2.putBoolean(EGGRATING_STATE_VERSION, true);
        editor2.apply();
        Log.d(TAG_NAME,"version code" +versionCode);
    }

    /**
     * a Method checkVersionCode before display dialog if version is equal not show.
     * @param context
     */
    private static boolean getVersionCode(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int lastSaveVersion = pref.getInt(EGGRATING_LASTSAVE_VERSION, 0);
        int versionCode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            PackageManager packMan = context.getPackageManager();
            try {
                PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
                versionCode = pkgInfo.versionCode;
                Log.d(TAG_NAME,"version code" +versionCode);

                if(versionCode > lastSaveVersion) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static class Configuration {
        private String mTitleString;
        private String mMessageString;
        private String mYesString;
        private String mCancelString;
        private String mTitleThanksString;
        private String mMessageThanksString;
        private String mOkThanksString;
        private String mTitlePlayStoreString;
        private String mMessagePlayStoreString;
        private String mYesPlayStoreString;
        private String mCancelPlayStoreString;
        private int mTitleId = 0;
        private int mMessageId = 0;
        private int mYesButtonId = 0;
        private int mNoButtonId = 0;
        private int mCancelButton = 0;
        private int mTitleThanksId = 0;
        private int mTitleThanksDescId = 0;
        private int mTitleStoreId = 0;
        private int mDescStoreId = 0;
        private int mCriteriaInstallDays;
        private int mCriteriaLaunchTimes;
        private int mCriteriaLaunchTimesReTry;
        private int mColorHighlight = 0;
        private int mColorBorder = 0;
        private int mColorNormal = 0;
        private float mRating = 0;
        private boolean mTryAgain = true;

        Configuration(int criteriaLaunchTimes, int criteriaLaunchTimesReTry) {
            this.mCriteriaLaunchTimes = criteriaLaunchTimes;
            this.mCriteriaLaunchTimesReTry = criteriaLaunchTimesReTry;
        }

        public int getmCriteriaInstallDays() {
            return mCriteriaInstallDays;
        }

        public void setmCriteriaInstallDays(int mCriteriaInstallDays) {
            this.mCriteriaInstallDays = mCriteriaInstallDays;
        }

        /**
         * a Method getmCriteriaLaunchTimes get max days criteria first dialog.
         * @return  mCriteriaLaunchTimes
         */
        public int getmCriteriaLaunchTimes() {
            return mCriteriaLaunchTimes;
        }

        /**
         * a Method setCriterialLaunchTimes set max days before show first dialog.
         * @param mCriteriaLaunchTimes
         */
        public void setmCriteriaLaunchTimes(int mCriteriaLaunchTimes) {
            if(mCriteriaLaunchTimes == 0) {
                this.mCriteriaLaunchTimes = 10;
            } else {
                this.mCriteriaLaunchTimes = mCriteriaLaunchTimes;
            }
        }

        /**
         * a Method getmTitleId get title of first dialog.
         * @return  mTitleId
         */
        public int getmTitleId() {
            return mTitleId;
        }

        /**
         * a Method setmTitleId set title of first dialog.
         * @param mTitleId
         */
        public void setmTitleId(int mTitleId) {
            this.mTitleId = mTitleId;
        }

        /**
         * a Method getmMessageId get message of first dialog.
         * @return mMessageId
         */
        public int getmMessageId() {
            return mMessageId;
        }

        /**
         * a Method setmMessageId set message of first dialog.
         * @param mMessageId
         */
        public void setmMessageId(int mMessageId) {
            this.mMessageId = mMessageId;
        }

        /**
         * a Method getmYesButtonId get message of ok button of first dialog.
         * @return mMessageId
         */
        public int getmYesButtonId() {
            return mYesButtonId;
        }

        /**
         * a Method setmYesButtonId set message of ok button of first dialog.
         * @param mYesButtonId
         */
        public void setmYesButtonId(int mYesButtonId) {
            this.mYesButtonId = mYesButtonId;
        }

        /**
         * a Method getmNoButtonId set message of no button of first dialog.
         * @return mNoButtonId
         */
        public int getmNoButtonId() {
            return mNoButtonId;
        }

        /**
         * a Method setmNoButtonId set message of no button of first dialog.
         * @param mNoButtonId
         */
        public void setmNoButtonId(int mNoButtonId) {
            this.mNoButtonId = mNoButtonId;
        }

        /**
         * a Method getmCancelButton get message of cancel button of first dialog.
         * @return mCancelButton
         */
        public int getmCancelButton() {
            return mCancelButton;
        }

        /**
         * a Method setmCancelButton set message of cancel button of first dialog.
         * @param mCancelButton
         */
        public void setmCancelButton(int mCancelButton) {
            this.mCancelButton = mCancelButton;
        }

        /**
         * a Method getmTitleStoreId get title of store dialog.
         * @return mTitleStoreId
         */
        public int getmTitleStoreId() {
            return mTitleStoreId;
        }

        /**
         * a Method setmTitleStoreId set title of store dialog.
         * @param  mTitleStoreId
         */
        public void setmTitleStoreId(int mTitleStoreId) {
            this.mTitleStoreId = mTitleStoreId;
        }

        /**
         * a Method getmDescStoreId get message of store dialog.
         * @return mDescStoreId
         */
        public int getmDescStoreId() {
            return mDescStoreId;
        }

        /**
         * a Method setmDescStoreId set message of store dialog.
         * @param mDescStoreId
         */
        public void setmDescStoreId(int mDescStoreId) {
            this.mDescStoreId = mDescStoreId;
        }

        /**
         * a Method getmTitleThanksId get title of thanks dialog.
         * @return mTitleThanksId
         */
        public int getmTitleThanksId() {
            return mTitleThanksId;
        }

        /**
         * a Method setmTitleThanksId set title of thanks dialog.
         * @param mTitleThanksId
         */
        public void setmTitleThanksId(int mTitleThanksId) {
            this.mTitleThanksId = mTitleThanksId;
        }

        /**
         * a Method getmTitleThanksId get message of thanks dialog.
         * @return mTitleThanksDescId
         */
        public int getmTitleThanksDescId() {
            return mTitleThanksDescId;
        }

        /**
         * a Method setmTitleThanksDescId set message of thanks dialog.
         * @param mTitleThanksDescId
         */
        public void setmTitleThanksDescId(int mTitleThanksDescId) {
            this.mTitleThanksDescId = mTitleThanksDescId;
        }

        /**
         * a Method getmColorHighlight get highlight color of rating bar in first dialog.
         * @return mColorHighlight
         */
        public int getmColorHighlight() {
            return mColorHighlight;
        }

        /**
         * a Method setmColorHighlight set highlight color of rating bar in first dialog.
         * @param mColorHighlight
         */
        public void setmColorHighlight(int mColorHighlight) {
            this.mColorHighlight = mColorHighlight;
        }

        /**
         * a Method getmColorBorder get border color of rating bar in first dialog.
         * @return mColorBorder
         */
        public int getmColorBorder() {
            return mColorBorder;
        }

        /**
         * a Method setmColorBorder set border color of rating bar in first dialog.
         * @param mColorBorder
         */
        public void setmColorBorder(int mColorBorder) {
            this.mColorBorder = mColorBorder;
        }

        /**
         * a Method getmColorNormal get normal color of rating bar in first dialog.
         * @return mColorNormal
         */
        public int getmColorNormal() {
            return mColorNormal;
        }

        /**
         * a Method setmColorNormal set normal color of rating bar in first dialog.
         * @param mColorNormal
         */
        public void setmColorNormal(int mColorNormal) {
            this.mColorNormal = mColorNormal;
        }

        /**
         * a Method getmRating get rating limit to show store/thanks dialog.
         * @return mRating
         */
        public float getmRating() {
            return mRating;
        }

        /**
         * a Method setmRating set rating limit to show store/thanks dialog.
         * @param mRating
         */
        public void setmRating(float mRating) {
            this.mRating = mRating;
        }

        /**
         * a Method getmCriteriaLaunchTimesReTry get retry to show dialog again if user select cancel.
         * @return mCriteriaLaunchTimesReTry
         */
        public int getmCriteriaLaunchTimesReTry() {
            return mCriteriaLaunchTimesReTry;
        }

        /**
         * a Method getmCriteriaLaunchTimesReTry set retry to show dialog again if user select cancel.
         * @param mCriteriaLaunchTimesReTry
         */
        public void setmCriteriaLaunchTimesReTry(int mCriteriaLaunchTimesReTry) {
            this.mCriteriaLaunchTimesReTry = mCriteriaLaunchTimesReTry;
        }

        /**
         * a Method ismTryAgain get condition if user select cancel.
         * @return mTryAgain
         */
        public boolean ismTryAgain() {
            return mTryAgain;
        }

        /**
         * a Method setmTryAgain set condition if user select cancel.
         * @param mTryAgain
         */
        public void setmTryAgain(boolean mTryAgain) {
            this.mTryAgain = mTryAgain;
        }

        public String getmTitleString() {
            return mTitleString;
        }

        public void setmTitleString(String mTitleString) {
            this.mTitleString = mTitleString;
        }

        public String getmMessageString() {
            return mMessageString;
        }

        public void setmMessageString(String mMessageString) {
            this.mMessageString = mMessageString;
        }

        public String getmYesString() {
            return mYesString;
        }

        public void setmYesString(String mYesString) {
            this.mYesString = mYesString;
        }

        public String getmCancelString() {
            return mCancelString;
        }

        public void setmCancelString(String mCancelString) {
            this.mCancelString = mCancelString;
        }

        public String getmTitleThanksString() {
            return mTitleThanksString;
        }

        public void setmTitleThanksString(String mTitleThanksString) {
            this.mTitleThanksString = mTitleThanksString;
        }

        public String getmMessageThanksString() {
            return mMessageThanksString;
        }

        public void setmMessageThanksString(String mMessageThanksString) {
            this.mMessageThanksString = mMessageThanksString;
        }

        public String getmOkThanksString() {
            return mOkThanksString;
        }

        public void setmOkThanksString(String mOkThanksString) {
            this.mOkThanksString = mOkThanksString;
        }

        public String getmTitlePlayStoreString() {
            return mTitlePlayStoreString;
        }

        public void setmTitlePlayStoreString(String mTitlePlayStoreString) {
            this.mTitlePlayStoreString = mTitlePlayStoreString;
        }

        public String getmMessagePlayStoreString() {
            return mMessagePlayStoreString;
        }

        public void setmMessagePlayStoreString(String mMessagePlayStoreString) {
            this.mMessagePlayStoreString = mMessagePlayStoreString;
        }

        public String getmYesPlayStoreString() {
            return mYesPlayStoreString;
        }

        public void setmYesPlayStoreString(String mYesPlayStoreString) {
            this.mYesPlayStoreString = mYesPlayStoreString;
        }

        public String getmCancelPlayStoreString() {
            return mCancelPlayStoreString;
        }

        public void setmCancelPlayStoreString(String mCancelPlayStoreString) {
            this.mCancelPlayStoreString = mCancelPlayStoreString;
        }
    }

}
