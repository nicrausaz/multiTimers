package click.crausaz.andoidmultitimer;

import org.json.JSONException;
import org.json.JSONObject;

public class Timer{
    public String name;
    public String full_time;
    public String actual_time;

    public Timer (String name, String full_time, String actual_time) {
        this.name = name;
        this.full_time = full_time;
        this.actual_time = actual_time;
    }

    public JSONObject toJSONObject () throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", this.name);
        obj.put("full_time", this.full_time);
        obj.put("actual_time", this.actual_time);
        return obj;
    }
}
