package com.santhosh.codepath.nytimes.fragment;


import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.ARTS;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.FASHION;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.NEWS_DESK;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SHARED_PREFS_NAME;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SORT;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SPORTS;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.START_DATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.santhosh.codepath.nytimes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsPrefEditFragment extends DialogFragment {

    @BindView(R.id.start_date)
    EditText mStartDate;
    @BindView(R.id.sort_order)
    Spinner mSortOrder;
    @BindView(R.id.sports)
    CheckBox mSports;
    @BindView(R.id.fashion)
    CheckBox mFashion;
    @BindView(R.id.arts)
    CheckBox mArts;

    public NewsPrefEditFragment() {
    }

    public static NewsPrefEditFragment newInstance(String title) {
        NewsPrefEditFragment frag = new NewsPrefEditFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_pref, null);
        ButterKnife.bind(this, view);

        final DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener datePicker =
                (view1, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mStartDate.setText(df.format(calendar.getTime()));
                };

        mStartDate.setOnClickListener(v -> new DatePickerDialog(getContext(), datePicker, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);

        alertDialogBuilder.setView(view);

        String date = df.format(Calendar.getInstance().getTime());

        mSortOrder.setSelection(
                sharedPreferences.getString(SORT, "Newest").equals("Newest") ? 0 : 1);
        mStartDate.setText(sharedPreferences.getString(START_DATE, date));
        mSports.setChecked(sharedPreferences.getInt(SPORTS, 0) == 1);
        mArts.setChecked(sharedPreferences.getInt(ARTS, 0) == 1);
        mFashion.setChecked(sharedPreferences.getInt(FASHION, 0) == 1);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(START_DATE, mStartDate.getText().toString());
            editor.putString(SORT, mSortOrder.getSelectedItem().toString());

            HashSet<String> newsDesk = new HashSet<>();

            if (mArts.isChecked()) {
                newsDesk.add(ARTS);
                editor.putInt(ARTS, 1);
            } else {
                editor.putInt(ARTS, 0);
            }

            if (mFashion.isChecked()) {
                newsDesk.add(FASHION);
                editor.putInt(FASHION, 1);
            } else {
                editor.putInt(FASHION, 0);
            }

            if (mSports.isChecked()) {
                newsDesk.add(SPORTS);
                editor.putInt(SPORTS, 1);
            } else {
                editor.putInt(SPORTS, 0);
            }

            editor.putStringSet(NEWS_DESK, newsDesk);

            {
                editor.apply();
            }

            EditPrefDialogListener editPrefDialogListener =
                    (EditPrefDialogListener) getTargetFragment();
            editPrefDialogListener.onFinishEditDialog();
            dialog.dismiss();
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return alertDialogBuilder.create();
    }

    public interface EditPrefDialogListener {
        void onFinishEditDialog();
    }
}
