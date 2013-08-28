package com.iory.zhixun.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.iory.zhixun.R;
import com.iory.zhixun.provider.NewsServer;

public class RightPanelListFragment extends Fragment{

	private int mColorRes = -1;
    private Button mAddLabelBtn;
    private Button mEditLabelBtn;
    private NewsServer mNewsServer;
	
	public RightPanelListFragment() {
		this(R.color.white);
	}
	
	public RightPanelListFragment(int colorRes) {
		mColorRes = colorRes;
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_right_list, null);
        mNewsServer = new NewsServer(getActivity());
        mAddLabelBtn = (Button) view.findViewById(R.id.add_label);
        mEditLabelBtn = (Button) view.findViewById(R.id.edit_label);
        mAddLabelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
        return view;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
