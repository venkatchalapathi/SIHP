package com.example.sihp.Utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sihp.Activities.DrawerActivity;
import com.example.sihp.Models.ComPojo;
import com.example.sihp.TaskLoadedCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";
    ArrayList<ComPojo> list;

    public PointsParser(DrawerActivity mContext, String directionMode, ArrayList<ComPojo> list) {
        this.list = list;
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("mylog", "Executing routes");
            Log.d("mylog", routes.toString());

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {

            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                Log.i("locations:", lat + "" + lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }

        Log.i("size:", "" + points.size());

        ArrayList<ComPojo> mainlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            for (int j = 0; j < points.size(); j++) {


                String dbLat = String.valueOf(list.get(i).getLattitude()).substring(0,6);
                String dbLong = String.valueOf(list.get(i).getLongitude()).substring(0,6);


                Log.i("suB-->",""+points.get(j));

                //String dbLng = list.get(i).getLocation().substring()
                String mapLat = points.get(j).toString().substring(10, 16);
                Log.i("sub-->", dbLat + ":::" + mapLat);
                if (dbLat.equals(mapLat)) {

                    Log.i("matched-->", dbLat + ":::" + mapLat);
                    String problem = list.get(i).getProblem();
                    double lat = list.get(i).getLattitude();
                    double longi = list.get(i).getLongitude();

                    ComPojo pojo = new ComPojo(problem,lat,longi);
                    mainlist.add(pojo);
                    break;
                }
            }
        }
        Log.i("databaseList:", "" + list.size());
        Log.i("routeList:", "" + points.size());
        Log.i("mainList:", "" + mainlist.size());
        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions);

        } else {
            Log.d("mylog", "without Polylines drawn");
        }
        taskCallback.onCompareDone(mainlist);


    }
}
