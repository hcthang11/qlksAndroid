package com.ming6464.ungdungquanlykhachsanmctl;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ming6464.ungdungquanlykhachsanmctl.DTO.Categories;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.OrderDetail;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Orders;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.People;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Rooms;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.ServiceCategory;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.ServiceOrder;
import com.ming6464.ungdungquanlykhachsanmctl.DTO.Services;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public abstract class KhachSanDAO {
    // categories
    @Query("SELECT * FROM categories")
    public abstract List<Categories> getAllOfLoaiPhong();

    @Insert
    public abstract void insertOfLoaiPhong(Categories obj);


    //services
    @Insert
    public abstract void insertOfService(Services services);

    @Query("SELECT * FROM services WHERE id = :id")
    public abstract Services getObjOfServices(int id);

    @Query("SELECT * FROM Services")
    public abstract List<Services> getAllService();

    public List<Services> getListWithOrderDetailIdOfService(int id){
        List<Services> list = new ArrayList<>();
        for (ServiceOrder x : getListWithOrderDetailIdOfServiceOrder(id)){
            list.add(getObjOfServices(x.getServiceId()));
        }
        return list;
    }

    public List<Services> getListWithRoomIdOfServices(String id){
        List<Services> list = new ArrayList<>();
        for (ServiceCategory x : getListWithCategoryIdOfServiceCategory(getObjOfRooms(id).getCategoryID())){
            list.add(getObjOfServices(x.getServiceID()));
        }
        return list;
    }


    //User
    @Insert
    public abstract void insertOfUser(People people);

    @Query("SELECT * FROM People WHERE status = :status")
    public abstract List<People> getListWithStatusOfUser(int status);

    @Query("SELECT * FROM People")
    public abstract List<People> getListUser();

    @Query("SELECT * FROM PEOPLE WHERE  SDT = :phoneNumber")
    public abstract People getObjOfUser(String phoneNumber);

    @Query("SELECT * FROM PEOPLE WHERE cccd = :CCCD_or_CMND")
    public abstract People getObjWithCCCDOfUser(String CCCD_or_CMND);

    @Update
    public abstract void UpdateUser(People people);

    @Delete
    public abstract void DeleteUser(People people);

    @Query("SELECT * FROM people WHERE id = :id")
    public abstract People getObjOfUser(int id);

    @Query("SELECT * FROM PEOPLE WHERE status = 0")
    public abstract List<People> getListKhachHangOfUser();

    //serviceCategory
    @Insert
    public abstract void insertOfServiceCategory(ServiceCategory obj);

    @Query("SELECT * FROM ServiceCategory WHERE categoryID = :id")
    public abstract List<ServiceCategory> getListWithCategoryIdOfServiceCategory(int id);


    //Rooms
    @Insert
    public abstract void insertOfRooms(Rooms obj);

    @Query("SELECT * FROM Rooms WHERE id = :id")
    public abstract Rooms getObjOfRooms(String id);

    @Query("SELECT * FROM Rooms")
    public abstract List<Rooms> getAllOfRooms();

    @Update
    public abstract void updateOfRooms(Rooms obj);

    @Query("SELECT price FROM Categories WHERE id = (SELECT categoryID  FROM Rooms WHERE id = :id)")
    public abstract int getPriceWithIdOfRooms(String id);

    @Query("SELECT * FROM rooms WHERE id in (SELECT roomID FROM orderdetail WHERE orderID = :id)")
    public abstract List<Rooms> getListWithOrderIdOfRooms(int id);

    //Orders
    @Insert
    public abstract void insertOfOrders(Orders obj);

    @Query("SELECT * FROM Orders")
    public abstract List<Orders> getAllOfOrders();

    @Query("SELECT MAX(id) FROM Orders")
    public abstract int getNewIdOfOrders();

    @Query("SELECT MAX(id) FROM Orders WHERE customID = :id AND status = :status")
    public abstract int getIdWithPeopleIdOfOrder(int id, int status);

    public void updateTotalOfOrders(int id, int money) {
        Orders obj = getObjOfOrders(id);
        obj.setTotal(obj.getTotal() + money);
        updateOfOrders(obj);
    }

    @Query("SELECT * FROM Orders WHERE id = :id")
    public abstract Orders getObjOfOrders(int id);

    @Update
    public abstract void updateOfOrders(Orders obj);

    public void checkOutRoomOfOrder(int id){
        Orders obj = getObjOfOrders(id);
        obj.setStatus(1);
        updateOfOrders(obj);
        for(OrderDetail x : getListWithOrderIdOfOrderDetail(obj.getId())){
            x.setStatus(1);
            updateOfOrderDetail(x);
        }
    }
    @Query("SELECT * FROM Orders WHERE status = :status")
    public abstract List<Orders> getListWithStatusOfOrders(int status);

    @Query("SELECT * FROM ORDERS WHERE CUSTOMID = :peopleId AND status = 0 AND id IN (SELECT ORDERID FROM ORDERDETAIL WHERE STARTDATE = :checkIn GROUP BY ORDERID)")
    public abstract Orders getObjUnpaidWithPeopleIdfOrders(int peopleId,Date checkIn);


    //OrderDetail
    @Insert
    public abstract void insertObjOfOrderDetail(OrderDetail obj);

    @Update
    public abstract void updateOfOrderDetail(OrderDetail obj);

    @Query("SELECT * FROM orderdetail")
    public abstract List<OrderDetail> getAllOfOrderDetail();

    @Query("SELECT * FROM orderdetail WHERE status = :status")
    public abstract List<OrderDetail> getListWithStatusOfOrderDetail(int status);

    @Query("SELECT * FROM orderdetail WHERE orderID = :id")
    public abstract List<OrderDetail> getListWithOrderIdOfOrderDetail(int id);

    @Query("SELECT MIN(startDate) FROM orderdetail WHERE orderID = :id")
    public abstract Date getMinStatDateWithIdOrderOfOrderDetail(int id);

    @Query("SELECT MAX(endDate) FROM orderdetail WHERE orderID = :id")
    public abstract Date getMaxEndDateWithIdOrderOfOrderDetail(int id);

    @Query("SELECT * FROM ORDERDETAIL WHERE ROOMID = :roomId AND STATUS = 2")
    public abstract List<OrderDetail> getListReserveWithRoomIdOfOrderDetail(String roomId);

    public OrderDetail getNextReserveOfOrderDetail(int id){
        OrderDetail obj = getObjOrderDetail(id),nextOrder = null;
        List<OrderDetail> list = getListReserveWithRoomIdOfOrderDetail(obj.getRoomID());
        if(list.size() > 0){
            int min,i;
            nextOrder = list.get(0);
            min = (int)(nextOrder.getStartDate().getTime() - obj.getEndDate().getTime())/3600000;
            for(OrderDetail x : list){
                if(x.getStartDate() == obj.getEndDate())
                    return x;
                i = (int)(nextOrder.getStartDate().getTime() - obj.getEndDate().getTime())/3600000;
                if(i < min){
                    min = i;
                    nextOrder = x;
                }
            }
        }
        return nextOrder;
    }

    public void insertOfOrderDetail(OrderDetail obj) {
        insertObjOfOrderDetail(obj);
        int priceRooms = getPriceWithIdOfRooms(obj.getRoomID());
        int amount_date = (int) (obj.getEndDate().getTime() - obj.getStartDate().getTime()) / (3600000 * 24) + 1;
        updateTotalOfOrders(obj.getOrderID(), priceRooms * amount_date);
    }

    @Query("SELECT * FROM ORDERDETAIL WHERE ID = :id")
    public abstract OrderDetail getObjOrderDetail(int id);

    @Query("SELECT * FROM ORDERDETAIL WHERE ROOMID = :id")
    public abstract OrderDetail getWithRoomIdOfOrderDetail (String id);

    @Query("SELECT MAX(id) FROM orderdetail")
    public abstract int getNewIdOfOrderDetail();

    public void cancelOfOrderDetail(int id){
        OrderDetail obj = getObjOrderDetail(id);
        obj.setStatus(4);
        updateOfOrderDetail(obj);
        boolean check = true;
        for(OrderDetail x : getListWithOrderIdOfOrderDetail(obj.getOrderID())){
            if(x.getStatus() != 4){
                check = false;
                break;
            }
        }
        if(check){
            Orders orders = getObjOfOrders(obj.getOrderID());
            orders.setStatus(2);
            updateOfOrders(orders);
        }
    }

    //serviceOrder
    @Insert
    public abstract void insertObjOfServiceOrder(ServiceOrder obj);

    @Query("SELECT * FROM SERVICEORDER WHERE serviceId = :idService AND  orderDetailID = :idOrderDetail")
    public abstract ServiceOrder getObjOfServiceOrder(int idService,int idOrderDetail);

    public void insertOfServiceOrder(ServiceOrder obj){
        ServiceOrder x = getObjOfServiceOrder(obj.getServiceId(),obj.getOrderDetailID());
        if(x != null){
            x.setAmount(x.getAmount() + obj.getAmount());
            updateOfServiceOrder(x);
        }else{
            insertObjOfServiceOrder(obj);
        }
        updateTotalOfOrders(getIdOrderWithIdOrderDetail(obj.getOrderDetailID()),getObjOfServices(obj.getServiceId()).getPrice() * obj.getAmount());
    }

    @Update
    public abstract void updateOfServiceOrder(ServiceOrder obj);

    @Query("SELECT * FROM SERVICEORDER WHERE ORDERDETAILID = :id")
    public abstract List<ServiceOrder> getListWithOrderDetailIdOfServiceOrder(int id);

    ////
    public String formatId(int id) {
        if (id < 10)
            return "#0" + id;
        return "#" + id;
    }

    @Query("SELECT * FROM Categories WHERE id = (SELECT categoryID FROM Rooms WHERE id = :id)")
    public abstract Categories getCategoryWithRoomId(String id);

    @Query("SELECT * FROM services WHERE id in (SELECT serviceID FROM servicecategory WHERE categoryID = (SELECT categoryID FROM Rooms WHERE id = :id))")
    public abstract List<Services> getListServiceCategoryWithRoomId(String id);

    public List<String> getListNameCategoryWithRoomId(List<Rooms> roomsList){
        List<String> list = new ArrayList<>();
        for(Rooms x : roomsList){
            list.add(getCategoryWithRoomId(x.getId()).getName());
        }
        return list;
    }

    @Query("SELECT amountOfPeople FROM categories WHERE id = (SELECT categoryID FROM Rooms WHERE id = :id)")
    public abstract int getAmountOfPeopleCategoryWithRoomId(String id);

    @Query("SELECT orderID FROM orderdetail WHERE id = :id")
    public abstract int getIdOrderWithIdOrderDetail(int id);

    // check login
    @Query("SELECT * FROM People Where SDT = :user")
    public abstract People checkLogin(String user);

    //get data
    @Query("SELECT * FROM People Where SDT =:sdt")
    public abstract People getUserBy(String sdt);
    
    @Query("SELECT SUM(PRICE * AMOUNT) FROM SERVICES AS A, SERVICEORDER WHERE A.ID = SERVICEID AND ORDERDETAILID = :id")
    public abstract int getTotalServiceWithOrderDetailId(int id);
    
    @Query("SELECT ROOMID FROM ORDERDETAIL WHERE ((:checkIn BETWEEN STARTDATE AND ENDDATE) OR (STARTDATE BETWEEN :checkIn AND :checkOut)) AND (STATUS = 1 AND STATUS = 4)")
    public abstract List<String> getListRoomIdBusyWithTime (Date checkIn,Date checkOut);

    public List<Rooms> getListRoomWithTime(Date checkIn, Date checkOut){
        List<Rooms> list = new ArrayList<>();
        if(checkIn.getTime() < checkOut.getTime()){
            list = getAllOfRooms();
            List<String> listRoomId = getListRoomIdBusyWithTime(checkIn,checkOut);
            for(Rooms x : list){
                if(listRoomId.contains(x.getId()))
                    x.setStatus(1);
                else
                    x.setStatus(0);
            }
        }
        return list;
    }

    @Query("SELECT COUNT(*) FROM ORDERDETAIL WHERE ENDDATE = :checkOut AND STATUS != 1")
    public abstract int getCountOrderDetailWithCheckOut(Date checkOut);

    @Query("SELECT * FROM ORDERDETAIL WHERE ENDDATE = :checkout AND (STATUS = 0 OR STATUS = 4)")
    public abstract List<OrderDetail> getListOrderDetailCheckOut(Date checkout);

}
