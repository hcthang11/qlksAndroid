package com.ming6464.ungdungquanlykhachsanmctl;

import static com.ming6464.ungdungquanlykhachsanmctl.Fragment.HoaDonTongFragment.KEY_ORDER;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ming6464.ungdungquanlykhachsanmctl.Adapter.ItemService3Adapter;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.OrderDetail;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Orders;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.People;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HoaDonChiTietActivity extends AppCompatActivity {
    private Orders ordersObj;
    private People customerObj;
    private int changeMoney = 0 ,totalRoom = 0, totalService = 0,
            totalPrepay = 0,totalPrepayCancel = 0,totalPrepayUndefined = 0,totalUnPay = 0;
    private NumberFormat format;
    private KhachSanDAO dao;
    private boolean checkSuccess = false;
    private ConstraintLayout constrain_order;
    private ProgressBar pg_load;
    private String room = "";
    private EditText ed_fullName,ed_sex,ed_phoneNumber,ed_CCCD,ed_address,ed_moneyOfCustomer;
    private TextView tv_totalService,tv_totalRoom,tv_totalPrepayCancel,tv_totalPay,tv_changeMoney,tv_totalPrepay,tv_totalPrepayUndefined,tv_totalUnPay;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoa_don_chi_tiet);
        dao = KhachSanDB.getInstance(this).getDAO();
        ordersObj = (Orders) getIntent().getSerializableExtra(KEY_ORDER);
        customerObj = dao.getObjOfUser(ordersObj.getCustomID());
        anhXa();
        new Handler().postDelayed(() -> {
            constrain_order.setVisibility(View.VISIBLE);
            pg_load.setVisibility(View.GONE);
        },800);
        format = NumberFormat.getInstance(new Locale("en","EN"));
        handleToolbar();
        handleInfoCustomer();
        handleAction();
        handleInfoOrder();
        loadChange();
    }

    private void handleAction() {
        ed_moneyOfCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadChange();
            }
        });
    }

    private void loadChange() {
        String s_money = ed_moneyOfCustomer.getText().toString();
        int money = 0;
        if(!ed_moneyOfCustomer.getText().toString().isEmpty()){
            money = Integer.parseInt(s_money);
        }
        changeMoney = money - totalUnPay - totalService;
        tv_changeMoney.setText(format.format(changeMoney) + "K");
    }

    private void handleInfoOrder() {
        addOrderDetail();
        if(totalPrepayUndefined > 0)
            room = room.substring(0,room.length() - 2);
        tv_totalPrepayCancel.setText(format.format(totalPrepayCancel) + "K");
        tv_totalPrepay.setText(format.format(totalPrepay) + "K");
        tv_totalPrepayUndefined.setText(format.format(totalPrepayUndefined) + "K");

        if(!checkSuccess){
            findViewById(R.id.actiHDCT_linear_pay).setVisibility(View.GONE);
        }
        int money = totalUnPay + totalService;
        if(money == 0){
            findViewById(R.id.actiHDCT_inputLayout_1).setVisibility(View.GONE);
        }

        if(ordersObj.getStatus() != 0){
            findViewById(R.id.actiHDCT_linear_pay).setVisibility(View.GONE);
        }else
            tv_totalPay.setText(format.format(totalUnPay + totalService)  + "K");

        tv_totalService.setText(format.format(totalService) + "K");
        tv_totalRoom.setText(format.format(totalRoom) + "K");
        tv_totalPrepay.setText(format.format(totalPrepay) + "K");
        tv_totalUnPay.setText(format.format(totalUnPay) + "K");
    }

    private void addOrderDetail() {
        TextView tv_room,tv_roomPrice,tv_checkIn,tv_checkOut,tv_serviceFee,tv_roomFee,tv_hours,tv_prepay;
        RecyclerView rc_service;
        LinearLayoutCompat linear = findViewById(R.id.actiHDCT_Linear_orderDetail),linear_orderDetail;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH");
        ItemService3Adapter subAdapter;
        for(OrderDetail x : dao.getListWithOrderIdOfOrderDetail(ordersObj.getId())){
            View itemView = LayoutInflater.from(linear.getContext()).inflate(R.layout.item_order_detail2,null);
            tv_room = itemView.findViewById(R.id.itemOrderDetail2_tv_room);
            tv_roomPrice = itemView.findViewById(R.id.itemOrderDetail2_tv_roomPrice);
            tv_hours = itemView.findViewById(R.id.itemOrderDetail2_tv_hours);
            tv_checkIn = itemView.findViewById(R.id.itemOrderDetail2_tv_checkIn);
            tv_checkOut = itemView.findViewById(R.id.itemOrderDetail2_tv_checkOut);
            tv_serviceFee = itemView.findViewById(R.id.itemOrderDetail2_tv_serviceFee);
            tv_roomFee = itemView.findViewById(R.id.itemOrderDetail2_tv_roomFee);
            tv_prepay = itemView.findViewById(R.id.itemOrderDetail2_tv_paySuccess);
            rc_service = itemView.findViewById(R.id.itemOrderDetail2_rc_service);
            linear_orderDetail = itemView.findViewById(R.id.itemOrderDetail2_linear_orderDetail2);
            ////
            tv_checkIn.setText("Check In :  "  + sdf.format(x.getCheckIn()) + "h");
            tv_checkOut.setText("Check Out :  "  + sdf.format(x.getCheckOut()) + "h");
            tv_room.setText(x.getRoomID());
            int amount_date = (int) (x.getCheckOut().getTime() - x.getCheckIn().getTime())/(3600000 * 24) + 1;
            int status = x.getStatus();
            int roomPrice = dao.getCategoryWithRoomId(x.getRoomID()).getPrice();
            int serviceFee = dao.getTotalServiceWithOrderDetailId(x.getId());
            int prepay = x.getPrepay();
            totalRoom += roomPrice * amount_date;
            totalService += serviceFee;
            tv_roomPrice.setText("Giá Phòng :  " + format.format(roomPrice) + "K");
            tv_hours.setText("Thời Gian :  " + amount_date + "Ngày");
            tv_roomFee.setText(format.format(roomPrice * amount_date) + "K");
            if(prepay > 0){
                tv_prepay.setVisibility(View.VISIBLE);
            }
            tv_serviceFee.setText(format.format(serviceFee) + "K");
            subAdapter = new ItemService3Adapter();
            subAdapter.setData(dao.getListWithOrderDetailIdOfService(x.getId()),
                    dao.getListWithOrderDetailIdOfServiceOrder(x.getId()));
            rc_service.setAdapter(subAdapter);
            rc_service.setLayoutManager(new LinearLayoutManager(this));
            if(status == 0){
                linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_chuathanhtoan);
                if(prepay > 0)
                    totalPrepay += prepay;
                else
                    totalUnPay += roomPrice * amount_date;
                checkSuccess = true;
            }
            else if(status == 1){
                linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_thanhtoan);
                if(prepay > 0)
                    totalPrepay += prepay;
                else
                    totalUnPay += roomPrice * amount_date;
                checkSuccess = true;
            }
            else if(status == 2){
                linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_dattruoc);
                totalPrepayUndefined += prepay;
                room += x.getRoomID() + ", ";
            }
            else  if(status == 3){
                linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_cothenhanphong);
                totalPrepayUndefined += prepay;
                room += x.getRoomID() + ", ";
            }
            else{
                linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_huyphong);
                totalPrepayCancel += prepay;
            }
            linear.addView(itemView);
        }
    }

    private void handleInfoCustomer() {
        ed_fullName.setText(customerObj.getFullName());
        ed_CCCD.setText(customerObj.getCCCD());
        ed_address.setText(customerObj.getAddress());
        ed_phoneNumber.setText(customerObj.getSDT());
        String sex = "Nam";
        if(customerObj.getSex() == 0)
            sex  = "Nữ";
        ed_sex.setText(sex);
    }

    private void handleToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void anhXa() {
        pg_load = findViewById(R.id.actiHDCT_pg_load);
        constrain_order = findViewById(R.id.actiHDCT_contrains_order);
        ed_fullName = findViewById(R.id.actiHDCT_ed_fullName);
        ed_sex = findViewById(R.id.actiHDCT_ed_Sex);
        ed_phoneNumber = findViewById(R.id.actiHDCT_ed_phoneNumber);
        ed_CCCD = findViewById(R.id.actiHDCT_ed_CCCD);
        ed_address = findViewById(R.id.actiHDCT_ed_address);
        ed_moneyOfCustomer = findViewById(R.id.actiHDCT_ed_moneyOfCustomer);
        tv_totalPay = findViewById(R.id.actiHDCT_tv_totalPay);
        tv_totalRoom = findViewById(R.id.actiHDCT_tv_totalRoom);
        tv_totalService = findViewById(R.id.actiHDCT_tv_totalService);
        tv_changeMoney = findViewById(R.id.actiHDCT_tv_changeMoney);
        tv_totalPrepay = findViewById(R.id.actiHDCT_tv_totalPrepay);
        tv_totalPrepayCancel = findViewById(R.id.actiHDCT_tv_totalPrepayCancel);
        tv_totalPrepayUndefined = findViewById(R.id.actiHDCT_tv_totalPrepayUndefined);
        tv_totalUnPay = findViewById(R.id.actiHDCT_tv_totalUnPay);
        toolbar = findViewById(R.id.actiHDCT_tb);
    }

    public void handleActionBack(View view) {
        this.finish();
    }

    public void handleActionPay(View view) {
        if(!ed_moneyOfCustomer.getText().toString().isEmpty() && changeMoney >= 0){
            if(totalPrepayUndefined > 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Lưu Ý !");
                builder.setMessage("Các phòng " + room + " đều vẫn chưa được sử dụng. Việc thanh toán sẽ tạo ra 1 hoá đơn tổng mới");
                builder.setPositiveButton("Xác Nhận", (dialog, which) -> checkOutOrder());
                builder.setNegativeButton("Huỷ", (dialog, which) -> {
                });
                builder.show();
                return;
            }
            checkOutOrder();
        }
        else
            CustomToast.makeText(this,"Khách đưa thiếu !",false).show();
    }

    private void checkOutOrder(){
        dao.checkOutRoomOfOrder(ordersObj.getId(), new Date(System.currentTimeMillis()));
        constrain_order.setVisibility(View.GONE);
        pg_load.setVisibility(View.VISIBLE);
        findViewById(R.id.actiHDCT_img_back).setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomToast.makeText(HoaDonChiTietActivity.this,"Thanh toán thành công !",true).show();
                finish();
            }
        },1000);
    }
}