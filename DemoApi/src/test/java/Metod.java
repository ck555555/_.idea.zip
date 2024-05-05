import com.jayway.jsonpath.DocumentContext;
import io.qameta.allure.Step;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class Metod {
    public String test1 = "Test1";

    @Step("Запись в Properties переменной {1} = {2}")
    public static void InputProp(String FileName, String NameProp, String Input) throws IOException {
        FileInputStream in = new FileInputStream(FileName);
        Properties props = new Properties();
        props.load(in);
        in.close();
        FileOutputStream out = new FileOutputStream(FileName);
        props.setProperty(NameProp, Input);
        props.store(out, null);
        out.close();
    }
    public static String ReadProp(String FileName, String NameProp) throws IOException {
        FileInputStream in = new FileInputStream(FileName);
        Properties props = new Properties();
        props.load(in);
        in.close();
        String Name = props.getProperty(NameProp);
        return Name;
    }


}

