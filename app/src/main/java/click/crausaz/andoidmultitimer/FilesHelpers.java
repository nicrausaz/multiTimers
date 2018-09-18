package click.crausaz.andoidmultitimer;

import android.content.Context;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilesHelpers {

    public Boolean createNewFile (Context app_context, String filename) throws IOException {
        return new File(app_context.getFilesDir(), filename).createNewFile();
    }
    public String getFileContentJSONString (Context app_context, String filename) throws FileNotFoundException, ParseException {
        return new JSONParser().parse(new FileReader(app_context.getFilesDir() + "/" + filename).toString()).toString();
    }
    public void writeJSONInFile (Context app_context, String filename, JSONObject json_object) throws IOException {
        FileWriter fileWriter = new FileWriter(app_context.getFilesDir() + "/" + filename);
        fileWriter.write(json_object.toString());
        fileWriter.close();
    }
}
