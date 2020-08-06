package com.uisrael.redsocialuisrael;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
    import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ServicioTask extends AsyncTask<Void, Void, String> {

    private Context httpContext;//contexto
    ProgressDialog progressDialog;//dialogo cargando
    private String resultadoapi="";
    private String linkrequestAPI="";//link  para consumir el servicio rest
    private String email="";
    private String nombre="";
    private String apellidos="";
    private String password="";


    public ServicioTask(Context ctx, String linkAPI, String email, String  nombre, String apellidos, String password ){
        this.httpContext=ctx;
        this.linkrequestAPI=linkAPI;
        this.email=email;
        this.nombre=nombre;
        this.apellidos= apellidos;
        this.password=password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(httpContext, "Procesando Solicitud", "por favor, espere");
    }

    @Override
    protected String doInBackground(Void... params) {
        //Declaracion de variables
        String result= null;
        String wsURL = linkrequestAPI;//webservice
        URL url = null;
        try {
            // se crea la conexion al api:
            url = new URL(wsURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //crear el objeto json para enviar por POST
            JSONObject parametrosPost= new JSONObject();
            parametrosPost.put("email",email);
            parametrosPost.put("nombre",nombre);
            parametrosPost.put("apellidos",apellidos);
            parametrosPost.put("password",password);

            //DEFINIR PARAMETROS DE CONEXION
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");// se puede cambiar por delete ,put ,etc
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);//insert into WS
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", "" );
            urlConnection.setUseCaches (false);


            //OBTENER EL RESULTADO DEL REQUEST
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));//UTF-8
            writer.write(getPostDataString(parametrosPost));
            writer.flush(); // UTF8-
            writer.close();//
            os.close();//CONEXION

            int responseCode=urlConnection.getResponseCode();// conexion OK?
            if(responseCode== HttpURLConnection.HTTP_OK){//ERROR
                BufferedReader in= new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuffer sb= new StringBuffer("");
                String linea="";
                while ((linea=in.readLine())!= null){
                    sb.append(linea);
                    break;
                }
                in.close();
                result= sb.toString();
            }
            else{
                result= new String("Error: "+ responseCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        resultadoapi=s;
        Toast.makeText(httpContext,resultadoapi,Toast.LENGTH_LONG).show();//mostrara una notificacion con el resultado del request

    }

    //FUNCIONES----------------------------------------------------------------------
    //Transformar JSON Obejct a String *******************************************
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){//HOLA

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}
