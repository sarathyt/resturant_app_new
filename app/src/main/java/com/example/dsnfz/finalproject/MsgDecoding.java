package com.example.dsnfz.finalproject;

/**
 * Created by dsnfz on 2017/11/26.
 */
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * has more work to do in the storing the info, notice the param in each method.
 * this first version has not the return value. There should be a return value to return customer
 * type or order type.
 */
public class MsgDecoding {
    String flag;
    String Msg2Name;
    public MsgDecoding(){
        flag="";
    }

    /**
     * Decoding the Msg1 and store it's information in a customer instance
     * @param Msg1 verify the customer is doing the register
     *             or sign up. And send the info of the customer. If customer want to
     *             sign up, Msg1 does not contain customerName
     *             “R/S,customerID,customerPIN,customerName(exists when register)”
     *             e.g.:”R,CU1234,2333,Kiven”  “S,CH1234,3333”
     */

    public Customer DecodingMsg1(String Msg1){
        Customer customer=new Customer();
        Scanner scan = new Scanner(Msg1);
        scan.useDelimiter(",");
        if((flag=scan.next()).equals("R")){
            customer.setCustomerID(scan.next());
            customer.setPassword(scan.next());
            customer.setCustomerName(scan.next());
        }
        else if(flag.equals("S")){
            customer.setCustomerID(scan.next());
            customer.setPassword(scan.next());
        }
        else ;
        return customer;
    }

    /**
     * Decoding the Msg2 and storing information if the register or sign up successed
     * in isSuccess. storing the customer name in customer.
     * @param msg2 informing the client if registration and signing up is success.
     *             *always contain the name of the customer.
     *             “R/S,customerName,success/fail”
     *             e.g.: “R,Kiven,success” “S,Bob,fail”
     */

    public boolean DecodingMsg2(String msg2){
        boolean isSuccess;
        Scanner scan=new Scanner(msg2);
        scan.useDelimiter(",");
        flag=scan.next();
        Msg2Name=scan.next();
        if(scan.next().equals("success"))isSuccess=true;
        else isSuccess=false;
        return isSuccess;
    }

    /**
     *
     * @param Msg3 contain the info of the order the customer made:
     *             “orderID,orderCost,customerID,MM/DD/YYYY”
     *             e.g.: “123,30000,CU1234,10/02/2222”
     */

    public Order DecodingMsg3(String Msg3){
        Order order=new Order();
        Scanner scan=new Scanner(Msg3);
        scan.useDelimiter(",");
        SimpleDateFormat format=new SimpleDateFormat("MM/DD/yyyy");
        order.setOrderID(scan.next());
        order.setOrderCost(scan.nextDouble());
        order.setCustomerID(scan.next());
        try{
        order.setOrderTime(format.parse(scan.next()));}catch(Exception e){e.printStackTrace();}
        while(scan.hasNext()){
            OrderItem item=new OrderItem();
            item.setName(scan.next());
            item.setQuantiy(scan.nextInt());
            order.addItem(item);
        }
        return order;
    }

    /**
     *
     * @param msg4 contain the info if the order is available:
     *             “AA(all available)/PA(partially available)/NA(not available)”
     *             “PA, ”: “PA,[Orderitemname]”;
     *             e.g.: “AA” “PA” “NA”;
     * @param order see in Order Class
     */

    public Order DecodingMsg4(String msg4,Order order){
        String flag;
        OrderItemList items=new OrderItemList();
        Scanner scan=new Scanner(msg4);
        scan.useDelimiter(",");
        if((flag=scan.next()).equals("AA")){
            order.setOrderAvailable("All Available");
        }else if(flag.equals("NA")){
            order.setOrderAvailable("Not Available");
        }else if(flag.equals("PA")){
            order.setOrderAvailable("Partially Available");
            //TODO need PA msg4 structure.

            while(scan.hasNext()){
                String name=scan.next();
                int Quantity=scan.nextInt();
                OrderItem p=new OrderItem();
                p.setQuantiy(Quantity);
                p.setName(name);
                items.add(p);
            }
            order.setOrderItems(items);
        }
        return order;

    }

    public Order DecodingItemAvailable(String availableItem, Order order){
        OrderItemList itemList=new OrderItemList();
        Scanner scanner=new Scanner(availableItem);
        scanner.useDelimiter(",");
        if((flag=scanner.next()).equals("PA")){
            while(scanner.hasNext()){
                OrderItem item=new OrderItem();
                item.setName(scanner.next());
                item.setQuantiy(scanner.nextInt());
                itemList.add(item);
            }
            order.setOrderItems(itemList);
            order.setOrderAvailable("Partially Available");
        } else if(flag.equals("AA")){
            order.setOrderAvailable("All Available");
        } else if(flag.equals("NA"))
            order.setOrderAvailable("Not Available");
        else ;

        return order;
    }


}
