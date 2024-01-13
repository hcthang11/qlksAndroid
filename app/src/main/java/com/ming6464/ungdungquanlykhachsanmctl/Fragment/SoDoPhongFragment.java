package com.ming6464.ungdungquanlykhachsanmctl.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemRoomAdapter;
import com.ming6464.ungdungquanlykhachsanmctl.ChucNangDatPhongActivity;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Rooms;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDAO;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDB;
import com.ming6464.ungdungquanlykhachsanmctl.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SoDoPhongFragment extends Fragment implements ItemRoomAdapter.IClickItemRooms, View.OnClickListener {
    public RecyclerView rcvRooms;
    private ItemRoomAdapter itemRoomAdapter;
    private List<Rooms> mListRooms,currentListRooms;
    private SimpleDateFormat sdf,sdf1;
    private Calendar calendar;
    private Date d_checkIn,d_checkOut;
    private ProgressBar pb_load;
    private ImageView img_currentCategory;
    private int currentCategory = 2;
    private boolean checkShowFilterCategory = false;
    private Animation animation,animationRotate;
    private long time = 0;
    private TextView tv_checkIn,tv_checkOut,tv_singleRoom,tv_doubleRoom,tv_vipRoom;
    public static final String KEY_BUNDLE = "KEY_BUNDLE",KEY_ROOM ="KEY_ROOM",
            KEY_CHECKIN = "KEY_CHECKIN",KEY_CHECKOUT ="KEY_CHECKOUT",
            KEY_STATUS = "KEY_STATUS",KEY_AMOUNT_DATE = "KEY_AMOUNT_DATE";
    private KhachSanDAO dao;

    public static SoDoPhongFragment newInstance() {
        return new SoDoPhongFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_so_do_phong, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animationRotate = AnimationUtils.loadAnimation(requireContext(),R.anim.scale_filter_room);
        calendar = Calendar.getInstance();
        d_checkOut = new Date();
        d_checkIn = new Date();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf1 = new SimpleDateFormat("dd/MM/yyyy HH");
        dao = KhachSanDB.getInstance(getContext()).getDAO();
        tv_checkIn = view.findViewById(R.id.fragSoDoPhong_tv_checkIn);
        tv_checkOut = view.findViewById(R.id.fragSoDoPhong_tv_checkOut);
        rcvRooms = view.findViewById(R.id.rcv_rooms);
        pb_load= view.findViewById(R.id.fragSoDoPhong_pg_load);
        img_currentCategory = view.findViewById(R.id.fragSoDoPhong_img_currentCategory);
        tv_singleRoom = view.findViewById(R.id.fragSoDoPhong_tv_singleRoom);
        tv_doubleRoom = view.findViewById(R.id.fragSoDoPhong_tv_doubleRoom);
        tv_vipRoom = view.findViewById(R.id.fragSoDoPhong_tv_vipRoom);
        currentListRooms = new ArrayList<>();
        itemRoomAdapter = new ItemRoomAdapter(this);
        mListRooms = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        rcvRooms.setLayoutManager(gridLayoutManager);
        rcvRooms.setAdapter(itemRoomAdapter);
        mListRooms = dao.getAllOfRooms();
        handleAction();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentCategory = 2;
        hideFilterCategory();
        try {
            time = sdf.parse(sdf.format(new Date(System.currentTimeMillis()))).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeInMillis(time);
        d_checkOut.setTime(time + 3600000 * 36);
        d_checkIn.setTime(time + 3600000 * 14);
        tv_checkIn.setText(sdf.format(d_checkIn));
        tv_checkOut.setText(sdf.format(d_checkOut));
        handleFilterRoom();
    }
    @Override
    public void datPhong(Rooms rooms) {
        int status = rooms.getStatus();
        if(status == 0){
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận đặt phòng")
                    .setMessage("Bạn có muốn đặt phòng không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        Intent intent = new Intent(requireContext(), ChucNangDatPhongActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_ROOM,rooms.getId());
                        bundle.putLong(KEY_CHECKIN,d_checkIn.getTime());
                        bundle.putLong(KEY_CHECKOUT,d_checkOut.getTime());
                        bundle.putInt(KEY_AMOUNT_DATE,(int)(d_checkOut.getTime() - d_checkIn.getTime())/(3600000*24) + 1);
                        int status1 = 0;
                        if(System.currentTimeMillis() < (d_checkIn.getTime()))
                            status1 = 2;
                        bundle.putInt(KEY_STATUS, status1);
                        intent.putExtra(KEY_BUNDLE,bundle);
                        startActivity(intent);
                    })
                    .setNegativeButton("Huỷ",null)
                    .show();
        }
    }
    private void beginLoad(){
        rcvRooms.setVisibility(View.GONE);
        pb_load.setVisibility(View.VISIBLE);
    }
    private void endLoad(){
        new Handler().postDelayed(() -> {
            rcvRooms.setVisibility(View.VISIBLE);
            pb_load.setVisibility(View.GONE);
        },600);
    }
    private void handleFilterRoom(){
        beginLoad();
        try {
            d_checkIn = sdf1.parse(tv_checkIn.getText().toString() + " 14");
            d_checkOut = sdf1.parse(tv_checkOut.getText().toString() + " 12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mListRooms = dao.getListRoomWithTime(d_checkIn,d_checkOut);
        handleFilterCategory();
    }

    private void handleFilterCategory() {
        beginLoad();
        currentListRooms.clear();
        for(Rooms x : mListRooms){
            if(x.getCategoryID() == currentCategory)
                currentListRooms.add(x);
        }
        checkShowFilterCategory = false;
        itemRoomAdapter.setData(currentListRooms, dao.getListNameCategoryWithRoomId(currentListRooms));
        updateColorFilterCategory();
        endLoad();
    }

    private String formatDate(int date){
        if(date < 10)
            return "0" + date;
        return String.valueOf(date);
    }

    private void handleAction() {
        tv_checkIn.setOnClickListener(this);
        tv_checkOut.setOnClickListener(this);
        img_currentCategory.setOnClickListener(this);
        tv_vipRoom.setOnClickListener(this);
        tv_singleRoom.setOnClickListener(this);
        tv_doubleRoom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragSoDoPhong_tv_checkIn:
                calendar.setTime(d_checkIn);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tv_checkIn.setText(formatDate(dayOfMonth) + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(time);
                datePickerDialog.show();
                break;
            case R.id.fragSoDoPhong_tv_checkOut:
                calendar.setTime(d_checkOut);
                datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    tv_checkOut.setText(formatDate(dayOfMonth) + "/" + (month + 1) + "/" + year);
                    handleFilterRoom();
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(time);
                datePickerDialog.show();
                break;
            case R.id.fragSoDoPhong_img_currentCategory:
                if(checkShowFilterCategory)
                    checkShowFilterCategory = false;
                else
                    checkShowFilterCategory = true;
                img_currentCategory.startAnimation(animationRotate);
                updateShowHideFilterCategory();
                break;
            case R.id.fragSoDoPhong_tv_singleRoom:
                if(currentCategory != 1){
                    currentCategory = 1;
                    handleFilterCategory();
                    updateShowHideFilterCategory();
                }
                break;
            case R.id.fragSoDoPhong_tv_doubleRoom:
                if(currentCategory != 2){
                    currentCategory = 2;
                    handleFilterCategory();
                    updateShowHideFilterCategory();
                }
                break;
            default:
                if(currentCategory != 3){
                    currentCategory = 3;
                    handleFilterCategory();
                    updateShowHideFilterCategory();
                }

        }
    }

    private void updateColorFilterCategory() {
        switch (currentCategory){
            case 1:
                img_currentCategory.setImageResource(R.drawable.single_user_30);
                tv_singleRoom.setBackgroundResource(R.color.hoadon_chuathanhtoan);
                tv_doubleRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                tv_vipRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                break;
            case 2:
                img_currentCategory.setImageResource(R.drawable.double_user_30);
                tv_singleRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                tv_doubleRoom.setBackgroundResource(R.color.hoadon_chuathanhtoan);
                tv_vipRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                break;
            default:
                img_currentCategory.setImageResource(R.drawable.vip_29);
                tv_singleRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                tv_doubleRoom.setBackgroundResource(R.color.hoadon_thanhtoan);
                tv_vipRoom.setBackgroundResource(R.color.hoadon_chuathanhtoan);
        }
    }

    private void updateShowHideFilterCategory(){
        if(checkShowFilterCategory)
            animation = AnimationUtils.loadAnimation(requireContext(),R.anim.show_category);
        else
            animation = AnimationUtils.loadAnimation(requireContext(),R.anim.hide_category);
        tv_singleRoom.startAnimation(animation);
        tv_doubleRoom.startAnimation(animation);
        tv_vipRoom.startAnimation(animation);
    }

    private void hideFilterCategory(){
        animation = AnimationUtils.loadAnimation(requireContext(),R.anim.hide_1_category);
        tv_singleRoom.startAnimation(animation);
        tv_doubleRoom.startAnimation(animation);
        tv_vipRoom.startAnimation(animation);
    }
}