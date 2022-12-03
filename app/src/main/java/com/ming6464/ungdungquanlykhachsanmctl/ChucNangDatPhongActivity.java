package com.ming6464.ungdungquanlykhachsanmctl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ming6464.ungdungquanlykhachsanmctl.Activiti_User.Activity_QuanLy;
import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemService1Adapter;
import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemServiceSpinnerAdapter;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.OrderDetail;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Orders;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.People;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.ServiceOrder;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Services;
import com.ming6464.ungdungquanlykhachsanmctl.Fragment.PhongFragment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChucNangDatPhongActivity extends AppCompatActivity implements ItemService1Adapter.EventOfItemService1Adapter {
    private int status,totalService = 0,hours,deposit = 0,total,color,minDeposit = 0;
    private String idRoom;
    private List<String> userListString;
    private List<Services> serviceList,serviceList1;
    private List<ServiceOrder> serviceOrderList;
    private NumberFormat format;
    private Spinner sp_customer,sp_amountOfPeople,sp_service;
    private EditText ed_fullName,ed_phoneNumber,ed_CCCD,ed_address;
    private TextView tv_total,tv_room,tv_checkIn,tv_checkOut,tv_deposit,tv_titleDeposit;
    private RadioButton rdo_male,rdo_newCustomer;
    private KhachSanDAO dao;
    private ItemService1Adapter itemServiceOrderAdapter;
    private RecyclerView rc_service;
    private Date checkIn,checkOut;
    private KhachSanSharedPreferences share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuc_nang_dat_phong);
        dao = KhachSanDB.getInstance(this).getDAO();
        share = new KhachSanSharedPreferences(this);
        format = NumberFormat.getInstance(new Locale("en","EN"));
        anhXa();
        hanldeDataBundle();
        handlerSpinner();
        handlerRecyclerService();
        loadTotal();
        minDeposit = Math.round(total * 0.5f);
        loadDeposit();
    }

    private void hanldeDataBundle() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH");
        Bundle bundle = getIntent().getBundleExtra(PhongFragment.KEY_BUNDLE);
        idRoom = bundle.getString(PhongFragment.KEY_ROOM);
        status = bundle.getInt(PhongFragment.KEY_STATUS);
        checkOut = new Date(bundle.getLong(PhongFragment.KEY_CHECKOUT));
        checkIn = new Date(bundle.getLong(PhongFragment.KEY_CHECKIN));
        hours = (int) (checkOut.getTime() - checkIn.getTime())/3600000;
        tv_room.setText(idRoom);
        tv_checkOut.setText(sdf.format(checkOut) + "h");
        tv_checkIn.setText(sdf.format(checkIn) + "h");
        if(status == 2){
            findViewById(R.id.actiCNDP_linear_services).setVisibility(View.GONE);
            findViewById(R.id.actiCNDP_linear_deposit).setVisibility(View.VISIBLE);
        }
    }

    private void handlerRecyclerService() {
        itemServiceOrderAdapter = new ItemService1Adapter(this);
        rc_service.setHasFixedSize(true);
        rc_service.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rc_service.setAdapter(itemServiceOrderAdapter);
        itemServiceOrderAdapter.setData(serviceList1);
    }

    private void anhXa() {
        ed_fullName = findViewById(R.id.actiCNDP_ed_fullName);
        ed_CCCD = findViewById(R.id.actiCNDP_ed_CCCD);
        ed_address = findViewById(R.id.actiCNDP_ed_address);
        ed_phoneNumber = findViewById(R.id.actiCNDP_ed_phoneNumber);
        rdo_male = findViewById(R.id.actiCNDP_rdo_male);
        rdo_newCustomer = findViewById(R.id.actiCNDP_rdo_newCustomer);
        sp_customer = findViewById(R.id.actiCNDP_sp_customer);
        sp_amountOfPeople = findViewById(R.id.actiCNDP_sp_amountOfPeople);
        sp_service = findViewById(R.id.actiCNDP_sp_service);
        tv_total = findViewById(R.id.actiCNDP_tv_total);
        tv_room = findViewById(R.id.actiCNDP_tv_room);
        tv_checkIn = findViewById(R.id.actiCNDP_tv_checkIn);
        tv_checkOut = findViewById(R.id.actiCNDP_tv_checkOut);
        rc_service = findViewById(R.id.actiCNDP_rc_service);
        tv_deposit = findViewById(R.id.actiCNDP_tv_deposit);
        tv_titleDeposit = findViewById(R.id.actiCNDP_tv_titleDeposit);
    }

    private void handlerSpinner() {
        serviceOrderList = new ArrayList<>();
        serviceList1 = new ArrayList<>();
        serviceList = new ArrayList<>();
        userListString = new ArrayList<>();
        ////
        for(People x : dao.getListKhachHangOfUser()){
            userListString.add(formatId(x.getId()) + " " + x.getFullName());
        }
        ArrayAdapter userAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, userListString);
        sp_customer.setAdapter(userAdapter);
        sp_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadInfoOldCustom(Integer.parseInt(userListString.get(position).substring(1,userListString.get(position).indexOf(" "))));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /////
        List<String> amountOfPeopleList = new ArrayList<>();
        for(int x = 1; x <= dao.getAmountOfPeopleCategoryWithRoomId(idRoom); x ++){
            amountOfPeopleList.add(String.valueOf(x));
        }
        ArrayAdapter amountOfPeopleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, amountOfPeopleList);
        sp_amountOfPeople.setAdapter(amountOfPeopleAdapter);
        //////
        serviceList = dao.getListServiceCategoryWithRoomId(idRoom);
        //
        ItemServiceSpinnerAdapter itemServiceSpinnerAdapter = new ItemServiceSpinnerAdapter();
        sp_service.setAdapter(itemServiceSpinnerAdapter);
        itemServiceSpinnerAdapter.setDate(serviceList);
        //////
        for (Services x : serviceList){
            serviceOrderList.add(new ServiceOrder(x.getId(),0,0));
        }
    }
    public String formatId(int id) {
        if (id < 10)
            return "#0" + id;
        return "#" + id;
    }

    public void handleActionRdoOldCustomer(View view){
        if(userListString.size() > 0){
            findViewById(R.id.actiCNDP_layout_oldCustomer).setVisibility(View.VISIBLE);
            setFocusInfomation(false);
            loadInfoOldCustom(Integer.parseInt(userListString.get(0).substring(1,userListString.get(0).indexOf(" "))));
            return;
        }
        rdo_newCustomer.setChecked(true);
        CustomToast.makeText(this, "Không có khách hàng cũ !", false).show();
    }

    public void handleActionRdoNewCustomer(View view){
        setFocusInfomation(true);
        findViewById(R.id.actiCNDP_layout_oldCustomer).setVisibility(View.GONE);
        ed_address.setText("");
        ed_CCCD.setText("");
        ed_fullName.setText("");
        ed_phoneNumber.setText("");
    }
    public void handleActionBtnSave(View view) {
        if(status == 2){
            if(color == R.color.coNguoi){
                CustomToast.makeText(this,"Tiền cọc không đủ !",false).show();
                return;
            }
        }
        int idCustomer,idOrder, amountOfPeople = Integer.parseInt(sp_amountOfPeople.getSelectedItem().toString()),idOrderDetail;
        if(rdo_newCustomer.isChecked()){
            String fullName = ed_fullName.getText().toString(),
                    phoneNumber = ed_phoneNumber.getText().toString(),
                    cccd = ed_CCCD.getText().toString(),
                    address = ed_address.getText().toString();
            if(fullName.isEmpty() || phoneNumber.isEmpty() || cccd.isEmpty() || address.isEmpty()){
                CustomToast.makeText(this, "Thông tin khách hàng không được bỏ trống !", false).show();
                return;
            }
            if(!fullName.matches("^[a-zA-Z ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$")){
                CustomToast.makeText(this, "Tên không phù hợp", false).show();
                return;
            }
            if(!phoneNumber.matches("^0\\d{9}$")){
                CustomToast.makeText(this, "Số điện thoại không đúng !", false).show();
                return;
            }
            if(cccd.length() != 12){
                CustomToast.makeText(this, "CCCD/CMND Không chính xác !", false).show();
                return;
            }
            if(dao.getObjOfUser(phoneNumber) != null){
                CustomToast.makeText(this, "Số điện thoại đã tồn tại !", false).show();
                return;
            }
            if (dao.getObjWithCCCDOfUser(cccd) != null) {
                CustomToast.makeText(this, "CCCD/CMND đã tồn tại !", false).show();
                return;
            }
            int sex = 0;
            if(rdo_male.isChecked())
                sex = 1;
            dao.insertOfUser(new People(fullName,phoneNumber, cccd,address,sex,0));
            idCustomer = dao.getObjOfUser(phoneNumber).getId();
            dao.insertOfOrders(new Orders(idCustomer,share.getID()));
            idOrder = dao.getNewIdOfOrders();
        }
        else {
            String text = sp_customer.getSelectedItem().toString();
            idCustomer = Integer.parseInt(text.substring(1,text.indexOf(" ")));
            Orders orders1 = dao.getObjUnpaidWithPeopleIdfOrders(idCustomer);
            if(orders1 == null){
                dao.insertOfOrders(new Orders(idCustomer,share.getID()));
                idOrder = dao.getNewIdOfOrders();
            }else{
                idOrder = orders1.getId();
            }
        }
        OrderDetail orderDetail = new OrderDetail(idRoom,idOrder,
                amountOfPeople,checkIn,checkOut);
        orderDetail.setStatus(status);
        if(status == 2)
            orderDetail.setDeposit(deposit);

        dao.insertOfOrderDetail(orderDetail);
        idOrderDetail = dao.getNewIdOfOrderDetail();
        for(ServiceOrder x : serviceOrderList){
            if(x.getAmount() != 0){
                x.setOrderDetailID(idOrderDetail);
                dao.insertOfServiceOrder(x);
            }
        }
        //
        if(dao.getCountOrderDetailWithCheckOut(checkOut)  == 1){
            Intent intent = new Intent(this,KhachSanReceiver.class);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,idOrderDetail + 1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d("TAG.m.a", "handleActionBtnSave: " + checkOut.getTime());
            manager.set(AlarmManager.RTC_WAKEUP,checkOut.getTime() - 600000,pendingIntent);
        }
        //
        CustomToast.makeText(this, "Đặt thành công !", true).show();
        finish();
    }

    public void handleActionBtnCancel(View view) {
        finish();
    }
    private void loadTotal(){
        total = dao.getPriceWithIdOfRooms(idRoom)  * hours + totalService;
        tv_total.setText(format.format(total) + "K");
    }
    public void handleActionBtnAddService(View view){
        int index = sp_service.getSelectedItemPosition();
        Services sv = serviceList.get(index);
        serviceList1.add(sv);
        totalService += sv.getPrice();
        for(int i = 0; i < serviceOrderList.size(); i++){
            if(serviceOrderList.get(i).getServiceId() == sv.getId()){
                serviceOrderList.get(i).setAmount(serviceOrderList.get(i).getAmount() + 1);
                break;
            }
        }
        itemServiceOrderAdapter.notifyDataSetChanged();
        loadTotal();
    }
    @Override
    public void cancel(int position) {
        Services sv = serviceList1.get(position);
        totalService -= sv.getPrice();
        serviceList1.remove(position);
;        for(int i = 0; i < serviceOrderList.size(); i++){
            if(serviceOrderList.get(i).getServiceId() == sv.getId()){
                serviceOrderList.get(i).setAmount(serviceOrderList.get(i).getAmount() - 1);
                break;
            }
        }
        itemServiceOrderAdapter.notifyDataSetChanged();
        loadTotal();
    }
    private void setFocusInfomation(boolean b){
        ed_fullName.setFocusableInTouchMode(b);
        ed_CCCD.setFocusableInTouchMode(b);
        ed_address.setFocusableInTouchMode(b);
        ed_phoneNumber.setFocusableInTouchMode(b);
        rdo_male.setEnabled(b);
        findViewById(R.id.actiCNDP_rdo_feMale).setEnabled(b);
    }
    private void loadInfoOldCustom(int id) {
        People people = dao.getObjOfUser(id);
        ed_phoneNumber.setText(people.getSDT());
        ed_fullName.setText(people.getFullName());
        ed_CCCD.setText(people.getCCCD());
        ed_address.setText(people.getAddress());
        if(people.getSex() == 1)
            rdo_male.setChecked(true);
        else{
            RadioButton rdo_feMale = findViewById(R.id.actiCNDP_rdo_feMale);
            rdo_feMale.setChecked(true);
        }
    }

    public void handleActionDeposit(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_deposit);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.dialog_slide_left_to_right;
        //
        EditText ed_deposit = dialog.findViewById(R.id.dialogDeposit_ed_deposit);
        TextView yes = dialog.findViewById(R.id.dialogDeposit_tv_yes), no = dialog.findViewById(R.id.dialogDeposit_tv_no),
                tv_titleMinDeposit = dialog.findViewById(R.id.dialogDeposit_tv_titleMinDeposit);
        tv_titleMinDeposit.setText("* Cọc Tối thiểu " + format.format(minDeposit) + "K");
        //
        ed_deposit.setText(String.valueOf(deposit));
        //
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_deposit = ed_deposit.getText().toString();
                if(s_deposit.isEmpty())
                    CustomToast.makeText(ChucNangDatPhongActivity.this,"Số tiền cọc không đủ !", false).show();
                else{
                    int i_deposit = Integer.parseInt(s_deposit);
                    if(i_deposit >= minDeposit){
                        deposit = i_deposit;
                        loadDeposit();
                        dialog.cancel();
                    }else
                        CustomToast.makeText(ChucNangDatPhongActivity.this,"Số tiền cọc không đủ !", false).show();
                }
            }
        });
        dialog.show();
    }

    private void loadDeposit(){
        if(deposit >= minDeposit){
            color = R.color.blue;
            tv_titleDeposit.setVisibility(View.GONE);
        }else{
            tv_titleDeposit.setVisibility(View.VISIBLE);
            tv_titleDeposit.setText("* Tiền cọc tối thiểu (50%) " + format.format(minDeposit) + "K");
            color = R.color.coNguoi;
        }
        tv_deposit.setText(format.format(deposit) + "K");
        tv_deposit.setTextColor(getResources().getColor(color));
    }
}