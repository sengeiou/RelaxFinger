package com.hardwork.fg607.relaxfinger.view;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.adapter.MyPagerAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ShortcutAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ToolAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.model.ShortcutInfo;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;
import com.orm.SugarRecord;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppSettingFragment extends Fragment implements View.OnClickListener{

    @Bind(R.id.app1_layout) RelativeLayout mLayout1;
    @Bind(R.id.app2_layout) RelativeLayout mLayout2;
    @Bind(R.id.app3_layout) RelativeLayout mLayout3;
    @Bind(R.id.app4_layout) RelativeLayout mLayout4;
    @Bind(R.id.app5_layout) RelativeLayout mLayout5;
    @Bind(R.id.app1_name) TextView mAppTextView1;
    @Bind(R.id.app2_name) TextView mAppTextView2;
    @Bind(R.id.app3_name) TextView mAppTextView3;
    @Bind(R.id.app4_name) TextView mAppTextView4;
    @Bind(R.id.app5_name) TextView mAppTextView5;
    @Bind(R.id.icon_app1) ImageView mAppIcon1;
    @Bind(R.id.icon_app2) ImageView mAppIcon2;
    @Bind(R.id.icon_app3) ImageView mAppIcon3;
    @Bind(R.id.icon_app4) ImageView mAppIcon4;
    @Bind(R.id.icon_app5) ImageView mAppIcon5;
    private String mAppName;
    private TextView mCurrentTextView;
    private ImageView mCurrentIcon;
    private TrayAppPreferences mPreferences;
    private FunctionDialog mFuncDialog;
    private Activity mActivity;

    private HashMap<String,MenuDataSugar> menu1Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String,MenuDataSugar> menu2Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String,MenuDataSugar> menu3Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String,MenuDataSugar> menu4Map = new HashMap<String, MenuDataSugar>();
    private HashMap<String,MenuDataSugar> menu5Map = new HashMap<String, MenuDataSugar>();
    private ArrayList<String> mChoosedList = new ArrayList<>();

    static String mCurrentApp;
    static HashMap<String,MenuDataSugar> currentMenuMap = null;
    static ArrayList<AppInfo> appList = null;
    static ArrayList<ToolInfo> toolList = null;
    static ArrayList<ShortcutInfo> shortcutList = null;


    public AppSettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_app_setting, container, false);

        ButterKnife.bind(this,fragmentView);

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        initMenuData();

        intView();

        return fragmentView;
    }

    private void initMenuData() {

        List<MenuDataSugar>  menuDataList = MenuDataSugar.listAll(MenuDataSugar.class);

        for(MenuDataSugar menuData:menuDataList){

            switch (menuData.getWhichMenu()){

                case "1":
                    menu1Map.put(menuData.getAction(),menuData);
                    break;
                case "2":
                    menu2Map.put(menuData.getAction(),menuData);
                    break;
                case "3":
                    menu3Map.put(menuData.getAction(),menuData);
                    break;
                case "4":
                    menu4Map.put(menuData.getAction(),menuData);
                    break;
                case "5":
                    menu5Map.put(menuData.getAction(),menuData);
                    break;
                default:
                    break;
            }
        }

    }

    private void intView() {

        mLayout1.setOnClickListener(this);
        mLayout2.setOnClickListener(this);
        mLayout3.setOnClickListener(this);
        mLayout4.setOnClickListener(this);
        mLayout5.setOnClickListener(this);

        generateMenu(menu1Map,mAppTextView1,mAppIcon1);
        generateMenu(menu2Map,mAppTextView2,mAppIcon2);
        generateMenu(menu3Map,mAppTextView3,mAppIcon3);
        generateMenu(menu4Map,mAppTextView4,mAppIcon4);
        generateMenu(menu5Map,mAppTextView5,mAppIcon5);

    }

    private void generateMenu(HashMap<String,MenuDataSugar> map, TextView textView, ImageView imageView){

        if(map.size()==0){

            textView.setText("");

            imageView.setBackground(null);
            imageView.setImageDrawable(null);

        }else if(map.size()==1){

            MenuDataSugar dataSugar = map.get(map.keySet().iterator().next());

            textView.setText(dataSugar.getName());

            int type = dataSugar.getType();

            switch (type){

                case 0:
                    imageView.setBackground(null);
                    imageView.setImageDrawable(AppUtils.getAppIcon(dataSugar.getAction()));
                    break;
                case 1:
                    imageView.setBackgroundResource(R.drawable.path_blue_oval);
                    imageView.setImageDrawable(FloatingBallUtils.getSwitcherIcon(dataSugar.getName()));
                    break;
                case 2:
                    imageView.setBackground(null);
                    imageView.setImageDrawable(AppUtils.getShortcutIcon(dataSugar.getName()));
                    break;
                default:
                    break;
            }

        }else if(map.size()>1){

            textView.setText("快捷文件夹");

            ArrayList<Bitmap> list = new ArrayList<>();

            for(String key:map.keySet()) {

                MenuDataSugar dataSugar = map.get(key);

                int type = dataSugar.getType();

                Drawable drawable = null;

                switch (type){

                    case 0:
                        drawable = AppUtils.getAppIcon(dataSugar.getAction());
                        break;
                    case 1:
                        drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());
                        break;
                    case 2:
                        drawable = AppUtils.getShortcutIcon(dataSugar.getName());
                        break;
                    default:
                        break;
                }

                if(drawable!=null){

                    list.add(ImageUtils.drawable2Bitmap(drawable));
                }

                if(list.size()>=9){

                    break;
                }
            }

            Bitmap bi = FloatingBallUtils.createCombinationImage(list);

            imageView.setBackground(new ColorDrawable(this.getResources().getColor(R.color.folder)));
            imageView.setImageBitmap(bi);

        }
    }

    private void generateMenu() {


        generateMenu(currentMenuMap,mCurrentTextView,mCurrentIcon);

        MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where WHICH_MENU='" + mCurrentApp+"'");

        for(String key :currentMenuMap.keySet()){

            MenuDataSugar sugar = currentMenuMap.get(key);

            sugar.save();
        }

        sendMsg(Config.UPDATE_APP, "which", mCurrentApp);
    }

    private void initDialog(ArrayList<String> choosedMenuList) {

        mFuncDialog = FunctionDialog.newInstance(choosedMenuList);

        mFuncDialog.setOperateFinishListener(new OnOperateFinishListener() {

            @Override
            public void onOperateFinish() {

                generateMenu();

            }
        });

    }


    @Override
    public void onClick(final View v) {

        new AsyncTask<Void,Void,Integer>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                switch (v.getId()){
                    case R.id.app1_layout:
                        mAppName = mAppTextView1.getText().toString();
                        mCurrentTextView = mAppTextView1;
                        mCurrentIcon = mAppIcon1;
                        mCurrentApp = "1";
                        currentMenuMap = menu1Map;
                        break;
                    case R.id.app2_layout:
                        mAppName = mAppTextView2.getText().toString();
                        mCurrentTextView = mAppTextView2;
                        mCurrentIcon = mAppIcon2;
                        mCurrentApp = "2";
                        currentMenuMap = menu2Map;
                        break;
                    case R.id.app3_layout:
                        mAppName = mAppTextView3.getText().toString();
                        mCurrentTextView = mAppTextView3;
                        mCurrentIcon = mAppIcon3;
                        mCurrentApp = "3";
                        currentMenuMap = menu3Map;
                        break;
                    case R.id.app4_layout:
                        mAppName = mAppTextView4.getText().toString();
                        mCurrentTextView = mAppTextView4;
                        mCurrentIcon = mAppIcon4;
                        mCurrentApp = "4";
                        currentMenuMap = menu4Map;
                        break;
                    case R.id.app5_layout:
                        mAppName = mAppTextView5.getText().toString();
                        mCurrentTextView = mAppTextView5;
                        mCurrentIcon = mAppIcon5;
                        mCurrentApp = "5";
                        currentMenuMap = menu5Map;
                        break;
                    default:
                        break;
                }

            }

            @Override
            protected Integer doInBackground(Void... params) {

                Integer type = -1;

                mChoosedList.clear();

                for(String action:currentMenuMap.keySet()){

                    mChoosedList.add(action);
                }

                if(mChoosedList.size()==1){

                    MenuDataSugar menuDataSugar = currentMenuMap.get((currentMenuMap.keySet().iterator().next()));
                    type = menuDataSugar.getType();
                }

                return type;
            }

            @Override
            protected void onPostExecute(final Integer type) {

                super.onPostExecute(type);

                popupFunctionDialog(type,mChoosedList);

            }
        }.execute();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }


    public void popupFunctionDialog(final int type, final ArrayList<String> choosedMenuList) {


        if (mFuncDialog == null) {

            initDialog(choosedMenuList);
        }

        if (mFuncDialog.getDialog() != null) {

            mFuncDialog.getDialog().show();

            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mFuncDialog.setCheckedFuncName(type,choosedMenuList);
                }
            },30);

        } else {

            MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mFuncDialog.show(getActivity().getFragmentManager(), "dialogFragment");

                    mFuncDialog.setCheckedFuncName(type,choosedMenuList);
                }
            },60);
        }



    }

    public  void sendMsg(int what,String name,String msg) {
        Intent intent = new Intent();
        intent.putExtra("what", what);
        intent.putExtra(name, msg);
        intent.setClass(MyApplication.getApplication(), FloatingBallService.class);
        MyApplication.getApplication().startService(intent);

    }

    @Override
    public void onPause() {
        DialogFragment dialogFragment = (DialogFragment) getActivity().getFragmentManager().findFragmentByTag("dialogFragment");

        if(dialogFragment!=null){

            getActivity().getFragmentManager().beginTransaction().remove(dialogFragment).commit();
        }
        super.onPause();
    }


    public static class FunctionDialog extends DialogFragment{

        @Bind(R.id.viewPager)
        ViewPager mViewPager;
        @Bind(R.id.tabs)
        TabLayout mTabs;

        private View mAppView = null;
        private View mButtonView = null;
        private View mShortcutView = null;

        private  ArrayList<String> mMenuChoosedList;
        private  ArrayList<String> mOldChoosedList;
        private MyPagerAdapter mPagerAdapter;
        private  AppAdapter adapter;
        private  ToolAdapter mToolAdapter;
        private ShortcutAdapter mShortcutAdapter;
        private String mCheckdedFuncName;
        private OnOperateFinishListener mClickListener;
        private int mType=0;
        private Handler mHandler;

        private static FunctionDialog mInstance=null;

        static FunctionDialog newInstance(ArrayList<String> choosedMenuList) {

            if(mInstance==null){

                FunctionDialog f = new FunctionDialog();

                Bundle args = new Bundle();
                args.putStringArrayList("checkedName", choosedMenuList);
                f.setArguments(args);

                return f;

            }else {

                return mInstance;
            }


        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mMenuChoosedList = getArguments().getStringArrayList("checkedName");

            mHandler = new Handler();

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.function_dialog_layout, null);

            ButterKnife.bind(this, view);

            initAppView();
            initButtonView();
            initShotcutView();

            setupViewPager();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BottomDialog);

            builder.setView(view);

       /*     builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    getDialog().hide();

                    adapter.setAppChecked(mOldChoosedList);
                    mShortcutAdapter.setShortcutChecked(mOldChoosedList);
                    mToolAdapter.setToolChecked(mOldChoosedList);

                    mOldChoosedList = null;
                }
            });

            builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    getDialog().hide();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mClickListener.onDialogClick(null);
                        }
                    });
                }
            });*/

           Dialog  dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);

            dialog.setTitle("选择快捷功能");

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if(keyCode==KeyEvent.KEYCODE_BACK){

                        getDialog().hide();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                mClickListener.onOperateFinish();
                            }
                        });

                        return true;
                    }

                    return false;
                }
            });

            // 设置宽度为屏宽、靠近屏幕底部。
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);

            return dialog;
        }


        private void setOperateFinishListener(OnOperateFinishListener listener){

            mClickListener = listener;
        }

        private void setCheckedFuncName(int type, ArrayList<String> choosedMenuList) {

            mType = type;
            mMenuChoosedList = choosedMenuList;

         /*   new Thread(new Runnable() {
                @Override
                public void run() {

                    mOldChoosedList = (ArrayList<String>) mMenuChoosedList.clone();
                }
            }).start();


*/

            if (mType == -1) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }

            } else if (mType == 0) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(0);
                }


            } else if (mType == 1) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(1);
                }

            } else if (mType == 2) {

                if (mViewPager != null) {
                    mViewPager.setCurrentItem(2);
                }
            }

            if (adapter != null) {

                adapter.setAppChecked(mMenuChoosedList);
                adapter.notifyDataSetChanged();
            }

            if (mToolAdapter != null) {

                mToolAdapter.setToolChecked(mMenuChoosedList);
                mToolAdapter.notifyDataSetChanged();
            }

            if (mShortcutAdapter != null) {

                mShortcutAdapter.setShortcutChecked(mMenuChoosedList);
                mShortcutAdapter.notifyDataSetChanged();
            }


        }



        private void initShotcutView() {

            if(mShortcutView==null){

                mShortcutView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }
            final ListView listView = (ListView) mShortcutView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mShortcutView.findViewById(R.id.loading);

            mShortcutAdapter= new ShortcutAdapter(getActivity());
            mShortcutAdapter.setShortcutChecked(mMenuChoosedList);

            if(shortcutList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            shortcutList = AppUtils.getShortcuts();

                        }catch (SecurityException e){

                            e.printStackTrace();


                        }catch (SQLiteException e){

                            e.printStackTrace();

                            shortcutList = new ArrayList<ShortcutInfo>();
                        }


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                mShortcutAdapter.addList(shortcutList);

                                listView.setAdapter(mShortcutAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        String action = shortcutList.get(i).getShortcutIntent();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,2,action);

                                            currentMenuMap.put(action,data);
                                            mMenuChoosedList.add(action);

                                        }else {

                                            currentMenuMap.remove(action);
                                            mMenuChoosedList.remove(action);
                                        }

                                        mShortcutAdapter.setShortcutChecked(mMenuChoosedList);

                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });



                    }
                }).start();


            }else {

                mShortcutAdapter.addList(shortcutList);

                listView.setAdapter(mShortcutAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        checkBox.setChecked(!checkBox.isChecked());

                        TextView textView = (TextView) view.findViewById(R.id.text);

                        String name = textView.getText().toString();

                        String action = shortcutList.get(i).getShortcutIntent();

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,2,action);

                            currentMenuMap.put(action,data);
                            mMenuChoosedList.add(action);
                        }else {

                            currentMenuMap.remove(action);
                            mMenuChoosedList.remove(action);
                        }

                        mShortcutAdapter.setShortcutChecked(mMenuChoosedList);


                    }
                });

                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }



        }

        private void initButtonView() {

            if(mButtonView==null){

                mButtonView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }

            final ListView listView = (ListView) mButtonView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mButtonView.findViewById(R.id.loading);

            mToolAdapter= new ToolAdapter(getActivity());
            mToolAdapter.setToolChecked(mMenuChoosedList);

            if(toolList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        toolList = FloatingBallUtils.getToolInfos();


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                mToolAdapter.addList(toolList);

                                listView.setAdapter(mToolAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,1,name);

                                            currentMenuMap.put(name,data);
                                            mMenuChoosedList.add(name);
                                        }else {

                                            currentMenuMap.remove(name);
                                            mMenuChoosedList.remove(name);
                                        }

                                        mToolAdapter.setToolChecked(mMenuChoosedList);


                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });



                    }
                }).start();

            }else {

                mToolAdapter.addList(toolList);

                listView.setAdapter(mToolAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        checkBox.setChecked(!checkBox.isChecked());

                        TextView textView = (TextView) view.findViewById(R.id.text);

                        String name = textView.getText().toString();

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,1,name);

                            currentMenuMap.put(name,data);
                            mMenuChoosedList.add(name);
                        }else {

                            currentMenuMap.remove(name);
                            mMenuChoosedList.remove(name);
                        }
                        mToolAdapter.setToolChecked(mMenuChoosedList);


                    }
                });


                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }




        }

        private void checkPermissionGranted(String permission) {


            if(Build.VERSION.SDK_INT>22){

                int grant = getActivity().checkSelfPermission(permission);

                if (grant != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    requestPermissions(new String[]{permission}, 123);
                }
            }

        }


        private void initAppView() {

            if(mAppView==null){

                mAppView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            }

            final ListView listView = (ListView) mAppView.findViewById(R.id.lv_app);

            final ProgressBar loading = (ProgressBar) mAppView.findViewById(R.id.loading);



            adapter= new AppAdapter(getActivity());
            adapter.setAppChecked(mMenuChoosedList);

            if(appList==null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        appList = AppUtils.getLauncherAppInfos();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                adapter.addList(appList);

                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                                        checkBox.setChecked(!checkBox.isChecked());

                                        TextView textView = (TextView) view.findViewById(R.id.text);

                                        String name = textView.getText().toString();

                                        String action = appList.get(i).getAppPackage();

                                        if(checkBox.isChecked()){

                                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                                    name,0,action);

                                            currentMenuMap.put(action,data);

                                            mMenuChoosedList.add(action);

                                        }else {

                                            currentMenuMap.remove(action);

                                            mMenuChoosedList.remove(action);
                                        }

                                        adapter.setAppChecked(mMenuChoosedList);

                                    }
                                });

                                loading.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });


                    }
                }).start();



            }else {

                adapter.addList(appList);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        checkBox.setChecked(!checkBox.isChecked());

                        TextView textView = (TextView) view.findViewById(R.id.text);

                        String name = textView.getText().toString();

                        String action = appList.get(i).getAppPackage();

                        if(checkBox.isChecked()){

                            MenuDataSugar data = new MenuDataSugar(mCurrentApp,
                                    name,0,action);

                            currentMenuMap.put(action,data);
                            mMenuChoosedList.add(action);

                        }else {

                            currentMenuMap.remove(action);
                            mMenuChoosedList.remove(action);
                        }

                        adapter.setAppChecked(mMenuChoosedList);



                    }
                });

                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

        }


        public void setupViewPager(){

            mPagerAdapter = new MyPagerAdapter();

            if(mAppView!=null){

                mPagerAdapter.addView(mAppView,"应用程序");
                mPagerAdapter.addView(mButtonView,"快捷开关");
                mPagerAdapter.addView(mShortcutView,"快捷方式");
            }


            mViewPager.setAdapter(mPagerAdapter);

            mTabs.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(3);

            switch (mType){

                case 0:
                    mViewPager.setCurrentItem(0);
                    break;
                case 1:
                    mViewPager.setCurrentItem(1);
                    break;
                case 2:
                    mViewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    switch (position) {
                        case 0:
                            // mFloatingActionButton.hide();
                            break;
                        case 1:
                            // mFloatingActionButton.hide();
                            //mHistoryFragment.refreshHistory();
                            break;
                        case 2:
                            //mFloatingActionButton.show();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }


    }

    public interface OnOperateFinishListener{

        public void onOperateFinish();

    }


}
