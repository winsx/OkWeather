package net.gility.okweather.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.utils.AppUtils;

import javax.inject.Inject;

/**
 * @author Alimy
 */

public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private final String INTRODUCTION = "introduction";
    private final String CURRENT_VERSION = "current_version";
    private final String SHARE = "share";
    private final String STAR = "Star";
    private final String ENCOURAGE = "encourage";
    private final String BLOG = "blog";
    private final String GITHUB = "github";
    private final String EMAIL = "email";
    private final String CHECK = "check_version";

    private Preference mIntroduction;
    private Preference mVersion;
    private Preference mCheckVersion;
    private Preference mShare;
    private Preference mStar;
    private Preference mEncounrage;
    private Preference mBolg;
    private Preference mGithub;
    private Preference mEmail;

    @Inject ClipboardManager mClipboardManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);

        mIntroduction = findPreference(INTRODUCTION);
        mVersion = findPreference(CURRENT_VERSION);
        mCheckVersion = findPreference(CHECK);
        mShare = findPreference(SHARE);
        mStar = findPreference(STAR);
        mEncounrage = findPreference(ENCOURAGE);
        mBolg = findPreference(BLOG);
        mGithub = findPreference(GITHUB);
        mEmail = findPreference(EMAIL);

        mIntroduction.setOnPreferenceClickListener(this);
        mVersion.setOnPreferenceClickListener(this);
        mCheckVersion.setOnPreferenceClickListener(this);
        mShare.setOnPreferenceClickListener(this);
        mStar.setOnPreferenceClickListener(this);
        mEncounrage.setOnPreferenceClickListener(this);
        mBolg.setOnPreferenceClickListener(this);
        mGithub.setOnPreferenceClickListener(this);
        mEmail.setOnPreferenceClickListener(this);

        mVersion.setSummary(getActivity().getString(R.string.version_name) + AppUtils.getVersion(getActivity()));

        Injector.instance.inject(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mVersion == preference) {
            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.about_dialog_title))
                    .setMessage(getString(R.string.about_dialog_message))
                    .setPositiveButton(getString(R.string.about_dialog_positive), null)
                    .show();
        } else if (mShare == preference) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt));
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
        } else if (mStar == preference) {

            new AlertDialog.Builder(getActivity()).setTitle("点赞")
                    .setMessage("去项目地址给作者个Star，鼓励下作者")
                    .setNegativeButton("复制", (dialog, which) -> {
                        copyToClipboard(getView(), getActivity().getResources()
                                .getString(
                                        R.string.app_html));
                    })
                    .setPositiveButton("打开", (dialog, which) -> {
                        Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);           //指定Action
                        intent.setData(uri);                            //设置Uri
                        getActivity().startActivity(intent);        //启动Activity
                    })
                    .show();
        } else if (mIntroduction == preference) {
            Uri uri = Uri.parse(getString(R.string.readme));   //指定网址
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);           //指定Action
            intent.setData(uri);                            //设置Uri
            getActivity().startActivity(intent);        //启动Activity
            // goToWebFragment(getString(R.string.readme));
        } else if (mEncounrage == preference) {
            new AlertDialog.Builder(getActivity()).setTitle("请作者喝杯咖啡？")
                    .setMessage("非常感谢，有心就OK了！")
                    .setPositiveButton("确定", (dialog, which) -> {})
                    .show();
        } else if (mBolg == preference) {
            //copyToClipboard(getView(), mBolg.getSummary().toString());
            goToWebFragment(mBolg.getSummary().toString());
        } else if (mGithub == preference) {
            //copyToClipboard(getView(), mGithub.getSummary().toString());
            goToWebFragment(mGithub.getSummary().toString());
        } else if (mEmail == preference) {
            copyToClipboard(getView(), mEmail.getSummary().toString());
        } else if (mCheckVersion == preference) {
            Snackbar.make(getView(), "正在检查", Snackbar.LENGTH_SHORT).show();
        }
        return false;
    }

    //复制黏贴板
    private void copyToClipboard(View view, String info) {

        ClipData clipData = ClipData.newPlainText("msg", info);
        mClipboardManager.setPrimaryClip(clipData);
        Snackbar.make(view, "已经复制到剪切板", Snackbar.LENGTH_SHORT).show();
    }

    private void goToWebFragment(String url) {
        WebviewFragment webviewFragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        webviewFragment.setArguments(bundle);
        getActivity().getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.framelayout, webviewFragment, WebviewFragment.TAG)
                .commit();
    }
}
