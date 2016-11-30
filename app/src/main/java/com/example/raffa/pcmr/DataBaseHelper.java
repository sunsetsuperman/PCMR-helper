package com.example.raffa.pcmr;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by raffa on 14/11/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.raffa.pcmr/databases/";

    private static String DB_NAME = "pcmrDB16.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }


    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public Cursor lerBD(String s, String objetivo){
        if(objetivo != "geral" && (s =="placa_mae" || s == "processador" || s == "placa_video" || s=="ram")){
            return myDataBase.query(s,null,"objetivo = \""+objetivo+"\"",null,null,null,null);
        }
        return myDataBase.query(s,null,null,null,null,null,null);
    }

    public Cursor lerTabelaListaPC(){
        return myDataBase.query("meu_pc",null,null,null,null,null,null);
    }

    public void inserePc(String nome,String processador, String placa_mae, String hd,String ssd, String ram, String gabinete, String placa_video, String fonte,String cooler_processador){
        myDataBase = this.getWritableDatabase();
        String s = "INSERT INTO meu_pc VALUES(\""+nome+"\",\""+processador+"\",\""+placa_mae+"\",\""+hd+"\",\""+ram+"\",\""+ssd+"\",\""+placa_video+"\",\""+fonte+"\",\""+gabinete+"\",\""+cooler_processador+"\")";
        myDataBase.execSQL(s);
    }

    public void deletarPc(String nome){
        myDataBase = this.getWritableDatabase();
        String s = "DELETE FROM meu_pc WHERE _id="+"\""+nome+"\";";
        myDataBase.execSQL(s);
    }
    public Cursor limitaSocket(String pecanome, String tabela, String pecaatual){
        Cursor c = myDataBase.query(tabela,null,"_id = \""+pecanome+"\"",null,null,null,null);
        c.moveToFirst();
        String sckt = c.getString(c.getColumnIndexOrThrow("socket"));
        return myDataBase.query(pecaatual,null,"socket = \""+sckt+"\"",null,null,null,null);
    }
    public Cursor limitaRAM(String pecanome, String tabela, String pecaatual){
        Cursor c = myDataBase.query(tabela,null,"_id = \""+pecanome+"\"",null,null,null,null);
        c.moveToFirst();
        String sckt = c.getString(c.getColumnIndexOrThrow("tipo_ram"));
        return myDataBase.query(pecaatual,null,"tipo_ram = \""+sckt+"\"",null,null,null,null);
    }
    public Cursor limitaVGA(String pecanome, String tabela, String pecaatual){
        Cursor c = myDataBase.query(tabela,null,"_id = \""+pecanome+"\"",null,null,null,null);
        c.moveToFirst();
        String sckt = c.getString(c.getColumnIndexOrThrow("socket_pv"));
        return myDataBase.query(pecaatual,null,"socket_pv = \""+sckt+"\"",null,null,null,null);
    }
    public Cursor limitaMBALL(String proc,String vga,String ram, String pecaatual){
        Cursor c = myDataBase.query("processador",null,"_id = \""+proc+"\"",null,null,null,null);
        c.moveToFirst();
        String sckt = c.getString(c.getColumnIndexOrThrow("socket"));
        c = myDataBase.query("ram",null,"_id = \""+ram+"\"",null,null,null,null);
        c.moveToFirst();
        String tipo = c.getString(c.getColumnIndexOrThrow("tipo_ram"));
        c = myDataBase.query("placa_video",null,"_id = \""+vga+"\"",null,null,null,null);
        c.moveToFirst();
        String socket_pv = c.getString(c.getColumnIndexOrThrow("socket_pv"));

        return myDataBase.query(pecaatual,null,"socket = \""+sckt+"\" AND tipo_ram = "+"\""+tipo+"\" AND " +
                "socket_pv = \""+socket_pv+"\"",null,null,null,null);
    }
    public Cursor limitaMBPV(String proc, String vga, String pecaatual){
        Cursor c = myDataBase.query("processador",null,"_id = \""+proc+"\"",null,null,null,null);
        c.moveToFirst();
        String sckt = c.getString(c.getColumnIndexOrThrow("socket"));
        c = myDataBase.query("placa_video",null,"_id = \""+vga+"\"",null,null,null,null);
        c.moveToFirst();
        String socket_pv = c.getString(c.getColumnIndexOrThrow("socket_pv"));
        return myDataBase.query(pecaatual,null,"socket = \""+sckt+"\" AND socket_pv = \""+socket_pv+"\"",null,null,null,null);
    }
    public Cursor limitaMBRV(String ram, String vga, String pecaatual){
        Cursor c = myDataBase.query("ram",null,"_id = \""+ram+"\"",null,null,null,null);
        c.moveToFirst();
        String tipo = c.getString(c.getColumnIndexOrThrow("tipo_ram"));
        c = myDataBase.query("placa_video",null,"_id = \""+vga+"\"",null,null,null,null);
        c.moveToFirst();
        String socket_pv = c.getString(c.getColumnIndexOrThrow("socket_pv"));
        return myDataBase.query(pecaatual,null,"tipo_ram = \""+tipo+"\" AND socket_pv = \""+socket_pv+"\"",null,null,null,null);
    }
    public Cursor limitaMBRP(String ram, String proc, String pecaatual){
        Cursor c = myDataBase.query("ram",null,"_id = \""+ram+"\"",null,null,null,null);
        c.moveToFirst();
        String tipo = c.getString(c.getColumnIndexOrThrow("tipo_ram"));
        c = myDataBase.query("processador",null,"_id = \""+proc+"\"",null,null,null,null);
        c.moveToFirst();
        String socket = c.getString(c.getColumnIndexOrThrow("socket"));
        return myDataBase.query(pecaatual,null,"tipo_ram = \""+tipo+"\" AND socket = \""+socket+"\"",null,null,null,null);
    }
}