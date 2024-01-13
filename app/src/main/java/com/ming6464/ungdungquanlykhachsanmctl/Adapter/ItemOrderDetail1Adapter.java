package com.ming6464.ungdungquanlykhachsanmctl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ming6464.ungdungquanlykhachsanmctl.DTO.OrderDetail;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.People;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDAO;
import com.ming6464.ungdungquanlykhachsanmctl.KhachSanDB;
import com.ming6464.ungdungquanlykhachsanmctl.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemOrderDetail1Adapter extends RecyclerView.Adapter<ItemOrderDetail1Adapter.MyViewHolder> {
    private List<OrderDetail> list;
    private KhachSanDAO dao;
    private OnEventOfOrderDetailAdpater action;
    private SimpleDateFormat sdf,sdf1;
    private NumberFormat numberFormat;
    public ItemOrderDetail1Adapter(Context context, OnEventOfOrderDetailAdpater action){
        dao = KhachSanDB.getInstance(context).getDAO();
        this.action  = action;
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf1 = new SimpleDateFormat("HH");
        numberFormat = NumberFormat.getInstance(new Locale("en","EN"));
    }

    public void setData(List<OrderDetail> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public interface OnEventOfOrderDetailAdpater{
        void click(int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail1,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder h, int position) {
        OrderDetail obj = list.get(position);
        People people = dao.getObjOfUser(dao.getObjOfOrders(obj.getOrderID()).getCustomID());
        Date checkOut = obj.getCheckOut(),checkIn = obj.getCheckIn();
        h.tv_room.setText(obj.getRoomID());
        h.tv_fullName.setText(people.getFullName());
        h.tv_phoneNumber.setText("Số Điện Thoại :  " + people.getSDT());
        String status;
        int i_status = obj.getStatus();
        if(i_status == 0){
            status = "Đang Sử Dụng";
            h.btn_detail.setText("chức năng");
            h.linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_chuathanhtoan);
        }else if(i_status == 1){
            status = "Đã Trả Phòng";
            h.btn_detail.setText("Tới hoá đơn tổng");
            h.linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_thanhtoan);
        }
        else if(i_status == 2){
            status = "Đặt Trước";
            h.btn_detail.setText("chức năng");
            h.linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_dattruoc);

        }else if(i_status == 4){
            status = "Huỷ";
            h.btn_detail.setText("Tới hoá đơn tổng");
            h.linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_huyphong);

        }else {
            status = "Có Thể Nhận Phòng";
            h.btn_detail.setText("Chức năng");
            h.linear_orderDetail.setBackgroundResource(R.drawable.background_hoadon_cothenhanphong);
        }
        if(obj.getPrepay() > 0)
            h.img_paySuccess.setVisibility(View.VISIBLE);
        else
            h.img_paySuccess.setVisibility(View.GONE);
        h.tv_status.setText(status);
        h.tv_checkIn.setText( "Ngày Nhận :  " + sdf.format(checkIn));
        h.tv_checkOut.setText("Ngày Trả :  "  + sdf.format(checkOut));
        h.tv_hourCheckOut.setText("Giờ :  "  + sdf1.format(checkOut) + "h");
        h.tv_hourCheckIn.setText("Giờ :  "  + sdf1.format(checkIn) + "h");
        int amount_date = (int)(checkOut.getTime() - checkIn.getTime())/(3600000 * 24) + 1,
                price_room = dao.getPriceWithIdOfRooms(obj.getRoomID());
        h.tv_total.setText(numberFormat.format(price_room * amount_date + dao.getTotalServiceWithOrderDetailId(obj.getId())) + "K");
        h.btn_detail.setOnClickListener(v -> action.click(h.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if(list == null)
            return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_room,tv_fullName,tv_phoneNumber,tv_checkIn,tv_hourCheckIn,
                tv_checkOut,tv_hourCheckOut,tv_status,tv_total;
        private Button btn_detail;
        private ImageView img_paySuccess;
        private LinearLayoutCompat linear_orderDetail;
        public MyViewHolder(@NonNull View iv) {
            super(iv);
            tv_room = iv.findViewById(R.id.itemOrderDetail1_tv_room);
            tv_fullName = iv.findViewById(R.id.itemOrderDetail1_tv_fullName);
            tv_phoneNumber = iv.findViewById(R.id.itemOrderDetail1_tv_phoneNumber);
            tv_checkIn = iv.findViewById(R.id.itemOrderDetail1_tv_checkIn);
            tv_hourCheckIn = iv.findViewById(R.id.itemOrderDetail1_tv_hourCheckIn);
            tv_checkOut = iv.findViewById(R.id.itemOrderDetail1_tv_checkOut);
            tv_hourCheckOut = iv.findViewById(R.id.itemOrderDetail1_tv_hourCheckOut);
            tv_status = iv.findViewById(R.id.itemOrderDetail1_tv_status);
            tv_total = iv.findViewById(R.id.itemOrderDetail1_tv_total);
            btn_detail = iv.findViewById(R.id.itemHoaDonPhong_btn_detail);
            linear_orderDetail = iv.findViewById(R.id.itemOrderDetail1_linear_orderDetail1);
            img_paySuccess = iv.findViewById(R.id.itemOrderDetail1_img_paySuccess);
        }
    }
}
