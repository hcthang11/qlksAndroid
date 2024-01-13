package com.ming6464.ungdungquanlykhachsanmctl.Activiti_User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemNhanVienAdapter;
import com.ming6464.ungdungquanlykhachsanmctl.CustomToast;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.People;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDAO;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDB;
import com.ming6464.ungdungquanlykhachsanmctl.R;
import java.util.List;

public class QuanLyActivity extends AppCompatActivity implements ItemNhanVienAdapter.EventOfItemNhanVienAdapter {
    private RecyclerView recyclerView;
    private KhachSanDAO dao;
    private List<People> list;
    private People people;
    private int status;
    private SearchView search;
    private ItemNhanVienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly);
        //
        Toolbar toolbar = findViewById(R.id.actiQuanLy_tb);
        toolbar.setTitle("Quản Lý Nhân Viên");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //
        dao = KhachSanDB.getInstance(QuanLyActivity.this).getDAO();
        recyclerView = findViewById(R.id.actiQuanLy_rc_nhanVien);
        search = findViewById(R.id.actiQuanLy_search);
        handleRecycler();
        handleSearch();
        findViewById(R.id.actiQuanLy_btn_addNhanVien).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyActivity.this);
            View view = LayoutInflater.from(QuanLyActivity.this).inflate(R.layout.dialog_them_nhanvien, null);
            EditText ed_name = view.findViewById(R.id.dialogThemNhanVien_ed_name);
            EditText ed_sdt = view.findViewById(R.id.dialogThemNhanVien_ed_sdt);
            EditText ed_cccd = view.findViewById(R.id.dialogThemNhanVien_ed_cccd);
            EditText ed_pass = view.findViewById(R.id.dialogThemNhanVien_ed_pass);
            EditText ed_address = view.findViewById(R.id.dialogThemNhanVien_ed_address);
            RadioButton rdo_feMale = view.findViewById(R.id.dialogThemNhanVien_rdo_feMale);
            Button btn_add = view.findViewById(R.id.dialogThemNhanVien_btn_add);
            Button btn_cancel = view.findViewById(R.id.dialogThemNhanVien_btn_cancel);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            btn_add.setOnClickListener(v1 -> {
                //
                String name = ed_name.getText().toString(),sdt = ed_sdt.getText().toString(),
                        cccd = ed_cccd.getText().toString(),pass = ed_pass.getText().toString(),address = ed_address.getText().toString();
                if (name.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || pass.isEmpty() || address.isEmpty()) {
                    CustomToast.makeText(QuanLyActivity.this, "Thông tin khách hàng không để trống", false);
                    return;
                }
                if(dao.checkLogin(sdt) != null){
                    CustomToast.makeText(QuanLyActivity.this,"Nhân Viên Đã Tồn Tại !",false).show();
                    return;
                }
                if(!name.matches("^[a-zA-Z][a-zA-Z ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$")){
                    CustomToast.makeText(QuanLyActivity.this, "Tên không phù hợp", false).show();
                    return;
                }
                if (!sdt.matches("^0\\d{9}")) {
                    CustomToast.makeText(QuanLyActivity.this, "Số điện thoại không đúng !", false).show();
                    return;
                }
                if (cccd.length() < 12) {
                    CustomToast.makeText(QuanLyActivity.this, "CCCD/CMND Không chính xác !", false).show();
                    return;
                }
                if(dao.getObjOfUser(sdt) != null){
                    CustomToast.makeText(QuanLyActivity.this, "Số điện thoại đã tồn tại !", false).show();
                    return;
                }
                if (dao.getObjWithCCCDOfUser(cccd) != null) {
                    CustomToast.makeText(QuanLyActivity.this, "CCCD/CMND đã tồn tại !", false).show();
                    return;
                }
                int sex = 1;
                if (rdo_feMale.isChecked())
                    sex = 0;
                People people = new People(name,sdt,cccd,address,sex,1);
                people.setPassowrd(pass);
                dao.insertOfUser(people);
                list.add(people);
                CustomToast.makeText(QuanLyActivity.this, "Thêm Thành Công", true).show();
                int index = list.size() - 1;
                if(list.size() == 0)
                    index = 0;
                adapter.notifyItemInserted(index);
                dialog.dismiss();
            });
            btn_cancel.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.dialog_slide_left_to_right;
            dialog.show();
        });
    }

    private void handleSearch() {
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list = dao.searchNhanVienWithPhoneNumber("%" + newText + "%");
                adapter.setData(list);
                return false;
            }
        });
    }

    private void handleRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(QuanLyActivity.this));
        list = dao.getListWithStatusOfUser(1);
        list.addAll(dao.getListWithStatusOfUser(4));
        list.addAll(dao.getListWithStatusOfUser(5));
        adapter = new ItemNhanVienAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setData(list);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(int position) {
        people = list.get(position);
        status = people.getStatus();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_nhanvien, null);
        view.findViewById(R.id.dialogThemNhanVien_linear_status).setVisibility(View.VISIBLE);
        EditText ed_name = view.findViewById(R.id.dialogThemNhanVien_ed_name);
        EditText ed_sdt = view.findViewById(R.id.dialogThemNhanVien_ed_sdt);
        EditText ed_cccd = view.findViewById(R.id.dialogThemNhanVien_ed_cccd);
        EditText ed_pass = view.findViewById(R.id.dialogThemNhanVien_ed_pass);
        EditText ed_address = view.findViewById(R.id.dialogThemNhanVien_ed_address);
        RadioButton rdo_feMale = view.findViewById(R.id.dialogThemNhanVien_rdo_feMale),
                rdo_transferShift = view.findViewById(R.id.dialogThemNhanVien_rdo_transferShift),
                rdo_inActive = view.findViewById(R.id.dialogThemNhanVien_rdo_inActive);
        Button btn_update = view.findViewById(R.id.dialogThemNhanVien_btn_add);
        Button btn_cancel = view.findViewById(R.id.dialogThemNhanVien_btn_cancel);
        builder.setView(view);
        Dialog dialog = builder.create();
        //fill table
        ed_name.setText(people.getFullName());
        ed_sdt.setText(people.getSDT());
        ed_cccd.setText(people.getCCCD());
        ed_pass.setText(people.getPassowrd());
        ed_address.setText(people.getAddress());
        if(status == 4)
            rdo_transferShift.setChecked(true);
        else if(status == 5)
            rdo_inActive.setChecked(true);
        TextView tv_title = view.findViewById(R.id.dialogThemNhanVien_tv_title);
        tv_title.setText("Cập nhật Nhân Viên");
        btn_update.setText("Cập nhật");
        if (people.getSex() == 0)
            rdo_feMale.setChecked(true);
        btn_update.setOnClickListener(v1 -> {
            String name = ed_name.getText().toString(),
                    sdt = ed_sdt.getText().toString(),
                    cccd = ed_cccd.getText().toString(),
                    address = ed_address.getText().toString(),
                    pass = ed_pass.getText().toString();
            if(name.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || address.isEmpty() || pass.isEmpty()){
                CustomToast.makeText(this, "Thông tin không được bỏ trống !", false).show();
                return;
            }
            if(!name.matches("^[a-zA-Z ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$")){
                CustomToast.makeText(this, "Tên không phù hợp", false).show();
                return;
            }
            if(!sdt.matches("^0\\d{9}$")){
                CustomToast.makeText(this, "Số điện thoại không đúng !", false).show();
                return;
            }else if(dao.getAmountWithPhoneNumberPeople(sdt) > 1) {
                CustomToast.makeText(this, "Số điện thoại đã tồn tại !", false).show();
                return;
            }
            if(cccd.length() != 12){
                CustomToast.makeText(this, "CCCD/CMND Không chính xác !", false).show();
                return;
            }else if(dao.getAmountWithCCCDPeople(cccd) > 1){
                CustomToast.makeText(this, "CCCD/CMND đã tồn tại !", false).show();
                return;
            }
            if(pass.length() < 3){
                CustomToast.makeText(this, "Mật khẩu tối thiểu 3 kí tự !", false).show();
                return;
            }
            people.setFullName(name);
            people.setSDT(sdt);
            people.setCCCD(cccd);
            people.setAddress(address);
            people.setPassowrd(pass);
            int sex = 1;
            if (rdo_feMale.isChecked())
                sex = 0;
            people.setSex(sex);
            if(rdo_transferShift.isChecked())
                status = 4;
            else if(rdo_inActive.isChecked())
                status = 5;
            else
                status = 1;
            people.setStatus(status);
            //
            dao.UpdateUser(people);
            adapter.notifyItemChanged(position);
            CustomToast.makeText(this, "Cập Nhật Thành Công", true).show();
            dialog.cancel();
        });
        btn_cancel.setOnClickListener(v1 -> {
            dialog.cancel();
        });
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.dialog_slide_left_to_right;
        dialog.show();
    }
}