package me.dm7.barcodescanner.zxing.sample.controller;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.sample.model.ClientCompany;
import me.dm7.barcodescanner.zxing.sample.model.Course;
import me.dm7.barcodescanner.zxing.sample.model.ReservationMonth;
import me.dm7.barcodescanner.zxing.sample.model.Voucher;

/**
 * Created by CarlosEmilio on 28/09/2016.
 */

public class ClientController {
    private ArrayList<ClientCompany> clients;
    private ArrayList<Course> courses;
    private DataBaseController dbController;


    //===================================================
    //
    //     Create client model
    //
    //===================================================
    /**
     *
     * @param context
     */
    public ClientController(Context context) {
        setClients(new ArrayList<ClientCompany>());
        setCourses(new ArrayList<Course>());
        dbController = DataBaseController.getInstance(context);
    }


    /**
     *
     * @param ClientName
     * @param ClientCT
     * @return
     */
    public boolean addClient(int id, String ClientName, int ClientCT){
        ClientCompany client = new ClientCompany(ClientName, ClientCT);
        client.setIdCompany(id);
        if(!getClients().contains(client))
            getClients().add(client);
        else
            return false;
        return true;
    }


    /**
     * Get all elements in BD put in Array of Client Company
     */
    public void preloadClients(){
        Cursor cs = getDbController().getConnDataBase().query("CLIENT_COMPANY",null,null,null,null,null,null);

        if(cs.getCount() > 0) {
            cs.moveToFirst();

            do {
                int id = cs.getInt(1);
                String name = cs.getString(1);
                int ct = cs.getInt(2);
               addClient(id, name, ct);


            }while(cs.moveToNext());
        }


        cs.close();

    }


    //===================================================
    //
    //     Create Course model
    //
    //===================================================

    /**
     *
     * @param course
     * @return
     */
    public boolean addCourse(Course course){

        if(!getCourses().contains(course))
            getCourses().add(course);
        else
            return false;
        return true;
    }


    /**
     * Get all elements in BD put in Array of Client Company
     */
    public void preloadCourse(){
        Cursor cs = getDbController().getConnDataBase().query("COURSE",null,null,null,null,null,null);

        if(cs.getCount() > 0) {
            cs.moveToFirst();

            do {

                addCourse(createCourse(cs));

            }while(cs.moveToNext());
        }


        cs.close();

    }

    public Course createCourse(Cursor cs){
        int id = Integer.parseInt(cs.getString(0));
        String name = cs.getString(1);
        String date = cs.getString(2);

        Course course = new Course(name, date);
        course.setIdCourse(id);

        return course;
    }


    //===================================================
    //
    //     Create Voucher model
    //
    //===================================================

    public void preLoadVoucher(){
        Cursor cs = getDbController().getConnDataBase().query("VOUCHER",null,null,null,null,null,null);

        if(cs.getCount() > 0) {
            cs.moveToFirst();

            do {
                int idCourse = cs.getInt(1);
                int idClient = cs.getInt(2);
                int voucherN = Integer.parseInt(cs.getString(3));
                int status = Integer.parseInt(cs.getString(4));
                int extra = cs.getInt(5);
                addVoucher( idCourse, idClient, voucherN, status, extra);

            }while(cs.moveToNext());
        }


        cs.close();
    }




    private void addVoucher(int idCourse, int idClient, int voucherN, int status, int extra) {
        Cursor cs = getDbController().getConnDataBase().query("COURSE",null,idCourse + " = _id",null,null,null,null);

        Course link = createCourse(cs);

        Voucher voucher = new Voucher(voucherN, false, link);
        //change int to boolean
        voucher.setExtra(extra != 0);
        voucher.setChecked(status != 0);

        for (int i = 0; i < getClients().size(); i++) {
            if(getClients().get(i).getIdCompany() == idClient){
                int key = Integer.parseInt(link.getDateCourse());
                if(!getClients().get(i).getListOfReservation().containsKey(key)) {
                    getClients().get(i).getListOfReservation().put(key, new ReservationMonth(key));
                    getClients().get(i).getListOfReservation().get(key).getReservedCourses().add(voucher);
                }
                else {
                    getClients().get(i).getListOfReservation().get(key).getReservedCourses().add(voucher);
                }
            }
        }
    }

    /**
     * make all preload
     * @param context of create connection
     * @return object copy of
     */
    public void loadClientController(Context context){

        this.preloadClients();
        this.preloadCourse();
        this.preLoadVoucher();


    }


    public DataBaseController getDbController() {
        return dbController;
    }


    public ArrayList<ClientCompany> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ClientCompany> clients) {
        this.clients = clients;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
