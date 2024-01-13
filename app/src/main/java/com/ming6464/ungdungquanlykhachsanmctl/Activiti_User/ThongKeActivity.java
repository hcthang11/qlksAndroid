package com.ming6464.ungdungquanlykhachsanmctl.Activiti_User;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemCategoryThongKeAdapter;
import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemServiceThongKeAdapter;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Categories;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.OrderDetail;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.ServiceOrder;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Services;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDAO;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDB;
import com.ming6464.ungdungquanlykhachsanmctl.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {
    private List<Integer> listIdCategory,listIdService;
    private int[] arrSoLieuCategory,arrSoLieuService;
    private List<String> listNameCategory,listNameService;
    private ItemCategoryThongKeAdapter adapterCategory;
    private ItemServiceThongKeAdapter adapterService;
    private KhachSanDAO dao;
    private RecyclerView rc_category,rc_service;
    private TextView tv_checkIn,tv_checkOut,tv_roomSuccess,tv_roomCancel,tv_roomTotal,tv_serviceTotal,tv_total;
    private SimpleDateFormat sdf,sdf1;
    private Date startDate,endDate;
    private NumberFormat format;
    private DatePickerDialog datePicker;
    private Calendar calendar;
    private Toolbar tb;
    private ProgressBar pg;
    private LinearLayoutCompat linear_thongKe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        //
        format = NumberFormat.getInstance(new Locale("en","EN"));
        dao = KhachSanDB.getInstance(this).getDAO();
        anhXa();
        handleList();
        handleToolbar();
        handleRecycler();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf1 = new SimpleDateFormat("dd/MM/yyyy HH");
    }

    private void handleList() {
        listNameCategory = new ArrayList<>();
        listIdCategory = new ArrayList<>();
        listNameService = new ArrayList<>();
        listIdService = new ArrayList<>();
        int size = 0;
        for(Categories x : dao.getAllOfLoaiPhong()){
            listIdCategory.add(x.getId());
            listNameCategory.add(x.getName());
            size ++;
        }
        arrSoLieuCategory = new int[size * 3];
        //
        size = 0;
        for(Services x : dao.getAllService()){
            listIdService.add(x.getId());
            listNameService.add(x.getName());
            size ++;
        }
        arrSoLieuService = new int[size * 2];
    }

    private void handleRecycler() {
        adapterCategory = new ItemCategoryThongKeAdapter();
        adapterService = new ItemServiceThongKeAdapter();
        rc_category.setAdapter(adapterCategory);
        rc_category.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rc_category.setHasFixedSize(true);
        rc_service.setAdapter(adapterService);
        rc_service.setLayoutManager(new LinearLayoutManager(this));
    }

    private void handleToolbar() {
        tb.setTitle("Thống Kê");
        tb.setTitleTextColor(Color.BLACK);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long time = System.currentTimeMillis();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        startDate = new Date(time - 24*3600000);
        endDate = new Date(time);
        tv_checkOut.setText(sdf.format(endDate));
        tv_checkIn.setText(sdf.format(startDate));
        filterDuLieu();
    }

    private void anhXa() {
        tv_checkIn = findViewById(R.id.actiThongKe_tv_checkIn);
        tv_checkOut = findViewById(R.id.actiThongKe_tv_checkOut);
        tv_roomSuccess = findViewById(R.id.actiThongKe_tv_roomSuccess);
        tv_roomCancel = findViewById(R.id.actiThongKe_tv_roomCancel);
        tv_roomTotal = findViewById(R.id.actiThongKe_tv_roomTotal);
        tv_total = findViewById(R.id.actiThongKe_tv_total);
        tv_serviceTotal = findViewById(R.id.actiThongKe_tv_serviceTotal);
        rc_category = findViewById(R.id.actiThongKe_rc_category);
        rc_service = findViewById(R.id.actiThongKe_rc_service);
        tb = findViewById(R.id.actiThongKe_tb);
        linear_thongKe = findViewById(R.id.actiThongKe_linear_thongKe);
        pg = findViewById(R.id.actiThongKe_pg);
    }

    public void handleActionTvCheckIn(View view) {
        calendar.setTimeInMillis(startDate.getTime());
        datePicker = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> tv_checkIn.setText(formatDate(dayOfMonth) + "/" + (month + 1) + "/" + year),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public void handleActionTvCheckOut(View view) {
        calendar.setTimeInMillis(endDate.getTime());
        datePicker = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            tv_checkOut.setText(formatDate(dayOfMonth) + "/" + (month + 1) + "/" + year);
            filterDuLieu();
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void filterDuLieu() {
        showEffectLoad();
        try {
            startDate = sdf1.parse(tv_checkIn.getText().toString() + " 01");
            endDate = sdf1.parse(tv_checkOut.getText().toString() + " 23");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int roomSuccess = 0;
        int roomCancel = 0;
        int serviceTotal = 0;
        Arrays.fill(arrSoLieuCategory,0);
        Arrays.fill(arrSoLieuService,0);
        int index = 0,soLuongCategory = 0,soLuongService = 0,sl = 0,sl2 = 0,height = 0;
        for(OrderDetail x : dao.getListOrderDetailWhenEndDateBetweenTime(startDate,endDate)){
            index = listIdCategory.indexOf(dao.getObjOfRooms(x.getRoomID()).getCategoryID());
            int sum = dao.getTotalPriceOrderDetail(x.getId());
            arrSoLieuCategory[index * 3] += sum;
            if(x.getStatus() != 4){
                arrSoLieuCategory[index * 3 + 1] += 1;
                roomSuccess += sum;
            }
            else{
                arrSoLieuCategory[index * 3 + 2] += 1;
                roomCancel += sum;
            }

            sl = arrSoLieuCategory[index * 3 + 1] + arrSoLieuCategory[index * 3 + 2];
            if(soLuongCategory < sl){
                soLuongCategory = sl;
            }
            for(ServiceOrder y : dao.getListWithOrderDetailIdOfServiceOrder(x.getId())){
                index = listIdService.indexOf(y.getServiceId());
                sum = y.getAmount() * dao.getObjOfServices(y.getServiceId()).getPrice();
                arrSoLieuService[index * 2] += sum;
                arrSoLieuService[index * 2 + 1] += y.getAmount();
                sl2 = arrSoLieuService[index * 2 + 1];
                if(sl2 > soLuongService)
                    soLuongService = sl2;
                serviceTotal += sum;
            }

        }
        if(soLuongCategory > 0)
            height = 500/soLuongCategory;
        adapterCategory.setData(listNameCategory,arrSoLieuCategory,height);
        if(soLuongService > 0)
            height = 700/soLuongService;
        adapterService.setData(listNameService,arrSoLieuService,height);
        //
        tv_roomSuccess.setText(format.format(roomSuccess) + "K");
        tv_roomCancel.setText(format.format(roomCancel) + "K");
        tv_roomTotal.setText(format.format(roomCancel + roomSuccess) + "K");
        tv_serviceTotal.setText(format.format(serviceTotal) + "K");
        tv_total.setText(format.format(serviceTotal + roomCancel + roomSuccess) + "K");
    }

    private String formatDate(int date){
        if(date < 10)
            return "0" + date;
        return String.valueOf(date);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showEffectLoad(){
        tb.setEnabled(false);
        linear_thongKe.setVisibility(View.GONE);
        pg.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            tb.setEnabled(true);
            linear_thongKe.setVisibility(View.VISIBLE);
            pg.setVisibility(View.GONE);
        },600);
    }
}