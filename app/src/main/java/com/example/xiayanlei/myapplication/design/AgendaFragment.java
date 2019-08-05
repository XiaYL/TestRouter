package com.example.xiayanlei.myapplication.design;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.xiayanlei.myapplication.R;

/**
 * Author       : yanbo
 * Date         : 2015-06-01
 * Time         : 15:09
 * Description  :
 */
public class AgendaFragment extends Fragment {
    private View mParentView;

    private TextInputLayout mTextInputLayout;
    private EditText mEditText;

    private FloatingActionButton mFloatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.agenda_fragment, container, false);
        return mParentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        demoTextInputLayout();

        demomFloatingActionButton();
    }

    private void demoTextInputLayout() {
        mTextInputLayout = (TextInputLayout) mParentView.findViewById(R.id.text_input_layout);
        mEditText = mTextInputLayout.getEditText();
        mTextInputLayout.setHint("请输入四位学号");
        mTextInputLayout.setErrorEnabled(true);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("abc","text length is " + s.length());
                if (s.length() > 4) {
                    mTextInputLayout.setError("学号输入错误");
                } else {
                    mTextInputLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void demomFloatingActionButton() {
        mFloatingActionButton = (FloatingActionButton) mParentView.findViewById(R.id.action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "结束当前Acitivty", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });
    }
}
