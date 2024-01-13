package com.ming6464.ungdungquanlykhachsanmctl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Categories;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Rooms;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.ServiceCategory;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Services;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.DichVuFragment;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.TaiKhoanFragment;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.HoaDonFragment;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.KhachHangFragment;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.PhongFragment;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private KhachSanDAO dao;
    private TextView tv_titleTb;
    private KhachSanSharedPreferences share;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        bottomNavigationView.setItemIconTintList(null);
        goiPhonFragment();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.home_25);
        addData();
        Click();
    }

    private void addData() {
        if (!share.getCheck2()) {
            share.setCheck2(true);
            dao.insertOfLoaiPhong(new Categories("Phòng Đơn",800,1));
            dao.insertOfLoaiPhong(new Categories("Phòng Đôi",1200,2));
            dao.insertOfLoaiPhong(new Categories("Phòng Vip",3200,4));
            //
            dao.insertOfService(new Services("Giặt là", 30));
            dao.insertOfService(new Services("Ủi đồ", 30));
            dao.insertOfService(new Services("Giặt khô", 30));
            dao.insertOfService(new Services("Fitness center / Gym", 30));
            dao.insertOfService(new Services("Dọn phòng hằng ngày", 40));
            dao.insertOfService(new Services("Ăn Sáng", 40));
            dao.insertOfService(new Services("Spa", 100));
            dao.insertOfService(new Services("Đưa đón sân bay", 250));
            dao.insertOfService(new Services("Karaoke", 130));
            dao.insertOfService(new Services("Chăm sóc thú cưng", 100));
            dao.insertOfService(new Services("Thuê xe tự lái", 900));
            //rooms
            dao.insertOfRooms(new Rooms("101", 1));
            dao.insertOfRooms(new Rooms("102", 1));
            dao.insertOfRooms(new Rooms("103", 1));
            dao.insertOfRooms(new Rooms("104", 2));
            dao.insertOfRooms(new Rooms("105", 2));
            dao.insertOfRooms(new Rooms("106", 3));
            dao.insertOfRooms(new Rooms("201", 1));
            dao.insertOfRooms(new Rooms("202", 1));
            dao.insertOfRooms(new Rooms("203", 1));
            dao.insertOfRooms(new Rooms("204", 2));
            dao.insertOfRooms(new Rooms("205", 2));
            dao.insertOfRooms(new Rooms("206", 3));
            dao.insertOfRooms(new Rooms("301", 1));
            dao.insertOfRooms(new Rooms("302", 1));
            dao.insertOfRooms(new Rooms("303", 1));
            dao.insertOfRooms(new Rooms("304", 2));
            dao.insertOfRooms(new Rooms("305", 2));
            dao.insertOfRooms(new Rooms("306", 3));
            dao.insertOfRooms(new Rooms("401", 1));
            dao.insertOfRooms(new Rooms("402", 1));
            dao.insertOfRooms(new Rooms("403", 1));
            dao.insertOfRooms(new Rooms("404", 2));
            dao.insertOfRooms(new Rooms("405", 2));
            dao.insertOfRooms(new Rooms("406", 3));
            dao.insertOfRooms(new Rooms("501", 1));
            dao.insertOfRooms(new Rooms("502", 1));
            dao.insertOfRooms(new Rooms("503", 1));
            dao.insertOfRooms(new Rooms("504", 2));
            dao.insertOfRooms(new Rooms("505", 2));
            dao.insertOfRooms(new Rooms("506", 3));

            //
            dao.insertOfServiceCategory(new ServiceCategory(1, 1));
            dao.insertOfServiceCategory(new ServiceCategory(1, 2));
            dao.insertOfServiceCategory(new ServiceCategory(1, 3));
            dao.insertOfServiceCategory(new ServiceCategory(1, 4));
            dao.insertOfServiceCategory(new ServiceCategory(1, 6));

            dao.insertOfServiceCategory(new ServiceCategory(2, 1));
            dao.insertOfServiceCategory(new ServiceCategory(2, 2));
            dao.insertOfServiceCategory(new ServiceCategory(2, 3));
            dao.insertOfServiceCategory(new ServiceCategory(2, 4));
            dao.insertOfServiceCategory(new ServiceCategory(2, 5));
            dao.insertOfServiceCategory(new ServiceCategory(2, 6));

            dao.insertOfServiceCategory(new ServiceCategory(3, 1));
            dao.insertOfServiceCategory(new ServiceCategory(3, 2));
            dao.insertOfServiceCategory(new ServiceCategory(3, 3));
            dao.insertOfServiceCategory(new ServiceCategory(3, 4));
            dao.insertOfServiceCategory(new ServiceCategory(3, 5));
            dao.insertOfServiceCategory(new ServiceCategory(3, 6));
            dao.insertOfServiceCategory(new ServiceCategory(3, 7));
            dao.insertOfServiceCategory(new ServiceCategory(3, 8));
            dao.insertOfServiceCategory(new ServiceCategory(3, 9));
            dao.insertOfServiceCategory(new ServiceCategory(3, 10));
            dao.insertOfServiceCategory(new ServiceCategory(3, 11));
        }
    }

    //sk gọi frm
    private void goiPhonFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PhongFragment()).commit();
    }

    //sk click
    private void Click() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String title = null;
            int logo = 0;
            switch (item.getItemId()) {
                case R.id.menu_bottom1:
                    title = "Sơ Đồ Phòng";
                    logo = R.drawable.home_25;
                    fragment = new PhongFragment();
                    break;
                case R.id.menu_bottom2:
                    title = "Khách Hàng";
                    logo = R.drawable.customer_25;
                    fragment = new KhachHangFragment();
                    break;
                case R.id.menu_bottom3:
                    title = "Dịch Vụ";
                    logo = R.drawable.services_24;
                    fragment = new DichVuFragment();
                    break;
                case R.id.menu_bottom4:
                    title = "Hóa Đơn";
                    logo = R.drawable.order_25;
                    fragment = new HoaDonFragment();
                    break;
                case R.id.menu_bottom5:
                    title = "Tài Khoản";
                    logo = R.drawable.yourselt_25;
                    fragment = new TaiKhoanFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            tv_titleTb.setText(title);
            toolbar.setLogo(logo);
            return true;
        });
    }

    private void anhXa() {
        bottomNavigationView = findViewById(R.id.bottomNavMenu);
        toolbar = findViewById(R.id.actiMain_tb);
        dao = KhachSanDB.getInstance(this).getDAO();
        share = new KhachSanSharedPreferences(this);
        tv_titleTb = findViewById(R.id.actiMain_tv_titleTb);
    }
}